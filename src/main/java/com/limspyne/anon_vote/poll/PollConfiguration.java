package com.limspyne.anon_vote.poll;

import com.limspyne.anon_vote.poll.dto.GetCategory;
import com.limspyne.anon_vote.poll.entities.PollCategory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.stream.Collector;

@Configuration
public class PollConfiguration {
    @Autowired
    public void configureModelMapper(ModelMapper modelMapper) {
        modelMapper.typeMap(PollCategory.class, GetCategory.ResponseDto.class).addMappings(mapper -> {
            mapper.map(src -> new ArrayList<>(),
                    GetCategory.ResponseDto::setChildren);
        });

    }
}
