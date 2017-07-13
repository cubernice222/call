package org.cuber.callerDemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by cuber on 2017/7/13.
 */
@SpringBootApplication
@EnableWebMvc
public class CallerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CallerApplication.class,args);
    }
}
