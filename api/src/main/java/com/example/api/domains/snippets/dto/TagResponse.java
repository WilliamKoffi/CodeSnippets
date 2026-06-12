package com.example.api.domains.snippets.dto;

public record TagResponse(
    String name,
    String count
) {
    public static TagResponse from(String name, Long count) {
        return new TagResponse(name, formatCount(count));
    }

    private static String formatCount(Long count) {
        if (count == null) return "0";
        if (count >= 1000000) {
            return String.format("%.1fM", count / 1000000.0).replace(".0", "");
        }
        if (count >= 1000) {
            return String.format("%.1fk", count / 1000.0).replace(".0", "");
        }
        return String.valueOf(count);
    }
}
