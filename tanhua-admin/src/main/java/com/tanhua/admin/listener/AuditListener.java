package com.tanhua.admin.listener;

import com.tanhua.autoconfig.template.AliyunGreenTemplate;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.model.mongo.Movement;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuditListener {
    @DubboReference
    private MovementApi movementApi;

    @Autowired
    private AliyunGreenTemplate template;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "tanhua.audit.queue",
                    durable = "true"),
            exchange = @Exchange(
                    value = "tanhua.audit.exchange",
                    type = ExchangeTypes.TOPIC
            ),
            key = "audit.movement"
    ))
    public void audit(String movementId){
        try {
            Movement movement = movementApi.findById(movementId);
            if (movement !=null && movement.getState()==0){
                //审核文本,审核图片
                Map<String, String> textScan = template.greenTextScan(movement.getTextContent());
                Map<String, String> imageScan = template.imageScan(movement.getMedias());
                //判断审核结果
                int state= 0;
                if (textScan!=null &&imageScan!=null){
                    String testSuggestion = textScan.get("suggestion");
                    String imagesuggestion = imageScan.get("suggestion");
                    if ("block".equals(testSuggestion)||"block".equals(imagesuggestion)){
                        state=2;//驳回
                    }
                    if ("pass".equals(testSuggestion)&&"pass".equals(imagesuggestion)){
                        state=1;//通过
                    }
                }
                movementApi.update(movementId,state);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
