package service.MDB;

import dao.ConversationEntryDao;
import entity.ConversationEntry;
import entity.GradeMsg;
import entity.GradePack;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * @ClassName:GradeMsgListener
 * @Description: TODO
 * @Author:Dazz1e
 * @Date:2023/5/23 下午 10:23
 * Version V1.0
 */
@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
                @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/J2EChat")
        }
)
public class GradeMsgListener implements MessageListener {
    @Resource
    private MessageDrivenContext mdc;
    @EJB
    ConversationEntryDao entryDao;

    @Override
    public void onMessage(Message message) {
        ObjectMessage msg = null;
        try {
            if (message instanceof ObjectMessage) {
                msg = (ObjectMessage) message;
                GradeMsg pack = (GradeMsg)msg.getObject();
                if (!entryDao.updateGrade(pack.getId(), pack.getGrade())) {
                    throw new Exception("更新失败");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mdc.setRollbackOnly();
        }
    }
}
