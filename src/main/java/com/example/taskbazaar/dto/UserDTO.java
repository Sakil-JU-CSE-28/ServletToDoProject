/*
 * Author: Md. Sakil Ahmed
 */
package com.example.taskbazaar.dto;

import com.example.taskbazaar.model.User;

import java.io.Serializable;

public record UserDTO(String username, String password, String confirmPassword, String role,String salt,boolean isBlocked) implements Serializable {
    public String toString(){
        return username + "," + password + "," + confirmPassword + "," + role + "," + salt;
    }

    public static UserDTO toDTO(User user) {
        return new UserDTO(
                user.getUsername(),
                user.getPassword(),
                null,
                user.getRole(),
                user.getSalt(),
                user.isBlocked()
        );
    }
    public static User fromDTO(UserDTO userDTO) {
        return new User(
                userDTO.username(),
                userDTO.password(),
                userDTO.role(),
                userDTO.salt(),
                userDTO.isBlocked()
        );
    }
}