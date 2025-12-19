package com.limspyne.anon_vote.poll.domain.services.stat.export;

public interface ExportDailyStatService<T> {
    byte[] exportDailyStat(T data);
    String getContentType();
}
