package com.limspyne.anon_vote.poll.application.services.botcommands.answerpoll;

import com.limspyne.anon_vote.poll.application.entities.Poll;
import com.limspyne.anon_vote.poll.application.entities.Question;
import com.limspyne.anon_vote.poll.application.exceptions.PollNotFoundException;
import com.limspyne.anon_vote.poll.application.services.PollSubmitService;
import com.limspyne.anon_vote.poll.application.services.query.PollQueryService;
import com.limspyne.anon_vote.poll.infrastructure.repositories.PollRepository;
import com.limspyne.anon_vote.poll.presenter.dto.GetPoll;
import com.limspyne.anon_vote.poll.presenter.dto.SearchPolls;
import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommand;
import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommandContext;
import com.limspyne.anon_vote.shared.application.telegram.services.CommandRunner;
import com.limspyne.anon_vote.shared.presenter.telegram.dto.TelegramDto;
import com.limspyne.anon_vote.users.application.entities.User;
import com.limspyne.anon_vote.users.application.services.UserService;
import com.limspyne.anon_vote.users.instrastructure.security.AppUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AnswerPollRunner extends CommandRunner {
    private final PollRepository pollRepository;

    private final PollQueryService pollQueryService;

    private final PollSubmitService pollSubmitService;

    private final UserService userService;

    public static class Buttons {
        private static final String SHOW_ALL = "показать все";

        private static final String SELECT_CATEGORY = "выбрать категорию";

        private static final String SELECT_TAGS = "выбрать теги";

        private static final String PAGINATION_PREV = "назад";

        private static final String PAGINATION_NEXT = "вперёд";
    }

    @Override
    protected boolean canRun(BotCommand botCommand) {
        return botCommand == BotCommand.ANSWER_POLL;
    }

    @Override
    @Transactional
    public TelegramDto.Response handleCommand(TelegramDto.Request request, BotCommandContext context) {
        if (!(context instanceof AnswerPollContext answerPollContext)) throw new RuntimeException("context expected to be of type AnswerPollContext");

        return switch (answerPollContext.getState()) {
            case NONE -> handleStart(request, answerPollContext);
            case SELECTING_POLL -> handleSelectPoll(request, answerPollContext);
            case BEFORE_ANSWERING, ANSWERING -> handleAnswer(request, answerPollContext);
        };
    }

    private TelegramDto.Response handleStart(TelegramDto.Request request, AnswerPollContext context) {
        context.setState(AnswerPollContext.AnswerPollState.SELECTING_POLL);
        context.resetSearchData();


        StringBuilder textBuilder = new StringBuilder();

        textBuilder.append("Отлично! Выберете опрос!\n");

        // todo:
        //        var data = context.getSearchData();
        //        textBuilder.append("Выбранная категория: %s".formatted(data.getCategoryId())).append("\n");
        //        textBuilder.append("Выбранные теги %s".formatted(String.join("; ", data.getTags()))).append("\n\n");

        textBuilder.append("Введите название, по которому ищем..");

        return request.replyBuilder().text(textBuilder.toString()).build();
    }

    private TelegramDto.Response handleSelectPoll(TelegramDto.Request request, AnswerPollContext context) {
        String text = request.getText().toLowerCase();

        return switch (text.toLowerCase()) {
            case Buttons.PAGINATION_PREV -> handlePrevPage(request, context);
            case Buttons.PAGINATION_NEXT -> handleNextPage(request, context);

            default -> {
                boolean isNumeric = text.chars().allMatch(Character::isDigit);
                boolean isSelecting = isNumeric && !context.getSearchData().getResults().isEmpty();

                if (isSelecting) {
                    yield selectPoll(request, context);
                } else {
                    yield updateSearchResults(request, context);
                }
            }
        };
    }

    private TelegramDto.Response handleNextPage(TelegramDto.Request request, AnswerPollContext context) {
        if (!context.getSearchData().isHasNextPage()) {
            return request.replyBuilder().text("Не существует следующей страницы").build();
        }

        context.getSearchData().nextPage();
        return updateSearchResults(request, context);
    }

    private TelegramDto.Response handlePrevPage(TelegramDto.Request request, AnswerPollContext context) {
        if (context.getSearchData().getPageNumber() < 1) {
            return request.replyBuilder().text("Не существует предыдущей страницы").build();
        }

        context.getSearchData().prevPage();
        return updateSearchResults(request, context);
    }

    private TelegramDto.Response updateSearchResults(TelegramDto.Request request, AnswerPollContext context) {
        var searchData = context.getSearchData();
        User user = userService.getUserByTelegramId(request.getTelegramId());
        var searchDto = new SearchPolls.Request();
        searchDto.setTitle(request.getText());
        searchDto.setPage(searchData.getPageNumber());
        var result = pollQueryService.searchPolls(searchDto, new AppUserDetails(user.getEmail(), user.getId()));

        Function<GetPoll.QuestionDto, String> pollQuestionToPreview = question -> question.getText() + "( " + String.join("; ", question.getOptions()) + " )";
        Function<GetPoll.Response, String> pollToPreview = poll -> poll.getTitle() + "\n" + poll.getQuestions().stream().limit(3).map(pollQuestionToPreview).collect(Collectors.joining("\n")) + "\n" + "...";

        searchData.setResults(result.getContent());
        searchData.setHasNextPage(result.isHasNextPage());

        var responseText =
                searchData.getResults().isEmpty() ? "Ничего не нашлось ( Введите другой текст поиска" :
                "Результат поиска (страница %d) \n\n".formatted(searchData.getPageNumber() + 1) +
                        IntStream.range(0, result.getContent().size())
                                .mapToObj(i -> "%d. %s".formatted(
                                        i + 1,
                                        pollToPreview.apply(result.getContent().get(i))
                                ))
                                .collect(Collectors.joining("\n\n")) + "\n\n" +
                "Выберете опрос числом %d - %d".formatted(1, result.getContent().size());

        List<String> buttons = new ArrayList<>();
        if (result.isHasNextPage()) buttons.add(Buttons.PAGINATION_NEXT);
        if (searchData.getPageNumber() > 0) buttons.add(Buttons.PAGINATION_PREV);

        return request.replyBuilder().text(responseText).inlineButtons(buttons.toArray(new String[0])).build();
    }

    private TelegramDto.Response selectPoll(TelegramDto.Request request, AnswerPollContext context) {
        int pollNumber = Integer.parseInt(request.getText());

        if (pollNumber < 1 || pollNumber > context.getSearchData().getResults().size()) {
            return request.replyBuilder().text("Некорректный номер опроса, повторите выбор").build();
        }

        GetPoll.Response selected = context.getSearchData().getResults().get(pollNumber - 1);
        var pollUUID = UUID.fromString(selected.getId());

        User user = userService.getUserByTelegramId(request.getTelegramId());
        if (pollRepository.existsByIdAndAttemptedUsersId(pollUUID, user.getId())) {
            context.setFinished(true);
            return request.replyBuilder().text("Вы уже отвечали на этот опрос, пройдите другой :)").build();
        }

        context.setPollID(pollUUID);
        context.setState(AnswerPollContext.AnswerPollState.BEFORE_ANSWERING);

        return handleAnswer(request, context);
    }

    private TelegramDto.Response handleAnswer(TelegramDto.Request request, AnswerPollContext context) {
        if (context.getState() == AnswerPollContext.AnswerPollState.BEFORE_ANSWERING) {
            context.setState(AnswerPollContext.AnswerPollState.ANSWERING);
            context.setQuestionNumber(0);
            context.setAnswers(new HashMap<>());
            User user = userService.getUserByTelegramId(request.getTelegramId());
            if (pollRepository.existsByIdAndAttemptedUsersId(context.getPollID(), user.getId())) {
                context.setFinished(true);
                return request.replyBuilder().text("Вы уже отвечали на этот опрос, пройдите другой :)").build();
            }
        } else if (context.getState() == AnswerPollContext.AnswerPollState.ANSWERING) {
            var question = getCurrentQuestion(context).orElseThrow(() -> new IllegalStateException("No question to answer"));
            context.getAnswers().put(question.getId(), request.getText());
            context.nextQuestion();
        }

        var nextQuestionResponse = getCurrentQuestionResponse(request, context);

        if (nextQuestionResponse != null) {
            return nextQuestionResponse;
        } else {
            User user = userService.getUserByTelegramId(request.getTelegramId());
            pollSubmitService.submitPoll(user, context.getPollID(), context.getAnswers());
            context.setFinished(true);
            return request.replyBuilder().text("Опрос закончен, спасибо за уделённое время!").build();
        }
    }

    private TelegramDto.Response getCurrentQuestionResponse(TelegramDto.Request request, AnswerPollContext context) {
        var currentQuestion = getCurrentQuestion(context);
        return currentQuestion.map(question -> mapQuestionToReponse(question, request)).orElse(null);
    }

    private Optional<Question> getCurrentQuestion(AnswerPollContext context) {
        var poll = pollRepository.findPollWithQuestionsById(context.getPollID()).orElseThrow(() -> new PollNotFoundException(context.getPollID()));
        if (context.getQuestionNumber() > poll.getQuestions().size() - 1) return Optional.empty();
        return Optional.of(poll.getQuestions().get(context.getQuestionNumber()));
    }

    private TelegramDto.Response mapQuestionToReponse(Question question, TelegramDto.Request request) {
        String text = question.getText();
        var inlineButtons = question.getOptions().toArray(new String[0]);
        return request.replyBuilder().text(text).inlineButtons(inlineButtons).build();
    }



}
