package com.example.parseconfig.config;

import com.example.parseconfig.parser.Parser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BasicConfiguration {
    @Bean
    public Parser parser() {
        return new Parser();
    }
}
