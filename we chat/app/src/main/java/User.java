package com.example.wechat;

import android.net.Uri;
import android.widget.EditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class User {
    String email, userName, password, status, lastMessage, profilePic, userId;

    public User() {
    }

    public User(String email, String password, String id, String name, String image, String status) {
        this.email=email;
        this.password=password;
        this.userId=id;
        this.userName=name;
        this.profilePic=image;
        this.status=status;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
