package com.codeontime.munna;

public interface RecylerviewQuestionClickInterface {
    void onItemClick(int position);
    void onARadioBtnCLick(int position);
    void onBRadioBtnCLick(int position);
    void onCRadioBtnCLick(int position);
    void onDRadioBtnCLick(int position);
    String mQuestionSoltiontext(int position);
    //void onItemClickAuthorID(int position, String AuthorUID);
    //implement to main class. add two method
    //declared recylerviewClickInterface on Adapter Class
    //add this argument to constructor
    //
    //
}
