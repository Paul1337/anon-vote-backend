package com.limspyne.anon_vote.poll.infrastructure.config;

import com.limspyne.anon_vote.category.presenter.dto.GetCategory;
import com.limspyne.anon_vote.category.application.entities.PollCategory;
import com.limspyne.anon_vote.poll.application.entities.PollTag;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class PollConfiguration {
    public void configureModelMapper(ModelMapper modelMapper) {
        modelMapper.typeMap(PollCategory.class, GetCategory.ResponseDto.class).addMappings(mapper -> {
            mapper.map(src -> new ArrayList<>(),
                    GetCategory.ResponseDto::setChildren);
        });

        modelMapper.typeMap(PollTag.class, String.class).setConverter(ctx -> ctx.getSource().getName());
    }
}
