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
@Document(collection = "questions") //试题
public class Questions implements java.io.Serializable {
    private String id;//试题编号
    private String question;//题目
    List<Options>options;



}
