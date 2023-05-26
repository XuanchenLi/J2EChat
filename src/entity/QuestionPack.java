package entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName:QuestionPack
 * @Description: TODO
 * @Author:Dazz1e
 * @Date:2023/5/23 下午 9:10
 * Version V1.0
 */
@Data
public class QuestionPack implements Serializable {
    private Integer uId;
    private String name;
    private String msg;
}
