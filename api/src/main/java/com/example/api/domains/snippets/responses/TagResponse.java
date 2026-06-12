package com.example.api.domains.snippets.responses;

public record TagResponse(
    String name,
    String count
) {
    public static TagResponse from(String name, Long total) {
        return new TagResponse(name, abbreviate(total));
    }

    private static String abbreviate(Long total) {
        if (total == null) return "0";
        if (total >= 1000000) {
            return String.format("%.1fM", total / 1000000.0).replace(".0", "");
        }
        if (total >= 1000) {
            return String.format("%.1fk", total / 1000.0).replace(".0", "");
        }
        return String.valueOf(total);
    }
}
