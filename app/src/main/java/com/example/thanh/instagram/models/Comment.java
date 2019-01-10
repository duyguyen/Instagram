package com.example.thanh.instagram.models;

public class Comment {

    // == constants ==
    private int comment_id, user_id, story_id;
    private String user_name, profile_image, comment_text, time;

    // == constructor ==
    public Comment(int comment_id, int user_id, int story_id, String user_name, String profile_image, String comment_text, String time) {
        this.comment_id = comment_id;
        this.user_id = user_id;
        this.story_id = story_id;
        this.user_name = user_name;
        this.profile_image = profile_image;
        this.comment_text = comment_text;
        this.time = time;
    }


    // == getters & getters ==
    public void setTime(String time) {
        this.time = time;
    }
    public String getTime() {
        return time;
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getStory_id() {
        return story_id;
    }

    public void setStory_id(int story_id) {
        this.story_id = story_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }
}
