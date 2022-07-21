package com.halead.dubbo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @title: Album
 * @Author ppjjss
 * @Date: 2022/6/30 21:58
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "quanzi_album")
public class Album implements java.io.Serializable {

    private static final long serialVersionUID = 432183095092216817L;

    private ObjectId id; //主键id

    private ObjectId publishId; //发布id
    private Long created; //发布时间

}