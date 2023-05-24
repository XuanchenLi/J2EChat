package dao;


import entity.ConversationEntry;

import javax.ejb.Local;
import java.util.List;

@Local
public interface ConversationEntryDao {
    public Integer persistEntry(ConversationEntry e);
    public boolean updateGrade(Integer id, Integer grade);
    public List<ConversationEntry> findEntriesByUserId(Integer id);
}
