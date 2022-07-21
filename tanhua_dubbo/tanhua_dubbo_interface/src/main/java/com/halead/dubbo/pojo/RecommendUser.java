package com.halead.dubbo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @title: RecommendUser
 * @Author ppjjss
 * @Date: 2022/6/29 22:52
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "recommend_user")
public class RecommendUser implements Serializable {

    private static final long serialVersionUID = 5874126532504390567L;
    @Id
    private ObjectId id;  //主键id
    @Indexed
    private Long userId; //推荐用户的id

    private Long toUserId; //用户的id
    @Indexed
    private Double score; //推荐的得分

    private String date; //日期

}
