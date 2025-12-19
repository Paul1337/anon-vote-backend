package com.limspyne.anon_vote.poll.domain.services.stat.export;

import com.limspyne.anon_vote.poll.infrastructure.repositories.QuestionRepository;
import com.limspyne.anon_vote.poll.web.dto.GetDailyStat;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class PollStatCsvExportService implements ExportDailyStatService<GetDailyStat.Response> {
    private final QuestionRepository questionRepository;

    @Override
    @Transactional(readOnly = true)
    public byte[] exportDailyStat(GetDailyStat.Response response) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {

            String header = buildHeader(response);
            if (header.isEmpty()) return new byte[] {};
            writer.println(header);

            for (var item : response.getData()) {
                writer.println(buildRow(item));
            }

            writer.flush();
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Failed to export to CSV", e);
        }
    }

    private String buildHeader(GetDailyStat.Response response) {
        var statItems = response.getData();
        if (statItems.isEmpty()) return "";
        var answers = statItems.getFirst().getAnswers();
        var questions = questionRepository.findByIdIn(answers.keySet());

        StringBuilder header = new StringBuilder("Дата");
        questions.forEach(question -> {
            header.append(",").append(question.getText()).append(",");
            for (int i = 0; i < question.getOptions().size(); i++) {
                boolean isLast = i == question.getOptions().size() - 1;
                header.append(question.getOptions().get(i));
                if (!isLast) {
                    header.append(",");
                }
            }
        });
        return header.toString();
    }

    private String buildRow(GetDailyStat.StatItem item) {
        StringBuilder row = new StringBuilder();
        var answers = item.getAnswers();

        for (var answerEntry: answers.entrySet()) {
            answerEntry.getKey();
        }

        return row.toString();
    }

    @Override
    public String getContentType() {
        return "text/csv; charset=UTF-8";
    }
}