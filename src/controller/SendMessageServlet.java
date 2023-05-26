package controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import controller.response.ErrorResult;
import controller.response.Result;
import entity.ConversationItem;
import entity.QuestionPack;
import entity.User;
import service.ChatService;
import utils.JSonUtil;
import utils.SessionContext;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.Writer;

@WebServlet(name = "SendMessageServlet")
public class SendMessageServlet extends HttpServlet {
    private final String CHAT_SESSION_KEY = "CHAT_KEY";
    private final String SESSION_COOKIE_KEY = "JSESSIONID";
    private SessionContext context = SessionContext.getInstance();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Credentials","true");
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        response.setHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, PATCH");
        Writer out = response.getWriter();
        Cookie[] cookies = request.getCookies();
        String sessionId = null;
        if (cookies != null) {
            //从中取出cookie
            for (int i = 0; i < cookies.length; i++) {
                //依次取出
                Cookie temp = cookies[i];
                //System.out.println(temp.getValue());
                //判断一下
                if (temp.getName().equals(SESSION_COOKIE_KEY)) {
                    sessionId = temp.getValue();
                    break;
                }
            }
        }
        //System.out.println(sessionId);
        ChatService chatService = null;
        if (sessionId != null) {
            HttpSession se = context.getSession(sessionId);
            if (se != null)
                chatService = (ChatService)se.getAttribute(CHAT_SESSION_KEY);
        }
//        if (cookies != null) {
//            //从中取出cookie
//            for (int i = 0; i < cookies.length; i++) {
//                //依次取出
//                Cookie temp = cookies[i];
//                System.out.println(temp.getValue());
//                //判断一下
//                if (temp.getName().equals(SESSION_COOKIE_KEY)) {
//                    sessionId = temp.getValue();
//                    break;
//                }
//            }
//        }
        if (chatService == null) {
            //新对话
            System.out.println("new s");
            try {
                InitialContext ctx = new InitialContext();
                final String appName = "";
                final String moduleName = "J2EChat";
                final String distinctName = "";
                chatService = (ChatService) ctx.lookup(
                        "ejb:" + appName + "/" + moduleName + "/" + distinctName
                                +  "/ChatServiceImpl!service.ChatService?stateful"
                );
                request.getSession().setAttribute(CHAT_SESSION_KEY, chatService);

                context.addSession(request.getSession());
            }catch (Exception e) {
                e.printStackTrace();
                //报错返回
                ErrorResult res = ErrorResult.error("创建对话失败");
                String js = JSONObject.toJSONString(res);
                out.write(js);
                out.flush();
                out.close();
                return;
            }
        }

        //s
        JSONObject json = JSonUtil.getSomething(request);
        QuestionPack pack = JSON.toJavaObject(json, QuestionPack.class);
        ConversationItem retItem = chatService.fetchAnswer(pack.getUId(),pack.getName(), pack.getMsg());
        if (retItem != null) {
            Result<ConversationItem> res = Result.success(retItem);
            res.setMessage(chatService.toString());
            String js = JSONObject.toJSONString(res);
            out.write(js);
        }else {
            ErrorResult res = ErrorResult.error("连接AI失败");
            String js = JSONObject.toJSONString(res);
            out.write(js);
        }
        out.flush();
        out.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
