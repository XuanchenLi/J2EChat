package controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import controller.response.ErrorResult;
import controller.response.Result;
import entity.User;
import service.LoginService;
import utils.JSonUtil;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "ForgetServlet")
public class ForgetServlet extends HttpServlet {
    @EJB
    private LoginService loginService;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        JSONObject json = JSonUtil.getSomething(request);
        User user = JSON.toJavaObject(json, User.class);
        PrintWriter out = response.getWriter();
        if (loginService.changePSW(user)) {
            Result res = Result.success();
            String js = JSONObject.toJSONString(res);
            out.write(js);

        }else {
            ErrorResult res = ErrorResult.error("用户不存在或邮箱错误");
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
