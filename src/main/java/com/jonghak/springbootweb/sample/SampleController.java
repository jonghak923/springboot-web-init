package com.jonghak.springbootweb.sample;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @GetMapping("/hello/{name}")
    public String getHello(@PathVariable("name") Person person) {
        return "hello " + person.getName();
    }

    @GetMapping("/hello")
    public String getHelloParam(@RequestParam("name") Person person) {
        return "hello " + person.getName();
    }

    @GetMapping("/hellojpa")
    public String getHelloJpa(@RequestParam("id") PersonEntity person) {
        return "hello " + person.getName();
    }
}
