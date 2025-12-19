package com.limspyne.anon_vote.poll.domain.services.stat.export;

import java.util.UUID;

public interface ExportDailyStatService<T> {
    byte[] exportDailyStat(UUID pollId, T data);
    String getContentType();
}
