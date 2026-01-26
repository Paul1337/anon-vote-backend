package com.limspyne.anon_vote.poll.application.services.botcommands.answerpoll;

import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommand;
import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommandContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerPollContext extends BotCommandContext implements Cloneable {
    private UUID pollID = null;

    private int questionNumber = 0;

    private AnswerPollState state = AnswerPollState.NONE;

    private Map<UUID, String> answers = new HashMap<>();

    public void nextQuestion() {
        questionNumber++;
    }

    public AnswerPollContext(UUID pollID) {
        this.pollID = pollID;
        state = AnswerPollState.BEFORE_ANSWERING;
    }

    @Override
    public AnswerPollContext clone() {
        AnswerPollContext clone = (AnswerPollContext) super.clone();
        clone.answers = new HashMap<>(answers);
        return clone;
    }

    public enum AnswerPollState {
        NONE,
        SELECTING_POLL,
        BEFORE_ANSWERING,
        ANSWERING
    }

}
