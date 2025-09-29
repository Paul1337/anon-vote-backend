package com.limspyne.anon_vote.poll.services;

import com.limspyne.anon_vote.poll.entities.PollTag;
import com.limspyne.anon_vote.poll.repositories.PollTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PollTagService {
    @Autowired
    private PollTagRepository pollTagRepository;

    public Set<PollTag> findOrCreateTagsOfNames(Set<String> tagNames) {
        if (tagNames.isEmpty()) return new HashSet<>();
        Set<PollTag> existingTags = new HashSet<>(pollTagRepository.findAllByNameIn(tagNames));

        Set<String> existingNames = existingTags.stream()
                .map(PollTag::getName)
                .collect(Collectors.toSet());

        Set<String> newNames = tagNames.stream()
                .filter(name -> !existingNames.contains(name))
                .collect(Collectors.toSet());

        if (!newNames.isEmpty()) {
            List<PollTag> newTags = newNames.stream()
                    .map(PollTag::new)
                    .collect(Collectors.toList());
            List<PollTag> savedNewTags = pollTagRepository.saveAll(newTags);
            existingTags.addAll(savedNewTags);
        }

        return existingTags;
    }
}
