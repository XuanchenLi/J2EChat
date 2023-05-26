package service;


import entity.ConversationEntry;
import entity.ConversationItem;

import javax.annotation.PreDestroy;
import javax.ejb.Local;
import java.util.List;

@Local
public interface ChatService {
    public ConversationItem fetchAnswer(Integer uId,String username, String cmd);
    public ConversationEntry cancelChat(Integer uId, Integer grade) throws Exception;
    public ConversationEntry getEntryItems();
    //public void gradeChat(Integer id, Integer grade);
}
