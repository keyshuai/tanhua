package com.tanhua.model.mongo;

import com.tanhua.model.enums.CommentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "peachblossom")
public class Peachblossom implements java.io.Serializable {

    private static final long serialVersionUID = -3136732836884933873L;

//    private ObjectId ids; //主键id
    private Long vid; //自动增长
    private Long created; //创建时间

    private Integer id;//用户id
    private String avatar;//头像
    private String nickname;//昵称
    private String gender;//性别
    private Integer age; //年龄
    private String soundUrl; //语音文件，URL
    private Integer remainingTimes; //剩余次数




}
