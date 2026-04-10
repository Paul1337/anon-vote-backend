package com.limspyne.anon_vote.shared.inftrastrucure.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class TemplateLoader {
    private TemplateLoader() {}

    public static String loadTemplate(String path, String... placeholders) throws IOException {
        try (InputStream inputStream = TemplateLoader.class.getClassLoader().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IOException("Шаблон не найден: " + path);
            }
            String template = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            return String.format(template, (Object[]) placeholders);
        }
    }
}