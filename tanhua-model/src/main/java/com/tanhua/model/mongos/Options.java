package com.tanhua.model.mongos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "options") //选项
public class Options implements java.io.Serializable {
    private String id;//选项编号
    private String option;//选项
}
