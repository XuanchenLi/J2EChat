package entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName:GradeMsg
 * @Description: TODO
 * @Author:Dazz1e
 * @Date:2023/5/23 下午 10:26
 * Version V1.0
 */
@Data
public class GradeMsg implements Serializable {
    private Integer uId;
    private Integer id;
    private Integer grade;
}
