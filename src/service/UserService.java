package service;


import entity.User;

import javax.ejb.Local;

@Local
public interface UserService {
    public User updateUserInfo(User u);
}
