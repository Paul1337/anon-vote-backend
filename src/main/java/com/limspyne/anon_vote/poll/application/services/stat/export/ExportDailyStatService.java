package com.limspyne.anon_vote.poll.application.services.stat.export;

import java.util.UUID;

public interface ExportDailyStatService<T> {
    byte[] exportDailyStat(UUID pollId, T data);
    String getContentType();
}
