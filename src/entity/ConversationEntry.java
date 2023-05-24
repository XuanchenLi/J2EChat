package entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @ClassName:ConversationEntry
 * @Description: TODO
 * @Author:Dazz1e
 * @Date:2023/5/20 下午 8:40
 * Version V1.0
 */
@Entity
@Table(name="conv_entry")
@Data
public class ConversationEntry implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int grade;
    private String brief;
    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    @JoinColumn(name = "conv_id", referencedColumnName = "id")
    private Collection<ConversationItem> conversations= new ArrayList<ConversationItem>();

    public void addItem(ConversationItem i) {
        conversations.add(i);
    }
}
