package dao;

import entity.ConversationEntry;
import entity.ConversationItem;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.UserTransaction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @ClassName:ConversationItemDaoImpl
 * @Description: TODO
 * @Author:Dazz1e
 * @Date:2023/5/24 上午 8:28
 * Version V1.0
 */
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
public class ConversationItemDaoImpl implements ConversationItemDao{
    @PersistenceContext(unitName = "J2EChat")
    private EntityManager manager;
    @Resource
    private UserTransaction userTransaction;

    @Override
    public List<ConversationItem> findItemsByConvId(Integer cId) {
        List<ConversationItem> res = null;
        try {
            userTransaction.begin();
            Query query = manager.createNativeQuery(
                    "select * from conv_item where conv_id = ? order by `time` asc ", ConversationItem.class
            );
            query.setParameter(1, cId);
            List results=query.getResultList();
            userTransaction.commit();
            Iterator it= results.iterator();
            res = new ArrayList<>();
            while(it.hasNext())
            {
                ConversationItem p=(ConversationItem)it.next();
                res.add(p);
            }
            return  res;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
