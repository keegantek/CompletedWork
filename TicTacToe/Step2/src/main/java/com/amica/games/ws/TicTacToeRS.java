package com.amica.games.ws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class TicTacToeRS {

    @Bean
    public GameController getGameController() {
        return new GameController();
    }

    public static void main(String[] args) {
        SpringApplication.run(TicTacToeRS.class, args);
    }
}
