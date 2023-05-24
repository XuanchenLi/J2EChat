package dao;

import entity.ConversationItem;

import javax.ejb.Local;
import java.util.List;

@Local
public interface ConversationItemDao {
    public List<ConversationItem> findItemsByConvId(Integer cId);
}
