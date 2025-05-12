package org.gitter.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "org.gitter")
public class GitterApplication {

    public static void main(String[] args) {
        SpringApplication.run(GitterApplication.class, args);
    }
}
