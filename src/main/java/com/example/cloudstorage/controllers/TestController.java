package com.example.cloudstorage.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {
    @PreAuthorize("hasAuthority('FILE_READ')")
    @GetMapping("/a")
    public void  a(){
        log.info("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    }
}
