package com.example.demo.controller;

import com.example.demo.configs.InitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConcurrencyController {

    private static final String Prefix = "concurrency";

    @Autowired
    private InitService initService;

    @RequestMapping(value=Prefix+"/robbing/thread",method = RequestMethod.GET)
    public void  robbingThread(String mobile){
        initService.generateMultiThread(mobile);
    }
}
