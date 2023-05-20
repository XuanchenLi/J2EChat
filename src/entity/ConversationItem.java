package entity;

import lombok.Data;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName:ConversationItem
 * @Description: TODO
 * @Author:Dazz1e
 * @Date:2023/5/20 下午 8:43
 * Version V1.0
 */
@Entity
@Table(name="conv_item")
@Data
public class ConversationItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String from;
    private String to;
    private String content;
    private Date time;

}
