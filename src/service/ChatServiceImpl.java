package service;

import dao.ConversationEntryDao;
import entity.ConversationEntry;
import entity.ConversationItem;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private final String AI_NAME = "AI";
    //
    private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String DEFAULT_DESTINATION = "jms/queue/J2EChat";
    private static final String DEFAULT_MESSAGE_COUNT = "1";

    private static final String DEFAULT_USERNAME = "test";
    private static final String DEFAULT_PASSWORD = "zkxaihxb2";
    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String PROVIDER_URL = "remote://localhost:4447";
    //
    @EJB
    private ConversationEntryDao entryDao;
    private Integer grade = 0;

    @PostConstruct
    private void initChat() {
        chatEntry = new ConversationEntry();
    }

    @PreDestroy
    private void gradeChat() throws NamingException, JMSException {
        //
        Context context=null;
        Connection connection=null;
        try {
            // 设置JNDI
            System.out.println("设置JNDI访问环境信息也就是设置应用服务器的上下文信息!");
            final Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);// 初始化Context的工厂类
            env.put(Context.PROVIDER_URL,  PROVIDER_URL);// Context服务提供者的URL
            env.put(Context.SECURITY_PRINCIPAL, DEFAULT_USERNAME);
            env.put(Context.SECURITY_CREDENTIALS, DEFAULT_PASSWORD);//应用用户的登录名,密码
            // 初始化上下文
            context = new InitialContext(env);
            System.out.println ("获取连接工厂！");
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup(DEFAULT_CONNECTION_FACTORY);
            System.out.println ("获取目的地!");
            Destination destination = (Destination) context.lookup(DEFAULT_DESTINATION);

            // 创建JMS连接、会话、生产者和消费者
            connection = connectionFactory.createConnection(DEFAULT_USERNAME, DEFAULT_PASSWORD);
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(destination);
            connection.start();

            // 发送特定数目的消息
            ObjectMessage message = null;
            String opt = "";

            GradeMsg msg = new GradeMsg();
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
    public ConversationItem fetchAnswer(String username, String cmd) {
        ConversationItem item = null;
        ConversationItem fItem = getItem(username, AI_NAME, cmd);
        try {
            String sb = API_PATH + cmd;
            URL url = new URL(sb);
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("GET"); // 参数必须大写
            connect.connect();
            InputStream isString = connect.getInputStream();

            BufferedReader isRead = new BufferedReader(new InputStreamReader(isString));
            StringBuilder res = new StringBuilder();
            String str = "";
            while ((str = isRead.readLine()) != null) {
                str = new String(str.getBytes(),"UTF-8"); //解决中文乱码问题
//          System.out.println("文件解析打印：");
//          System.out.println(str);
                res.append(str);
            }
            isRead.close();
            isString.close();
            connect.disconnect();

            if (chatEntry.getConversations().isEmpty()) {
                chatEntry.setBrief(cmd);
            }

            chatEntry.addItem(fItem);
            item = getItem(AI_NAME, username, res.toString());
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
    public ConversationEntry cancelChat(Integer grade) throws Exception {
        Integer nId = entryDao.persistEntry(chatEntry);
        if (nId == null) throw new Exception();
        chatEntry.setId(nId);
        this.grade = grade;
        ConversationEntry res = chatEntry;
        res.setConversations(null);
        return res;
    }
}
