package com.example.springbootdocker.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author norhtking
 */
@RestController
public class DockerController {

    @GetMapping("/")
    public String index() {
        return "Docker hello";
    }
}
