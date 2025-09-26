package com.limspyne.anon_vote.global.config;

import com.limspyne.anon_vote.poll.dto.GetCategory;
import com.limspyne.anon_vote.poll.entities.PollCategory;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class MapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        var modelMapper = new ModelMapper();
        return modelMapper;
    }
}
