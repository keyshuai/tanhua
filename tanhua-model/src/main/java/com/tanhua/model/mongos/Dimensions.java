package com.tanhua.model.mongos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "report") //试题
public class Dimensions implements java.io.Serializable{
    private String key;//维度项（外向，判断，抽象，理性）
    private String value;//维度值
}
