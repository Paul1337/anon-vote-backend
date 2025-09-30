package com.limspyne.anon_vote.poll;

import com.limspyne.anon_vote.poll.dto.GetCategory;
import com.limspyne.anon_vote.poll.dto.GetPoll;
import com.limspyne.anon_vote.poll.entities.Poll;
import com.limspyne.anon_vote.poll.entities.PollCategory;
import com.limspyne.anon_vote.poll.entities.PollTag;
import com.limspyne.anon_vote.poll.entities.Question;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
