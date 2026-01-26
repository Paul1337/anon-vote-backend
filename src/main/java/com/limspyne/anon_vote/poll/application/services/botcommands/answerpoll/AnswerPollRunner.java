package com.limspyne.anon_vote.poll.application.services.botcommands.answerpoll;

import com.limspyne.anon_vote.poll.application.entities.Question;
import com.limspyne.anon_vote.poll.application.exceptions.PollNotFoundException;
import com.limspyne.anon_vote.poll.application.services.PollSubmitService;
import com.limspyne.anon_vote.poll.infrastructure.repositories.PollRepository;
import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommand;
import com.limspyne.anon_vote.shared.application.telegram.dto.BotCommandContext;
import com.limspyne.anon_vote.shared.application.telegram.services.CommandRunner;
import com.limspyne.anon_vote.shared.presenter.telegram.dto.TelegramDto;
import com.limspyne.anon_vote.users.application.entities.User;
import com.limspyne.anon_vote.users.application.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnswerPollRunner extends CommandRunner {
    private final PollRepository pollRepository;

    private final PollSubmitService pollSubmitService;

    private final UserService userService;

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
        return request.replyBuilder().text("Отлично! Введите id опроса").build();
    }

    private TelegramDto.Response handleSelectPoll(TelegramDto.Request request, AnswerPollContext context) {
        String pollId = request.getText();
        UUID pollUUID = UUID.fromString(pollId);
        if (!pollRepository.existsById(pollUUID)) {
            return request.replyBuilder().text("К сожалению, такого опроса не существует").build();
        }

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
