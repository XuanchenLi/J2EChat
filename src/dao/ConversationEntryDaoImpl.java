package dao;

import entity.ConversationEntry;
import entity.User;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.UserTransaction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @ClassName:ConversationEntryDaoImpl
 * @Description: TODO
 * @Author:Dazz1e
 * @Date:2023/5/23 下午 8:46
 * Version V1.0
 */
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
public class ConversationEntryDaoImpl implements ConversationEntryDao{
    @PersistenceContext(unitName = "J2EChat")
    private EntityManager manager;
    @Resource
    private UserTransaction userTransaction;

    @Override
    public Integer persistEntry(ConversationEntry entry) {
        try {
            userTransaction.begin();
            manager.persist(entry);
            userTransaction.commit();
            manager.flush();
            return entry.getId();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateGrade(Integer id, Integer grade) {
        try {
            userTransaction.begin();
            Query query = manager.createNativeQuery(
                    "select * from conv_entry where id = ?", ConversationEntry.class
            );
            query.setParameter(1, id);
            ConversationEntry result = null;
            try {
                result = (ConversationEntry) query.getSingleResult();
            }catch (NoResultException e) {
                userTransaction.commit();
                return false;
            }
            result.setGrade(grade);
            manager.merge(result);
            userTransaction.commit();
            return true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<ConversationEntry> findEntriesByUserId(Integer id) {
        List<ConversationEntry> res = null;

        try {
            userTransaction.begin();
            Query query = manager.createNativeQuery(
                    "select * from conv_entry where user_id = ? order by id desc ", ConversationEntry.class
            );
            query.setParameter(1, id);
            List results=query.getResultList();
            userTransaction.commit();
            Iterator it= results.iterator();
            res = new ArrayList<>();
            while(it.hasNext())
            {
                ConversationEntry p=(ConversationEntry)it.next();
                p.setConversations(null);
                res.add(p);
            }
            return  res;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
