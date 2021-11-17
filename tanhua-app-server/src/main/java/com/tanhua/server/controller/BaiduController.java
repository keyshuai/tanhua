package com.tanhua.server.controller;

import com.tanhua.server.service.BaiduService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/baidu")
public class BaiduController {
    @Autowired
    private BaiduService baiduService;

    //更新地理位置
    @PostMapping("/location")
    public ResponseEntity updateLocation(@RequestBody Map map){
        Double laitude= Double.valueOf(map.get("latitude").toString());
        Double logitude= Double.valueOf(map.get("longitude").toString());
        String address=map.get("addrStr").toString();
        //精度 维度 位置描述
        baiduService.updateLocation(logitude,laitude,address);
        return ResponseEntity.ok(null);

    }

}
