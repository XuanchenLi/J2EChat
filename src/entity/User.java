package entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @ClassName:User
 * @Description: TODO
 * @Author:Dazz1e
 * @Date:2023/5/20 下午 8:36
 * Version V1.0
 */
@Entity
@Table(name="user")
@Data
public class User implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String password;
    private String gender;
    private String email;
    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Collection<ConversationEntry> conversations= new ArrayList<ConversationEntry>();

}
