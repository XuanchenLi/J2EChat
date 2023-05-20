package utils;

import com.alibaba.fastjson.JSONObject;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


/**
 * @ClassName:JSonUtil
 * @Description: TODO
 * @Author:Dazz1e
 * @Date:2023/5/20 下午 10:17
 * Version V1.0
 */
public class JSonUtil {
    public static JSONObject getSomething(HttpServletRequest request)
    {
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
            StringBuffer sb = new StringBuffer("");
            String temp;
            while((temp = br.readLine()) != null)
            {
                sb.append(temp);
            }
            br.close();
            //获取JSON对象
            JSONObject jsonRet = JSONObject.parseObject(sb.toString());
            return jsonRet;
        }
        catch(UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch(IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
