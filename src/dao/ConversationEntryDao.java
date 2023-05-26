package dao;


import entity.ConversationEntry;

import javax.ejb.Local;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@Local
public interface ConversationEntryDao {
    public Integer persistEntry(ConversationEntry e);
    public boolean updateGrade(Integer id, Integer grade);
    public boolean updateOwner(Integer id, Integer uId);
    public List<ConversationEntry> findEntriesByUserId(Integer id);
}
