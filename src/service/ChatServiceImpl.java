package service;

import com.alibaba.fastjson.JSONObject;
import dao.ConversationEntryDao;
import entity.ConversationEntry;
import entity.ConversationItem;
import entity.ExprObject;
import entity.GradeMsg;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.StatefulTimeout;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName:ChatServiceImpl
 * @Description: TODO
 * @Author:Dazz1e
 * @Date:2023/5/23 下午 7:55
 * Version V1.0
 */
@Stateful
@StatefulTimeout(unit = TimeUnit.MINUTES, value = 20)
public class ChatServiceImpl implements ChatService{
    private ConversationEntry chatEntry;
    private final String API_PATH = "http://localhost:8089/J2EChatWS/api/Calculator/";
    private final String AI_NAME = "ChatJ2E";
    //
    private static final String DEFAULT_CONNECTION_FACTORY = "java:/ConnectionFactory";
    private static final String DEFAULT_DESTINATION = "java:/queue/J2EChat";

    private static final String DEFAULT_USERNAME = "test";
    private static final String DEFAULT_PASSWORD = "zkxaihxb2";
    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String PROVIDER_URL = "remote://localhost:4447";
    //
    @EJB
    private ConversationEntryDao entryDao;
    private Integer grade = 0;
    private Integer uId;
    @PostConstruct
    private void initChat() {
        chatEntry = new ConversationEntry();
    }

    @PreDestroy
    private void gradeChat() throws NamingException, JMSException {
        //
        if (this.uId == null) return;
        Context context=null;
        Connection connection=null;
        try {
            // 设置JNDI
//            final Properties env = new Properties();
//            env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);// 初始化Context的工厂类
//            env.put(Context.PROVIDER_URL,  PROVIDER_URL);// Context服务提供者的URL
//            env.put(Context.SECURITY_PRINCIPAL, DEFAULT_USERNAME);
//            env.put(Context.SECURITY_CREDENTIALS, DEFAULT_PASSWORD);//应用用户的登录名,密码
            // 初始化上下文
            context = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup(DEFAULT_CONNECTION_FACTORY);
            Destination destination = (Destination) context.lookup(DEFAULT_DESTINATION);
            // 创建JMS连接、会话、消费者
            connection = connectionFactory.createConnection(DEFAULT_USERNAME, DEFAULT_PASSWORD);
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(destination);
            connection.start();

            // 发送特定数目的消息
            ObjectMessage message = null;

            GradeMsg msg = new GradeMsg();
            if (chatEntry.getId() == 0) {
                Integer nId = entryDao.persistEntry(chatEntry);
                if (nId == null) throw new Exception();
                chatEntry.setId(nId);
            }
            msg.setUId(this.uId);
            msg.setId(chatEntry.getId());
            msg.setGrade(grade);
            message = session.createObjectMessage(msg);
            producer.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (context != null) {
                context.close();
            }
            // 关闭连接负责会话,生产商和消费者
            if (connection != null) {
                connection.close();
            }
        }
    }

    private ConversationItem getItem(String from, String to, String msg) {
        ConversationItem item = new ConversationItem();
        item.setContent(msg);
        item.setFrom(from);
        item.setTo(to);
        Date date = new Date();
        item.setTime(date);
        return item;
    }

    @Override
    public ConversationEntry getEntryItems() {
        return chatEntry;
    }

    @Override
    public ConversationItem fetchAnswer(Integer uId, String username, String cmd) {
        ConversationItem item = null;
        ConversationItem fItem = getItem(username, AI_NAME, cmd);
        this.uId = uId;
        try {
            String sb = API_PATH;
            //sb = URLEncoder.encode(sb, "UTF-8");
            ExprObject obj = new ExprObject();
            obj.setExpr(cmd);
            String jsonInputString = JSONObject.toJSONString(obj);
            URL url = new URL(sb);
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST"); // 参数必须大写
            connect.setRequestProperty("Content-Type","application/json");
            connect.setDoOutput(true);
            try(OutputStream os = connect.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connect.getResponseCode();
            StringBuffer response = new StringBuffer();
            if (responseCode == HttpURLConnection.HTTP_OK) { //success
                try(BufferedReader in = new BufferedReader(new InputStreamReader(
                        connect.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        inputLine = new String(inputLine.getBytes(),"UTF-8");
                        response.append(inputLine);
                    }

                }
            } else {
                System.out.println("POST request not worked");
                throw new Exception("连接异常");
            }

            connect.disconnect();

            if (chatEntry.getConversations().isEmpty()) {
                chatEntry.setBrief(cmd);
            }

            chatEntry.addItem(fItem);
            item = getItem(AI_NAME, username, response.toString());
            chatEntry.addItem(item);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

//    @Override
//    @Remove
//    public void gradeChat(Integer id, Integer grade) {
//        chatEntry.setGrade(grade);
//    }


    @Override
    @Remove
    public ConversationEntry cancelChat(Integer uId, Integer grade) throws Exception {
        this.uId = uId;
        Integer nId = entryDao.persistEntry(chatEntry);
        if (nId == null) throw new Exception();
        chatEntry.setId(nId);
        if (!entryDao.updateOwner(nId, uId)) {
            throw new Exception("保存失败");
        }
        this.grade = grade;
        ConversationEntry res = chatEntry;
        res.setConversations(null);
        return res;
    }
}
