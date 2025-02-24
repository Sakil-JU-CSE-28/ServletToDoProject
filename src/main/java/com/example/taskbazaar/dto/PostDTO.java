/*
 * author : Md. Sakil Ahmed
 */

package com.example.taskbazaar.dto;

import java.io.Serializable;

public class PostDTO implements Serializable {
    public String author;
    public String title;
    public String content;
    public int postId;

    public PostDTO(String author, String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
    }

    public PostDTO(String author, int postId) {
        this.author = author;
        this.postId = postId;
    }

    public PostDTO(int postId, String title, String content) {
        this.postId = postId;
        this.title = title;
        this.content = content;
    }
}
