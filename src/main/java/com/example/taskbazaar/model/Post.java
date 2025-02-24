/*
 * author : Md. Sakil Ahmed
 */

package com.example.taskbazaar.model;

public class Post {
    private int id;
    private String title;
    private String description;

    public Post(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public Post(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
    public String toString(){
        return "Post{" + "id=" + id + ", title=" + title + ", description=" + description + '}';
    }
}
