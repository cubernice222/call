package org.cuber.callDemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by cuber on 2017/7/12.
 */
@SpringBootApplication
@EnableWebMvc
public class CalleeApplication {
    public static void main(String[] args) {
        SpringApplication.run(CalleeApplication.class,args);
    }
}
