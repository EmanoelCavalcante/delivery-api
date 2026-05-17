package com.pitsdog.api.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class controller {

    @GetMapping("/health")
    public String health(){
        return "API Pit's Dog online";
    }
}
