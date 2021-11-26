package com.tanhua.server.controller;

import com.tanhua.model.mongo.Peachblossom;
import com.tanhua.server.service.PeachblossomServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/peachblossom")
public class PeachblossomController {

    @Autowired
    private PeachblossomServer peachblossomServer;

    //上传语言
    @PostMapping
    public ResponseEntity peachblossom(MultipartFile soundFile) throws IOException {
        peachblossomServer.save(soundFile);
        return ResponseEntity.ok(null);

    }
    @GetMapping
    public ResponseEntity peachblossom(){
        Peachblossom pr=peachblossomServer.reception();

        return ResponseEntity.ok(pr);
    }

}
