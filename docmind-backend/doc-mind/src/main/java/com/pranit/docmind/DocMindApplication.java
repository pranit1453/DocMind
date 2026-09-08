package com.pranit.docmind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DocMindApplication {

    static void main(String[] args) {
        SpringApplication.run(DocMindApplication.class, args);
    }

}
