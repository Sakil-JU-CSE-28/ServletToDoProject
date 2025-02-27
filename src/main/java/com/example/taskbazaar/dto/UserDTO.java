/*
 * Author: Md. Sakil Ahmed
 */
package com.example.taskbazaar.dto;

import java.io.Serializable;

public record UserDTO(String username, String password, String confirmPassword, String role,String salt,boolean isBlocked) implements Serializable {
    public String toString(){
        return username + "," + password + "," + confirmPassword + "," + role + "," + salt;
    }
}