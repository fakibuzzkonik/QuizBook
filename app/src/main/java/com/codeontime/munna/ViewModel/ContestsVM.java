package com.codeontime.munna.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.codeontime.munna.Model.ContestsModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ContestsVM extends AndroidViewModel {
    public ContestsVM(@NonNull Application application) {
        super(application);
        Log.d("ViewModel", "allViewModel:4 ContestsVM start");
    }
    public MutableLiveData mLiveData;
    public MutableLiveData<List<ContestsModel>> ContestsList(String dsBookUID) {
        List<ContestsModel> listContestItem ; listContestItem =new ArrayList<>();
        CollectionReference notebookRef;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("ViewModel", "allViewModel:4 LoadLevel4List start");

        notebookRef = db.collection("Quiz").document("Data")
                        .collection("AllBooks").document(dsBookUID).collection("AllContest");

        if(mLiveData == null) {
            mLiveData = new MutableLiveData();
            notebookRef.orderBy("CoiPriority", Query.Direction.ASCENDING).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {   //documnet er vitore je multiple document query ache er jonno for loop
                            String data = "";
                            if(queryDocumentSnapshots.isEmpty()) {
                                //String coUID, String coName, String coPhotoUrl, String coPassword, String coExtra, String coCreator,
                                // int coiViewCount, int coiTotalQues, int coiPriority, int coiCoins, int coiTotalParticipant, int coiDuration, int coiDate
                                listContestItem.add(new ContestsModel("UID","NULL", "PhotoUrl","Syllabus", "coPassword", "coExtra", "coCreator" , 0, 0, 0,0,0,0,0));
                                mLiveData.postValue(listContestItem);
                                Log.d("ViewModel", "allViewModel:4 queryDocumentSnapshots empty");
                            }else {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    ContestsModel contestsModel = documentSnapshot.toObject(ContestsModel.class);
                                    //messageModel.setDocumentID(documentSnapshot.getId());
                                    String dsContest_UID = documentSnapshot.getId();
                                    String dsContest_Name = contestsModel.getCoName();
                                    String dsContest_PhotoUrl = contestsModel.getCoPhotoUrl();
                                    String dsContest_Syllabus = contestsModel.getCoSyllabus();
                                    String dsContest_Password = contestsModel.getCoPassword();
                                    String dsContest_Extra = contestsModel.getCoExtra();
                                    String dsContest_Creator = contestsModel.getCoCreator();

                                    long diContest_ViewCount = contestsModel.getCoiViewCount();
                                    long diContest_TotalQuestion = contestsModel.getCoiTotalQues();
                                    long diContest_Priority = contestsModel.getCoiPriority();
                                    long diContest_Coins = contestsModel.getCoiCoins();
                                    long diContest_Participent = contestsModel.getCoiTotalParticipant();
                                    long diContest_Duration = contestsModel.getCoiDuration();
                                    long diContest_Date = contestsModel.getCoiDate();
                                    //String bookUID, String bookName, String bookPhotoUrl, String bookBio, String bookCreator, String bookExtra, String bookPassword, int bookiPriority, int bookiViewCount, int bookiTotalLevel
                                    listContestItem.add(new ContestsModel(dsContest_UID,dsContest_Name, dsContest_PhotoUrl,dsContest_Syllabus,dsContest_Password, dsContest_Extra,  dsContest_Creator,
                                            diContest_ViewCount, diContest_TotalQuestion, diContest_Priority, diContest_Coins,diContest_Participent ,diContest_Duration ,diContest_Date ));
                                    mLiveData.postValue(listContestItem);
                                }
                                mLiveData.postValue(listContestItem);    //All Items level 4 , it is a one type category

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }

        return mLiveData;
    }
}
