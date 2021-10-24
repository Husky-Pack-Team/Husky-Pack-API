package com.huskypack;

public class Post {
    public final int key;
    public final int id;
    public final String title;
    public final String content;

    public Post(int key, int id, String title, String content) {
        this.key = key;
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public String toString() {
        String post = "{\n"
            + "   key: " + key + "\n"
            + "   user: " + id + "\n"
            + "   title: " + title + "\n"
            + "   content: " + content + "\n"
            + "}";
        return post;
    }
}
