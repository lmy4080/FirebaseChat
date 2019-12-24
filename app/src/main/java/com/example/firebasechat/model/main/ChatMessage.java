package com.example.firebasechat.model.main;

public class ChatMessage {
    private String id; // db에 저장할 id
    private String text; // 메시지
    private String name; // 이름
    private String photoUrl; // 프로필 사진 경로
    private String imageUrl; // 첨부 이미지 경로

    public ChatMessage() {

    }

    public ChatMessage(String text, String name, String photoUrl, String imageUrl) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
