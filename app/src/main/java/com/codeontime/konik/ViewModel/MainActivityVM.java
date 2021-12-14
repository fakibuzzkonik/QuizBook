package com.codeontime.konik.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.codeontime.konik.Model.BookModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivityVM extends AndroidViewModel {
    public MainActivityVM(@NonNull Application application) {
        super(application);
        Log.d("ViewModel", "allViewModel:4 Level_D_VM start");
    }
    public MutableLiveData mLiveData;
    public MutableLiveData<List<BookModel>> LoadLevel4List() {
        List<BookModel> listBookItem ; listBookItem =new ArrayList<>();

        CollectionReference notebookRef;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("ViewModel", "allViewModel:4 LoadLevel4List start");

        notebookRef = db.collection("Quiz").document("Data").collection("AllBooks");

        if(mLiveData == null) {
            mLiveData = new MutableLiveData();
            notebookRef.orderBy("BookiPriority", Query.Direction.ASCENDING).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {   //documnet er vitore je multiple document query ache er jonno for loop
                            String data = "";
                            if(queryDocumentSnapshots.isEmpty()) {
                                //String bookName, String bookPhotoUrl, String bookBio, String bookCreator, String bookExtra, String bookPassword, int bookiPriority, int bookiViewCount, int bookiTotalLevel
                                listBookItem.add(new BookModel("UID","NULL", "PhotoUrl", "bookBio", "bookCreator", "bookExtra", "bookPassword", 0, 0, 0));
                                mLiveData.postValue(listBookItem);
                                Log.d("ViewModel", "allViewModel:4 queryDocumentSnapshots empty");
                            }else {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    BookModel book_model = documentSnapshot.toObject(BookModel.class);
                                    //messageModel.setDocumentID(documentSnapshot.getId());
                                    String dsBooK_UID = documentSnapshot.getId();
                                    String dsBooK_Name = book_model.getBookName();
                                    String dsBooK_PhotoUrl = book_model.getBookPhotoUrl();
                                    String dsBooK_Bio= book_model.getBookBio();

                                    String dsbookBio= book_model.getBookBio();
                                    String dsbookCreator= book_model.getBookCreator();
                                    String dsbookExtra = book_model.getBookExtra();
                                    String dsbookPassword = book_model.getBookPassword();
                                    int dibookiPriority = book_model.getBookiPriority();
                                    int dibookiViewCount = book_model.getBookiViewCount();
                                    int dibookiTotalLevel = book_model.getBookiTotalLevel();
                                    //String bookUID, String bookName, String bookPhotoUrl, String bookBio, String bookCreator, String bookExtra, String bookPassword, int bookiPriority, int bookiViewCount, int bookiTotalLevel
                                    listBookItem.add(new BookModel(dsBooK_UID,dsBooK_Name, dsBooK_PhotoUrl,dsbookBio, dsbookCreator,  dsbookExtra, dsbookPassword, dibookiPriority, dibookiViewCount, dibookiTotalLevel));
                                    mLiveData.postValue(listBookItem);
                                }
                                mLiveData.postValue(listBookItem);    //All Items level 4 , it is a one type category

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
