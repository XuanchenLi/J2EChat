package dao;

import entity.User;

import javax.ejb.Local;

@Local
public interface UserDao {
    public User findUserByName(String name);
    public boolean addUser(User u);
}
