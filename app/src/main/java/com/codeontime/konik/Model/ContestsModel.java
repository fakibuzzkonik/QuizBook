package com.codeontime.konik.Model;

public class ContestsModel {
    private String CoUID = "NO";
    private String CoName = "NO";
    private String CoPhotoUrl = "NO";
    private String CoSyllabus = "NO";
    private String CoPassword = "NO";
    private String CoExtra = "NO";
    private String CoCreator = "NO";
    private long CoiViewCount = 0;
    private long CoiTotalQues = 0;
    private long CoiPriority = 0;
    private long CoiCoins        = 0;
    private long CoiTotalParticipant = 0;
    private long CoiDuration = 0;
    private long CoiDate = 0;

    public ContestsModel() {
    }

    public ContestsModel(String coUID, String coName, String coPhotoUrl, String coSyllabus, String coPassword, String coExtra, String coCreator, long coiViewCount, long coiTotalQues, long coiPriority, long coiCoins, long coiTotalParticipant, long coiDuration, long coiDate) {
        CoUID = coUID;
        CoName = coName;
        CoPhotoUrl = coPhotoUrl;
        CoSyllabus = coSyllabus;
        CoPassword = coPassword;
        CoExtra = coExtra;
        CoCreator = coCreator;
        CoiViewCount = coiViewCount;
        CoiTotalQues = coiTotalQues;
        CoiPriority = coiPriority;
        CoiCoins = coiCoins;
        CoiTotalParticipant = coiTotalParticipant;
        CoiDuration = coiDuration;
        CoiDate = coiDate;
    }


    public String getCoSyllabus() {
        return CoSyllabus;
    }

    public String getCoUID() {
        return CoUID;
    }

    public String getCoName() {
        return CoName;
    }

    public String getCoPhotoUrl() {
        return CoPhotoUrl;
    }

    public String getCoPassword() {
        return CoPassword;
    }

    public String getCoExtra() {
        return CoExtra;
    }

    public String getCoCreator() {
        return CoCreator;
    }

    public long getCoiViewCount() {
        return CoiViewCount;
    }

    public long getCoiTotalQues() {
        return CoiTotalQues;
    }

    public long getCoiPriority() {
        return CoiPriority;
    }

    public long getCoiCoins() {
        return CoiCoins;
    }

    public long getCoiTotalParticipant() {
        return CoiTotalParticipant;
    }

    public long getCoiDuration() {
        return CoiDuration;
    }

    public long getCoiDate() {
        return CoiDate;
    }
}
