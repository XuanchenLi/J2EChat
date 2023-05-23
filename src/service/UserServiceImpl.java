package service;

import dao.UserDao;
import entity.User;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * @ClassName:UserServiceImpl
 * @Description: TODO
 * @Author:Dazz1e
 * @Date:2023/5/23 下午 7:42
 * Version V1.0
 */
@Stateless
@LocalBean
public class UserServiceImpl implements UserService{
    @EJB
    private UserDao dao;

    @Override
    public User updateUserInfo(User u) {
        try {
            User eUser = dao.findUserByName(u.getName());
            if (eUser == null) return null;
            eUser.setGender(u.getGender());
            eUser.setEmail(u.getEmail());
            if (dao.updateUser(eUser)) {
                return eUser;
            }else {
                return null;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
