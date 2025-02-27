/*
 * Author: Md. Sakil Ahmed
 */

package com.example.taskbazaar.dto;

import java.io.Serializable;

public record PostDTO(String author, String title, String content, int postId) implements Serializable {

    public PostDTO(String author, String title, String content) {
        this(author, title, content, 0);
    }

    public PostDTO(String author, int postId) {
        this(author, null, null, postId);
    }

    public PostDTO(int postId, String title, String content) {
        this(null, title, content, postId);
    }
}
