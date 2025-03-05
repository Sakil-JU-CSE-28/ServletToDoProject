/*
 * author : Md. Sakil Ahmed
 */

package com.example.taskbazaar.service;

import com.example.taskbazaar.dao.PostDao;
import com.example.taskbazaar.dto.PostDTO;
import com.example.taskbazaar.exception.PostException;

import java.util.List;


public class PostService {

    private static volatile PostService postService = null;
    private final PostDao postDao = PostDao.getInstance();

    private PostService() {}

    public static PostService getInstance() {
        if (postService == null) {
            synchronized (PostService.class) {
                if (postService == null) {
                    postService = new PostService();
                }
            }
        }
        return postService;
    }

    public List<PostDTO> getPosts() throws PostException {
        return postDao.getPosts();
    }

    public boolean create(String username, String title, String content) throws PostException {
        return postDao.add(new PostDTO(username, title, content));
    }

    public boolean delete(int postId) throws Exception {
        return postDao.delete(postId);
    }

    public boolean update(int postId, String newTitle, String newContent) throws PostException {
        return postDao.update(new PostDTO(postId, newTitle, newContent));
    }

    public PostDTO getById(int id) throws PostException {
        PostDTO post = postDao.getById(id);
        if(post == null) {
            throw new PostException("Post not found for id " + id);
        }
        return post;
    }

}