package com.dis.rpc.test.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com")
public class App {


    public static void main(String[] args) {
        SpringApplication.run(App.class,args);
    }
}
