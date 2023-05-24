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

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

@WebServlet(name = "SendMessageServlet")
public class SendMessageServlet extends HttpServlet {
    private final String CHAT_SESSION_KEY = "CHAT_KEY";
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        Writer out = response.getWriter();
        ChatService chatService = (ChatService) request.getSession().getAttribute(CHAT_SESSION_KEY);
        if (chatService == null) {
            //新对话
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
        ConversationItem retItem = chatService.fetchAnswer(pack.getName(), pack.getMsg());
        if (retItem != null) {
            Result<ConversationItem> res = Result.success(retItem);
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
