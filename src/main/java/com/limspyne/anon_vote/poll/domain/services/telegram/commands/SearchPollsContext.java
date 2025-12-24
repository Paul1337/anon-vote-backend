package com.limspyne.anon_vote.poll.domain.services.telegram.commands;

import com.limspyne.anon_vote.shared.domain.dto.telegram.BotCommand;
import com.limspyne.anon_vote.shared.domain.dto.telegram.BotCommandContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchPollsContext implements BotCommandContext {
    private String query;

    private int page;

    private SearchPollsState state;

    @Override
    public BotCommand getCommand() {
        return BotCommand.SEARCH_POLLS;
    }
}
