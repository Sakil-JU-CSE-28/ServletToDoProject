/*
 * author : Md. Sakil Ahmed
 */

package com.example.taskbazaar.service;

import com.example.taskbazaar.dao.PostDao;
import com.example.taskbazaar.dto.PostDTO;
import com.example.taskbazaar.model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class PostService {

    private static volatile PostService postService = null;
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    private PostService() {
        logger.info("PostService created");
    }

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

    public List<Post> getAllPosts() {
        List<Post> posts = PostDao.getAllPosts();
        return posts;
    }

    public boolean createPost(String username, String title, String content) {
        boolean isSuccessfull = PostDao.addPost(new PostDTO(username, title, content));
        return isSuccessfull;
    }

    public boolean deletePost(int postId) throws Exception {
        boolean isSuccessfull = PostDao.deletePost(postId);
        return isSuccessfull;
    }

    public boolean updatePost(int postId, String newTitle, String newContent) {
        boolean isSuccessfull = PostDao.updatePost(new PostDTO(postId, newTitle, newContent));
        return isSuccessfull;
    }
}