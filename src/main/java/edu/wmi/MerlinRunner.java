package edu.wmi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

/**
 * Created by lupus on 02.11.16.
 */
@SpringBootApplication
@EnableAutoConfiguration
@Profile("merlin")
public class MerlinRunner {
    public static void main(String[] args) {
        SpringApplication.run(MerlinRunner.class, args);
    }


}
