package entity;

import lombok.Data;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.Serializable;

/**
 * @ClassName:GradePack
 * @Description: TODO
 * @Author:Dazz1e
 * @Date:2023/5/23 下午 9:45
 * Version V1.0
 */
@Data
public class GradePack implements Serializable {
    private Integer uId;
    private Integer grade;
}
