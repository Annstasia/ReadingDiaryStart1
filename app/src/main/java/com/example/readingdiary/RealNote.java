package com.example.readingdiary;

// класс для записей
public class RealNote implements Note{
    private String path;
    private String author;
    private String title;
    private long id;
    private final int type = 0;



    public RealNote(long id, String path, String author, String title){
        this.id = id;
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

    @Override
    public int getItemType() {
        return type;
    }

    @Override
    public long getID() {
        return id;
    }
}

