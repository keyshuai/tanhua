package com.tanhua.model.mongos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "report") //试题
public class Report implements java.io.Serializable{

    private String id;
    private String conclusion;//鉴定结果
    private String cover;//鉴定图片
    private List<Dimensions> dimensions;//维度
    private List<similarYou> similarYou;//与你相似
}
