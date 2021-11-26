package com.tanhua.model.mongos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "soul")
public class Soul implements java.io.Serializable{


    private String id;//问卷编号
    private String name;//问卷名称：初级灵魂题,中级灵魂题,高级灵魂题
    private String cover;//封面
    private String level;//级别
    private Integer star;//星别（例如：2颗星，3颗星，5颗星）

    private List<Questions> questions;//试题

    private Integer isLock; //是否锁住（0解锁，1锁住）
    //private String reportId;//最新报告编号(如果有的话，提交过的才会有)

}
