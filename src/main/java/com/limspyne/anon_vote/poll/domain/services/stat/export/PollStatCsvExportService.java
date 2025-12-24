package com.limspyne.anon_vote.poll.domain.services.stat.export;

import com.limspyne.anon_vote.poll.domain.entities.Question;
import com.limspyne.anon_vote.poll.domain.exceptions.PollNotFoundException;
import com.limspyne.anon_vote.poll.infrastructure.repositories.PollRepository;
import com.limspyne.anon_vote.poll.infrastructure.repositories.QuestionRepository;
import com.limspyne.anon_vote.poll.presentation.dto.GetDailyStat;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PollStatCsvExportService implements ExportDailyStatService<GetDailyStat.Response> {
    private final QuestionRepository questionRepository;

    private final PollRepository pollRepository;

    @Override
    @Transactional(readOnly = true)
    public byte[] exportDailyStat(UUID pollId, GetDailyStat.Response response) {
        var poll = pollRepository.findPollWithQuestionsById(pollId).orElseThrow(() -> new PollNotFoundException(pollId));

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {

            String header = buildHeader(poll.getQuestions());
            if (header.isEmpty()) return new byte[] {};
            writer.println(header);

            for (var item : response.getData()) {
                writer.println(buildRow(item, poll.getQuestions()));
            }

            writer.flush();
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Failed to export to CSV", e);
        }
    }

    private String buildHeader(List<Question> questions) {
        StringBuilder header = new StringBuilder("Дата");
        questions.forEach(question -> {
            header.append(",").append(question.getText()).append(",");
            var sortedOptions = question.getOptions().stream().sorted().toList();
            for (int i = 0; i < sortedOptions.size(); i++) {
                boolean isLast = i == sortedOptions.size() - 1;
                header.append(sortedOptions.get(i));
                if (!isLast) {
                    header.append(",");
                }
            }
        });
        return header.toString();
    }

    private String buildRow(GetDailyStat.StatItem item, List<Question> questions) {
        StringBuilder row = new StringBuilder(item.getDate().toString());
        var answers = item.getAnswers();

        for (var question: questions) {
            row.append(",");
            var answer = answers.get(question.getId());
            var sortedOptions = question.getOptions().stream().sorted().toList();

            for (int i = 0; i < sortedOptions.size(); i++) {
                var option = sortedOptions.get(i);
                row.append(",");
                row.append(answer.getOrDefault(option, -1L));
            }
        }
        return row.toString();
    }

    @Override
    public String getContentType() {
        return "text/csv; charset=UTF-8";
    }
}