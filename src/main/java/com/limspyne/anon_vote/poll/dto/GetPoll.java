package com.limspyne.anon_vote.poll.dto;

public class GetPoll {
//    public static record Request(
//            String id
//    ) {}

    public static record Response(
            String id,
            String title
    ) {}
}
