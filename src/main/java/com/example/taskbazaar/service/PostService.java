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
        List<PostDTO> posts = postDao.getPosts();
        return posts;
    }

    public boolean create(String username, String title, String content) throws PostException {
        boolean isSuccessfull = postDao.add(new PostDTO(username, title, content));
        return isSuccessfull;
    }

    public boolean delete(int postId) throws Exception {
        boolean isSuccessfull = postDao.delete(postId);
        return isSuccessfull;
    }

    public boolean update(int postId, String newTitle, String newContent) throws PostException {
        boolean isSuccessfull = postDao.update(new PostDTO(postId, newTitle, newContent));
        return isSuccessfull;
    }

    public PostDTO getById(int id) throws PostException {
        return postDao.getById(id);
    }
}