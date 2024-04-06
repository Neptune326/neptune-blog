package com.neptune;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.neptune.mapper")
@SpringBootApplication
public class NeptuneSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(NeptuneSpringbootApplication.class, args);
    }

}
