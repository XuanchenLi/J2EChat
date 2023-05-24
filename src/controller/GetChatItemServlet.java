package controller;

import com.alibaba.fastjson.JSONObject;
import controller.response.ErrorResult;
import controller.response.Result;
import entity.ConversationEntry;
import entity.ConversationItem;
import service.ChatHistoryService;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet(name = "GetChatItemServlet")
public class GetChatItemServlet extends HttpServlet {
    @EJB
    private ChatHistoryService historyService;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        Writer out = response.getWriter();
        try {
            String cIds = request.getParameter("cId");
            Integer cId = Integer.parseInt(cIds);
            List<ConversationItem> res = historyService.getChatItems(cId);
            if (res == null) {
                throw  new Exception("Not found");
            }
            Result<List<ConversationItem>> ret = Result.success(res);
            String js = JSONObject.toJSONString(ret);
            out.write(js);
        }catch (Exception e) {
            e.printStackTrace();
            //报错返回
            ErrorResult res = ErrorResult.error("获取对话记录失败");
            String js = JSONObject.toJSONString(res);
            out.write(js);
            return;
        }finally {
            out.flush();
            out.close();
        }
    }
}
