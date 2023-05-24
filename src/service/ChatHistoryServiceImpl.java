package service;

import dao.ConversationEntryDao;
import dao.ConversationItemDao;
import entity.ConversationEntry;
import entity.ConversationItem;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.List;

/**
 * @ClassName:ChatHistoryServiceImpl
 * @Description: TODO
 * @Author:Dazz1e
 * @Date:2023/5/24 上午 8:23
 * Version V1.0
 */
@Stateless
@LocalBean
public class ChatHistoryServiceImpl implements ChatHistoryService{
    @EJB
    private ConversationEntryDao entryDao;
    @EJB
    private ConversationItemDao itemDao;
    @Override
    public List<ConversationEntry> getChatEntries(Integer uId) {
        return entryDao.findEntriesByUserId(uId);
    }

    @Override
    public List<ConversationItem> getChatItems(Integer cId) {
        return itemDao.findItemsByConvId(cId);
    }
}
