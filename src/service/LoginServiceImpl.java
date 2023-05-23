package service;

import dao.UserDao;
import dao.UserDaoImpl;
import entity.User;
import utils.CryptUtil;

import javax.ejb.*;
import java.util.Objects;

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
    @Override
    public boolean register(User u) {
        try {
            u.setPassword(CryptUtil.getSHA(u.getPassword()));
            return dao.addUser(u);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean changePSW(User u) {
        try {
            User existUser = dao.findUserByName(u.getName());
            if (existUser != null) {
                if (Objects.equals(u.getEmail(), existUser.getEmail())) {
                    existUser.setPassword(CryptUtil.getSHA(u.getPassword()));
                    return dao.updateUser(existUser);
                }else {
                    return false;
                }
            }else {
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public User login(User u) {
        try {
            User existUser = dao.findUserByName(u.getName());
            if (existUser != null) {
                if (Objects.equals(CryptUtil.getSHA(u.getPassword()), existUser.getPassword())) {
                    existUser.setPassword(u.getPassword());
                    return existUser;
                }else {
                    return null;
                }
            }else {
                return null;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
