package com.limspyne.anon_vote.shared.application.telegram.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public class TelegramDto {
    @Data
    @AllArgsConstructor
    public static class Request {
        String text;
        Long telegramId;

        public static Request from(Update update) {
            if (update.getMessage() != null) {
                return new Request(update.getMessage().getText(), update.getMessage().getChatId());
            } else if (update.getCallbackQuery() != null){
                return new Request(update.getCallbackQuery().getData(), update.getCallbackQuery().getFrom().getId());
            }
            return null;
        }

        public Response.Builder replyBuilder() {
            return Response.forChat(telegramId);
        }
    }

    @Data
    public static class Response {
        Long telegramId;
        String text;
        List<InlineButton> inlineButtons;
        boolean showMenu;
        boolean isCommandFinished = false;

        public static Builder forChat(long telegramId) {
            return new Response.Builder(telegramId);
        }

        private Response() {}

        @Data
        public static class InlineButton {
            private String name;
            private String callbackData;

            public InlineButton(String name, String callbackData) {
                if (!callbackData.startsWith("btn_")) callbackData = "btn_" + callbackData;

                this.name = name;
                this.callbackData = callbackData;
            }

            public boolean match(String text) {
                return text.equalsIgnoreCase(name) || text.equalsIgnoreCase(callbackData);
            }
        }

        @NoArgsConstructor
        public static class Builder {
            Long telegramId;
            String text;
            List<InlineButton> inlineButtons;
            boolean showMenu;

            public Builder(long telegramId) {
                this.telegramId = telegramId;
            }

            public Builder text(String text) {
                this.text = text;
                return this;
            }

            public Builder inlineButtons(List<InlineButton> inlineButtons) {
                this.inlineButtons = inlineButtons;
                return this;
            }

            public Builder withMenu() {
                this.showMenu = true;
                return this;
            }

            public Response build() {
                var response = new Response();
                response.telegramId = telegramId;
                response.text = text;
                response.inlineButtons = inlineButtons;
                response.showMenu = showMenu;

                return response;
            }
        }
    }
}
