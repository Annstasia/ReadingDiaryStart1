package com.example.readingdiary;

public class Note {
    private String path;
    private String author;
    private String title;

    public Note(String path, String author, String title){
        this.path = path;
        this.title = title;
        this.author = author;
    }

    public String getPath() {
        return path;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
