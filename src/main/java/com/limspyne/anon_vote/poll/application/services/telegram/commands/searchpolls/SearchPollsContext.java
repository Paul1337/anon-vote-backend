package com.limspyne.anon_vote.poll.application.services.telegram.commands.searchpolls;

import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommand;
import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommandContext;
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
