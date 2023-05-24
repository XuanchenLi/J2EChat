package service;


import entity.ConversationEntry;
import entity.ConversationItem;

import javax.ejb.Local;
import java.util.List;

@Local
public interface ChatHistoryService {
    public List<ConversationEntry> getChatEntries(Integer uId);
    public List<ConversationItem> getChatItems(Integer cId);
}
