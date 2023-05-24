package service;


import entity.ConversationEntry;
import entity.ConversationItem;

import javax.annotation.PreDestroy;
import javax.ejb.Local;

@Local
public interface ChatService {
    public ConversationItem fetchAnswer(String username, String cmd);
    public ConversationEntry cancelChat(Integer grade) throws Exception;
    //public void gradeChat(Integer id, Integer grade);
}
