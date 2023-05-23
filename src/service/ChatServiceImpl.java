package service;

import javax.ejb.Stateful;
import javax.ejb.StatefulTimeout;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName:ChatServiceImpl
 * @Description: TODO
 * @Author:Dazz1e
 * @Date:2023/5/23 下午 7:55
 * Version V1.0
 */
@Stateful
@StatefulTimeout(unit = TimeUnit.MINUTES, value = 20)
public class ChatServiceImpl implements ChatService{
}
