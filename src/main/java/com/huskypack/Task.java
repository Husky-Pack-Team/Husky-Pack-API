package com.huskypack;

public class Task {
    public final int code;
    public final User user;
    public final String title;
    public final String description;
    public final int cost;
    public String status;

    public Task(int code, User user, String title, String description, int cost) {
        this.code = code;
        this.user = user;
        this.title = title;
        this.description = description;
        this.cost = cost;
        this.status = "False";
    }

    public String toString() {
        String task = "{\n"
            + "   code: " + code + "\n"
            + "   User: " + user.id + "\n"
            + "   title: " + title + "\n"
            + "   description: " + description + "\n"
            + "   cost: " + cost + "\n"
            + "   status: " + status + "\n"
            + "}";
        return task;
    }
}
