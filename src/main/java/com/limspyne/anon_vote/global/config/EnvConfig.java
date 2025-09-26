package com.limspyne.anon_vote.global.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class EnvConfig {
    @PostConstruct
    public void loadDotEnv() {
//        Dotenv dotenv = Dotenv.configure().load();

    }
}