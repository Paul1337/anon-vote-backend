package com.limspyne.anon_vote.poll.application.services.botcommands.answerpoll;

import com.limspyne.anon_vote.poll.presenter.dto.GetPoll;
import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommandContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerPollContext extends BotCommandContext {
    private UUID pollID = null;

    private int questionNumber = 0;

    private AnswerPollState state = AnswerPollState.NONE;

    private SearchData searchData = new SearchData();

    private Map<UUID, String> answers = new HashMap<>();

    public void nextQuestion() {
        questionNumber++;
    }

    public AnswerPollContext(UUID pollID) {
        this.pollID = pollID;
        state = AnswerPollState.BEFORE_ANSWERING;
    }

    public void resetSearchData() {
        searchData = new SearchData();
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

    @Data
    public static class SearchData {
        private String title = "";

        private Set<String> tags = new HashSet<>();

        private UUID categoryId = null;

        private int pageNumber = 0;

        private List<GetPoll.Response> results = new ArrayList<>();

        private boolean hasNextPage = false;

        public void nextPage() {
            pageNumber++;
        }

        public void prevPage() {
            pageNumber--;
        }
    }

}
