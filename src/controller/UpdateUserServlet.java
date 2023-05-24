package controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import controller.response.ErrorResult;
import controller.response.Result;
import entity.User;
import service.UserService;
import utils.JSonUtil;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "UpdateUserServlet")
public class UpdateUserServlet extends HttpServlet {
    @EJB
   private UserService userService;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        JSONObject json = JSonUtil.getSomething(request);
        User user = JSON.toJavaObject(json, User.class);
        PrintWriter out = response.getWriter();
        User res = userService.updateUserInfo(user);
        if (res != null) {
            Result<User> ret = Result.success(res);
            String js = JSONObject.toJSONString(ret);
            out.write(js);
        }else {
            ErrorResult ret = ErrorResult.error("保存失败");
            String js = JSONObject.toJSONString(ret);
            out.write(js);
        }
        out.flush();
        out.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
