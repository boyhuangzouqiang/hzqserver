package com.message.manager.controller.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/")
public class Producer {

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    @GetMapping("send")
    public String send(String msg) {
        kafkaTemplate.send("hello", msg);
        return "SUCCESS";
    }
}
