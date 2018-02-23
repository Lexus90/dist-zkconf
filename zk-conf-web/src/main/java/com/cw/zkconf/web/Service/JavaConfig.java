package com.cw.zkconf.web.Service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JavaConfig {

    static String connect = "127.0.0.1:2181";

    @Bean
    public ConfServer confServer(){
        return new ConfServer(connect);
    }
}
