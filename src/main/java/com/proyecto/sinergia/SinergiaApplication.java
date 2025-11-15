package com.proyecto.sinergia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration; 
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration; 
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = {
    SecurityAutoConfiguration.class, 
    UserDetailsServiceAutoConfiguration.class
})
@EnableTransactionManagement
public class SinergiaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SinergiaApplication.class, args);
    }
}