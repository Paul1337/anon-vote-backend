package com.limspyne.anon_vote.poll.infrastructure.config;

import com.limspyne.anon_vote.poll.presentation.dto.GetCategory;
import com.limspyne.anon_vote.poll.domain.entities.PollCategory;
import com.limspyne.anon_vote.poll.domain.entities.PollTag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class PollConfiguration {
    @Autowired
    public void configureModelMapper(ModelMapper modelMapper) {
        modelMapper.typeMap(PollCategory.class, GetCategory.ResponseDto.class).addMappings(mapper -> {
            mapper.map(src -> new ArrayList<>(),
                    GetCategory.ResponseDto::setChildren);
        });

        modelMapper.typeMap(PollTag.class, String.class).setConverter(ctx -> ctx.getSource().getName());
    }
}
