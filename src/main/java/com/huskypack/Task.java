package com.huskypack;

public class Task {
    public final int code;
    public final User user;
    public final String title;
    public final String description;
    public Boolean status;

    public Task(int code, User user, String title, String description) {
        this.code = code;
        this.user = user;
        this.title = title;
        this.description = description;
        this.status = false;
    }

    public String toString() {
        String task = "{\n"
            + "   code: " + code + "\n"
            + "   User: " + user + "\n"
            + "   title: " + title + "\n"
            + "   description: " + description + "\n"
            + "   status: " + status + "\n"
            + "}";
        return task;
    }
}