package com.limspyne.anon_vote.poll.dto;

import lombok.Setter;

import java.util.List;

public class GetCategory {

    public static class Response {
        public String id;
        public String name;
        public List<GetCategory.Response> children;

        public Response(String id, String name, List<GetCategory.Response> children) {
            this.id = id;
            this.name = name;
            this.children = children;
        }
    }
}
