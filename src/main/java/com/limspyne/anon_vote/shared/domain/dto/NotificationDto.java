package com.limspyne.anon_vote.shared.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationDto {
    private Long userId;
    private String text;
}
