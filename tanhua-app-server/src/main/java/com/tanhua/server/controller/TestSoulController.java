package com.tanhua.server.controller;

import com.tanhua.model.mongos.Report;
import com.tanhua.model.mongos.Soul;
import com.tanhua.server.service.testSoulService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/testSoul")
public class TestSoulController {
    @Autowired
    private testSoulService testSoulService;


    @GetMapping
    public List<Soul> testSoul(){
        List<Soul> soul=testSoulService.testSoul();

        return soul;
    }

    //提交
    @PostMapping
    public ResponseEntity postSoul(@RequestBody Map answers){

        List<HashMap> hashMaps= (List<HashMap>) answers.get("answers");

        String id=testSoulService.submit(hashMaps);
        System.out.println(id);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/report/{id}")
    public ResponseEntity report(String id){
        Report report=testSoulService.report(id);
        System.out.println(id);
        return ResponseEntity.ok(report);
    }

}
