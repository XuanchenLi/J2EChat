package controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import controller.response.ErrorResult;
import controller.response.Result;
import entity.ConversationEntry;
import entity.GradePack;
import entity.User;
import service.ChatService;
import utils.JSonUtil;
import utils.SessionContext;

import javax.jms.Session;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.Writer;

@WebServlet(name = "CancelChatServlet")
public class CancelChatServlet extends HttpServlet {
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
        System.out.println(sessionId);
        ChatService chatService = null;
        if (sessionId != null) {
            HttpSession se = context.getSession(sessionId);
            if (se != null)
                chatService = (ChatService)se.getAttribute(CHAT_SESSION_KEY);
        }
        if (chatService == null) {
            ErrorResult res = ErrorResult.error("不存在活跃会话");
            String js = JSONObject.toJSONString(res);
            out.write(js);
            out.flush();
            out.close();
            return;
        }
        JSONObject json = JSonUtil.getSomething(request);
        GradePack grade = JSON.toJavaObject(json, GradePack.class);
//        chatService.gradeChat(grade.getGrade());
        try {
            ConversationEntry ret = chatService.cancelChat(grade.getUId(), grade.getGrade());
            ret.setGrade(grade.getGrade());
            Result<ConversationEntry> res = Result.success(ret);
            String js = JSONObject.toJSONString(res);
            request.getSession().removeAttribute(CHAT_SESSION_KEY);
            context.delSession(request.getSession());
            out.write(js);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            ErrorResult res = ErrorResult.error("关闭会话失败");
            String js = JSONObject.toJSONString(res);
            out.write(js);
            out.flush();
            out.close();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
