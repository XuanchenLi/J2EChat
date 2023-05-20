package service;

import dao.UserDao;
import dao.UserDaoImpl;
import entity.User;
import utils.CryptUtil;

import javax.ejb.*;

/**
 * @ClassName:LoginService
 * @Description: TODO
 * @Author:Dazz1e
 * @Date:2023/5/20 下午 10:33
 * Version V1.0
 */
@Stateless
@LocalBean
public class LoginServiceImpl implements LoginService{
    @EJB
    private UserDao dao;
    public boolean register(User u) {
        try {
            u.setPassword(CryptUtil.getSHA(u.getPassword()));
            return dao.addUser(u);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
