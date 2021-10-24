package com.huskypack;

public class Task {
    public final int code;
    public final int id;
    public final String title;
    public final String description;
    public final int cost;
    public String status;

    public Task(int code, int id, String title, String description, int cost) {
        this.code = code;
        this.id = id;
        this.title = title;
        this.description = description;
        this.cost = cost;
        this.status = "False";
    }

    public String toString() {
        String task = "{\n"
            + "   \"code\": \"" + code + "\",\n"
            + "   \"user\": \"" + id + "\",\n"
            + "   \"title\": \"" + title + "\",\n"
            + "   \"description\": \"" + description + "\",\n"
            + "   \"cost\": \"" + cost + "\",\n"
            + "   \"status\": \"" + status + "\",\n"
            + "}";
        return task;
    }
}
