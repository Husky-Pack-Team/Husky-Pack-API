package com.huskypack;

public class Post {
    public final int key;
    public final User user;
    public final String title;
    public final String content;

    public Post(int key, User user, String title, String content) {
        this.key = key;
        this.user = user;
        this.title = title;
        this.content = content;
    }

    public String toString() {
        String user = "{\n"
            + "   key: " + key + "\n"
            + "   user: " + user.id + "\n"
            + "   title: " + title + "\n"
            + "   content: " + content + "\n"
            + "}";
        return user;
    }
}
