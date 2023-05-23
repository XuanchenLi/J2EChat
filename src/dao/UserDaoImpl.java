package dao;

import entity.User;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.*;
import javax.transaction.*;
import javax.transaction.RollbackException;
import java.util.Iterator;
import java.util.List;

/**
 * @ClassName:UserDao
 * @Description: TODO
 * @Author:Dazz1e
 * @Date:2023/5/20 下午 9:00
 * Version V1.0
 */
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
public class UserDaoImpl implements UserDao{
    @PersistenceContext(unitName = "J2EChat")
    private EntityManager manager;
    @Resource
    private UserTransaction userTransaction;
    public UserDaoImpl() {}
    @Override
    public User findUserByName(String name) {
        User results = null;
        try {
            userTransaction.begin();
            Query query = manager.createNativeQuery(
                    "select * from user where name = ?", User.class
            );
            query.setParameter(1, name);
            query.getSingleResult();
            try {
                results = (User) query.getSingleResult();
            }catch (NoResultException e) {
                userTransaction.commit();
                return null;
            }
            userTransaction.commit();
            return results;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
    @Override
    public boolean addUser(User u) {
        try {
            userTransaction.begin();
            Query query = manager.createNativeQuery(
                    "select * from user where name = ?", User.class
            );
            query.setParameter(1, u.getName());
            try {
                User results = (User) query.getSingleResult();
                userTransaction.commit();
                return false;
            }catch (NoResultException e) {

            }
            manager.persist(u);
            userTransaction.commit();
            return true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateUser(User u) {
        try {
            userTransaction.begin();
            Query query = manager.createNativeQuery(
                    "select * from user where name = ?", User.class
            );
            query.setParameter(1, u.getName());
            User result = null;
            try {
                result = (User) query.getSingleResult();
            }catch (NoResultException e) {
                userTransaction.commit();
                return false;
            }
            result.setPassword(u.getPassword());
            result.setEmail(u.getEmail());
            result.setGender(u.getGender());
            manager.merge(result);
            userTransaction.commit();
            return true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
