package com.elacode;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkfllowController {
    record PingPong(String result){}

    @GetMapping("/ping")
    public WorkfllowController.PingPong getPingPong(){
        return new WorkfllowController.PingPong("Pong");
    }
}
