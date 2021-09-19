package com.codeontime.munna.Model;

public class BookModel {

    private String BookUID= "NO";
    private String BookName = "NO";
    private String BookPhotoUrl = "NO";
    private String BookBio = "NO";
    private String BookCreator = "NO";
    private String BookExtra = "NO";
    private String BookPassword = "NO";
    private int BookiPriority = 0;
    private int BookiViewCount= 0;
    private int BookiTotalLevel= 0;

    public BookModel() {
    }

    public BookModel(String bookUID, String bookName, String bookPhotoUrl, String bookBio, String bookCreator, String bookExtra, String bookPassword, int bookiPriority, int bookiViewCount, int bookiTotalLevel) {
        BookUID = bookUID;
        BookName = bookName;
        BookPhotoUrl = bookPhotoUrl;
        BookBio = bookBio;
        BookCreator = bookCreator;
        BookExtra = bookExtra;
        BookPassword = bookPassword;
        BookiPriority = bookiPriority;
        BookiViewCount = bookiViewCount;
        BookiTotalLevel = bookiTotalLevel;
    }

    public String getBookUID() {
        return BookUID;
    }

    public String getBookName() {
        return BookName;
    }

    public String getBookPhotoUrl() {
        return BookPhotoUrl;
    }

    public String getBookBio() {
        return BookBio;
    }

    public String getBookCreator() {
        return BookCreator;
    }

    public String getBookExtra() {
        return BookExtra;
    }

    public String getBookPassword() {
        return BookPassword;
    }

    public int getBookiPriority() {
        return BookiPriority;
    }

    public int getBookiViewCount() {
        return BookiViewCount;
    }

    public int getBookiTotalLevel() {
        return BookiTotalLevel;
    }
}
