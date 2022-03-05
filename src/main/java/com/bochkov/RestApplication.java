package com.bochkov;

import com.bochkov.jpa.entity.User;
import com.bochkov.jpa.repository.ProfileRepository;
import com.bochkov.jpa.repository.UserRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableJpaRepositories
@AllArgsConstructor

public class RestApplication {


    static Logger logger = LoggerFactory.getLogger(RestApplication.class);
    final ProfileRepository profileRepository;
    final UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
    }

    @PostConstruct
    public void initUsers() {
        userRepository.saveAll(User.createUsers(20));
    }


    @Scheduled(fixedRate = 20000, initialDelay = 20000)
    @Async
    public void runCashUp() {
        logger.debug("call cash up:" + LocalDateTime.now());
        profileRepository.cashUp();
    }
}
