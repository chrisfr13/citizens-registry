package gr.hua.dit.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "gr.hua.dit.domain.model")
public class CitizensRegistryApplication {
    public static void main(String[] args) {
        SpringApplication.run(CitizensRegistryApplication.class, args);
    }
}
