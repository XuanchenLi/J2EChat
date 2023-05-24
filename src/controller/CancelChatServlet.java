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

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

@WebServlet(name = "CancelChatServlet")
public class CancelChatServlet extends HttpServlet {
    private final String CHAT_SESSION_KEY = "CHAT_KEY";
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        Writer out = response.getWriter();
        ChatService chatService = (ChatService) request.getSession().getAttribute(CHAT_SESSION_KEY);
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
            ConversationEntry ret = chatService.cancelChat(grade.getGrade());
            ret.setGrade(grade.getGrade());
            Result<ConversationEntry> res = Result.success(ret);
            String js = JSONObject.toJSONString(res);
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
