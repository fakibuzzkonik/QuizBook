package com.codeontime.konik.Model;

public class QuestionModel {
        private String QuesUID = "NO";
        private String UserSelectedAnswer = "NO";
        private int AnswerOnIndex = 1;
        private String Ques = "NO";
        private String Ans = "NO";
        private String F2 = "NO";
        private String F3 = "NO";
        private String F4 = "NO";
        private String Solution = "NO";
        private String PhotoUrl = "NO";
        private String Extra = "NO";
        private long Serial = 0;

    public QuestionModel() {
    }

    public QuestionModel(String quesUID, String userSelectedAnswer, int answerOnIndex, String ques, String ans, String f2, String f3, String f4, String solution, String photoUrl, String extra, long serial) {
        QuesUID = quesUID;
        UserSelectedAnswer = userSelectedAnswer;
        AnswerOnIndex = answerOnIndex;
        Ques = ques;
        Ans = ans;
        F2 = f2;
        F3 = f3;
        F4 = f4;
        Solution = solution;
        PhotoUrl = photoUrl;
        Extra = extra;
        Serial = serial;
    }

    public String getQuesUID() {
        return QuesUID;
    }

    public String getUserSelectedAnswer() {
        return UserSelectedAnswer;
    }

    public String getQues() {
        return Ques;
    }

    public String getAns() {
        return Ans;
    }

    public String getF2() {
        return F2;
    }

    public String getF3() {
        return F3;
    }

    public String getF4() {
        return F4;
    }

    public String getSolution() {
        return Solution;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public String getExtra() {
        return Extra;
    }

    public long getSerial() {
        return Serial;
    }

    public int getAnswerOnIndex() {
        return AnswerOnIndex;
    }

    public void setAnswerOnIndex(int answerOnIndex) {
        AnswerOnIndex = answerOnIndex;
    }

    public void setUserSelectedAnswer(String userSelectedAnswer) {
        UserSelectedAnswer = userSelectedAnswer;
    }

    public void setAns(String ans) {
        Ans = ans;
    }

    public void setF2(String f2) {
        F2 = f2;
    }

    public void setF3(String f3) {
        F3 = f3;
    }

    public void setF4(String f4) {
        F4 = f4;
    }
}
