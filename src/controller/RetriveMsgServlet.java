package controller;

import com.alibaba.fastjson.JSONObject;
import controller.response.ErrorResult;
import controller.response.Result;
import entity.ConversationEntry;
import entity.ConversationItem;
import service.ChatService;
import utils.SessionContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

@WebServlet(name = "RetriveMsgServlet")
public class RetriveMsgServlet extends HttpServlet {
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
        if (sessionId == null || chatService == null) {
            ErrorResult res = ErrorResult.error("");
            String js = JSONObject.toJSONString(res);
            out.write(js);
            out.flush();
            out.close();
        }
        if (chatService != null) {
            Result<ConversationEntry> ls = Result.success(chatService.getEntryItems());
            String js = JSONObject.toJSONString(ls);
            out.write(js);
            out.flush();
            out.close();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
