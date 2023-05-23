package service;


import entity.User;

import javax.ejb.Local;

@Local
public interface LoginService {
    public boolean register(User u);
    public boolean changePSW(User u);
    public User login(User u);
}
