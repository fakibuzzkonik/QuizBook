package com.codeontime.munna.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codeontime.munna.Adapter.QuestionsListAdapter;
import com.codeontime.munna.Model.QuestionModel;
import com.codeontime.munna.R;
import com.codeontime.munna.RecylerviewQuestionClickInterface;
import com.codeontime.munna.ViewModel.QuestionsListVM;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class QuestionsList extends AppCompatActivity implements RecylerviewQuestionClickInterface {
    //RecyclerView
    private RecyclerView mQuesListRecylerview;
    QuestionModel TestModel;
    List<QuestionModel> listQuestionItem = new ArrayList<>();
    QuestionsListAdapter mQuestion_adapter;

    private Button mQuesListSubmitBtn ;
    //Firebase Auth
    private String dUserUID = "NO";
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_list);
        mQuesListSubmitBtn = (Button)findViewById(R.id.ques_list_submit_btn);
        mQuesListRecylerview = (RecyclerView)findViewById(R.id.ques_list_recyclerview);
        

        mQuesListSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitAnswerMethod();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Toast.makeText(getApplicationContext(),"Contest Add Activity", Toast.LENGTH_SHORT).show();;
                    dUserUID = user.getUid();
                    getIntentMethod();
                }else{
                    Toast.makeText(getApplicationContext(),"Please Login", Toast.LENGTH_SHORT).show();;
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        };
    }

    private void SubmitAnswerMethod() {
        String dsUserChoosedMCQ = "";
        int total_question = listQuestionItem.size();
        int AnswerNot = 0, AnswerWrong = 0, AnswerCorrect = 0;
        QuestionModel questionModelSubmit;
        for(int i = 0; i<total_question; i++){
            questionModelSubmit = listQuestionItem.get(i);
            String dsUserAnswer = questionModelSubmit.getUserSelectedAnswer();
            String dsOriginalAnswer = "NX ";
            String dsFalseAnswer = "NX";
            int diAnswerIndexNo = questionModelSubmit.getAnswerOnIndex();
            if (diAnswerIndexNo== 1){
                dsOriginalAnswer = questionModelSubmit.getAns();
            }else if(diAnswerIndexNo == 2){
                dsOriginalAnswer = questionModelSubmit.getF2();
                dsFalseAnswer = questionModelSubmit.getAns();
                questionModelSubmit.setF2(dsFalseAnswer);
                questionModelSubmit.setAns(dsOriginalAnswer);
            }else if(diAnswerIndexNo == 3){
                dsOriginalAnswer = questionModelSubmit.getF3();
                dsFalseAnswer = questionModelSubmit.getAns();
                questionModelSubmit.setF3(dsFalseAnswer);
                questionModelSubmit.setAns(dsOriginalAnswer);
            }else if(diAnswerIndexNo == 4){
                dsOriginalAnswer = questionModelSubmit.getF4();
                dsFalseAnswer = questionModelSubmit.getAns();
                questionModelSubmit.setF4(dsFalseAnswer);
                questionModelSubmit.setAns(dsOriginalAnswer);
            }
            if(dsUserAnswer.equals("NO")){
                AnswerNot++;
            }else if(dsUserAnswer.equals(dsOriginalAnswer)){
                AnswerCorrect++;
            }else{
                AnswerWrong++;
            }

            if(questionModelSubmit.getAns() == dsUserAnswer){
                dsUserChoosedMCQ = dsUserChoosedMCQ + "1";
            }else if(questionModelSubmit.getF2() == dsUserAnswer){
                dsUserChoosedMCQ = dsUserChoosedMCQ + "2";
            }else if(questionModelSubmit.getF3() == dsUserAnswer){
                dsUserChoosedMCQ = dsUserChoosedMCQ + "3";
            }else if(questionModelSubmit.getF4() == dsUserAnswer){
                dsUserChoosedMCQ = dsUserChoosedMCQ + "4";
            }else if("NO" == dsUserAnswer){
                dsUserChoosedMCQ = dsUserChoosedMCQ + "0";
            }
            //Toast.makeText(getApplicationContext(),i+1+"Selected = "+dsUserAnswer+ "Ori= "+dsOriginalAnswer, Toast.LENGTH_SHORT).show();;
        }
        Toast.makeText(getApplicationContext(),"Correct = "+AnswerCorrect+"\nWrong = "+AnswerWrong+"\nNot Answer = "+AnswerNot, Toast.LENGTH_LONG).show();;
        Toast.makeText(getApplicationContext(),"UserData = "+dsUserChoosedMCQ, Toast.LENGTH_LONG).show();;

        //Sent Data to Server
        Map<String, Object> note = new HashMap<>();
        note.put("ParticipantUID", dUserUID);
        note.put("Choosed", dsUserChoosedMCQ);
        note.put("Result", AnswerCorrect);
        note.put("LiveRank", 0);
        note.put("Extra", "NO");

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference quesRef = db.collection("Quiz").document("Data")
                .collection("AllBooks").document(dsBookUID).collection("AllContest").document(dsContestUID);
        quesRef.collection("Participant").document(dUserUID).set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(),"Submitted", Toast.LENGTH_SHORT).show();;
                Intent intent = new Intent(getApplicationContext(), ContestDetails.class);
                intent.putExtra("dsBookUID", dsBookUID);
                intent.putExtra("dsBookName", dsBookName);
                intent.putExtra("dsContestUID", dsContestUID);
                intent.putExtra("dsContestName", dsContestName);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Failed to Submit", Toast.LENGTH_SHORT).show();;
            }
        });

    }

    //View Model
    private QuestionsListVM contestsVM;
    private void callViewModel() {
        Log.d("ViewModel", "allViewModel:2 ContestActivityVM start");
        contestsVM = new ViewModelProvider(this).get(QuestionsListVM.class);
        contestsVM.ContestsList(dsBookUID,dsContestUID).observe(this, new Observer<List<QuestionModel>>() {
            @Override
            public void onChanged(List<QuestionModel> contest_model_list) {
                Log.d("ViewModel", "allViewModel:2 onChanged listview4 size = "+contest_model_list.size());
                if (contest_model_list.get(0).getQuesUID().equals("NULL")){
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "No Items Found", Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                }else{
                    listQuestionItem = contest_model_list;
                    Log.d("ViewModel", "allViewModel:6 ContestActivityVM start" + contest_model_list.size());
                    //Toast.makeText(getApplicationContext(),"xx = "+contest_model_list.size(), Toast.LENGTH_SHORT).show();;
                    showRecylerView();
                }
            }
        });
    }


    public void showRecylerView(){
        int diListSize = listQuestionItem.size();

        for(int i =0; i<diListSize; i++){

            //User Answered Code Showing
            if(dsUserSelectedAnsered.length() > 0){
                char dcUserAnswerBtnNo = dsUserSelectedAnsered.charAt(i);
                int diUserAnswerBtnNo = dcUserAnswerBtnNo-48;
                QuestionModel questionModelSetUserAnswer = listQuestionItem.get(i);
                if(diUserAnswerBtnNo == 1){
                    questionModelSetUserAnswer.setUserSelectedAnswer(questionModelSetUserAnswer.getAns());
                }else if(diUserAnswerBtnNo == 2){
                    questionModelSetUserAnswer.setUserSelectedAnswer(questionModelSetUserAnswer.getF2());
                }else if(diUserAnswerBtnNo == 3){
                    questionModelSetUserAnswer.setUserSelectedAnswer(questionModelSetUserAnswer.getF3());
                }else if(diUserAnswerBtnNo == 4){
                    questionModelSetUserAnswer.setUserSelectedAnswer(questionModelSetUserAnswer.getF4());
                }
            }//Finished User Answered Code Showing


            //Live Exam Code
            QuestionModel questionModelModifyList;
            questionModelModifyList = listQuestionItem.get(i);
            Random randomNum = new Random();
            int diRandomNum = randomNum.nextInt(3)+1;
            if(diRandomNum == 2){
                String dsFalseAnswer = questionModelModifyList.getF2();
                String dsAnswer = questionModelModifyList.getAns();
                questionModelModifyList.setAns(dsFalseAnswer);
                questionModelModifyList.setF2(dsAnswer);
                questionModelModifyList.setAnswerOnIndex(2);
            }else if(diRandomNum == 3){
                String dsFalseAnswer = questionModelModifyList.getF3();
                String dsAnswer = questionModelModifyList.getAns();
                questionModelModifyList.setAns(dsFalseAnswer);
                questionModelModifyList.setF3(dsAnswer);
                questionModelModifyList.setAnswerOnIndex(3);
            }else if(diRandomNum == 4){
                String dsFalseAnswer = questionModelModifyList.getF4();
                String dsAnswer = questionModelModifyList.getAns();
                questionModelModifyList.setAns(dsFalseAnswer);
                questionModelModifyList.setF4(dsAnswer);
                questionModelModifyList.setAnswerOnIndex(4);
            }
            listQuestionItem.set(i, questionModelModifyList);
        }
        QuestionModel questionModelModifyList2;
        questionModelModifyList2 = listQuestionItem.get(0);
        Toast.makeText(getApplicationContext(),"Answer A  = "+questionModelModifyList2.getAns(), Toast.LENGTH_SHORT).show();;
        //Collections.reverse(listBook);
        mQuestion_adapter = new QuestionsListAdapter(QuestionsList.this,listQuestionItem,QuestionsList.this);
        mQuestion_adapter.notifyDataSetChanged();
        //

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mQuesListRecylerview.setLayoutManager(new GridLayoutManager(QuestionsList.this,2));
            mQuesListRecylerview.setAdapter(mQuestion_adapter);
        } else {
            mQuesListRecylerview.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));
            mQuesListRecylerview.setAdapter(mQuestion_adapter);
        }
    }

    String dsBookUID = "NO", dsBookName = "NO";
    String dsContestUID = "NO", dsContestName = "NO";
    String dsBtnMode = "NO";
    long diContestDuration = 0;
    private boolean intentFoundError = true;
    private void getIntentMethod() {
        //////////////GET INTENT DATA
        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {
            dsBookUID = intent.getExtras().getString("dsBookUID");    //Sylhet
            dsBookName = intent.getExtras().getString("dsBookName");    //Grocery or Food or Home Services
            dsContestUID = intent.getExtras().getString("dsContestUID");    //Sylhet
            dsContestName = intent.getExtras().getString("dsContestName");    //Grocery or Food or Home Services
            dsBtnMode = intent.getExtras().getString("dsBtnMode","NO");    //Grocery or Food or Home Services
            diContestDuration = intent.getExtras().getLong("diContestDuration",0);    //Grocery or Food or Home Services
            intentFoundError = CheckIntentMethod(dsBookUID);
            intentFoundError = CheckIntentMethod(dsBookName);
            intentFoundError = CheckIntentMethod(dsContestUID);
            intentFoundError = CheckIntentMethod(dsContestName);
            intentFoundError = CheckIntentMethod(dsContestName);



            if(!intentFoundError){
                if(dsBtnMode.equals("ShowResult")){
                    mQuesListSubmitBtn.setVisibility(View.GONE);
                    getUserResult();
                }else if(dsBtnMode.equals("StartExam")){
                    callViewModel();
                    CountDownStart(diContestDuration);
                }else {
                    Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_SHORT).show();;
                }

            }else{
                Toast.makeText(getApplicationContext(),"Intent error", Toast.LENGTH_SHORT).show();;
            }
        }else{
            dsBookUID = "NO";
            dsBookName = "NO";
            dsContestUID = "NO";
            dsContestName = "NO";
            dsBtnMode = "NO";
            Toast.makeText(getApplicationContext(),"Intent error", Toast.LENGTH_SHORT).show();;

        }

    }
    private android.os.Handler mHandler = new Handler();
    private Runnable mRunnable;
    private static final int LOCATION_UPDATE_INTERVAL = 1000;
    private void CountDownStart(long duration) {
        Toast.makeText(getApplicationContext(), "duration"+duration , Toast.LENGTH_SHORT).show();
        diContestDuration = duration*1000;
        diContestDuration = diContestDuration*60;
            /*mHandler.postDelayed(mRunnable = new Runnable() {
                @Override
                public void run() {
                    SubmitAnswerMethod();
                    //mHandler.postDelayed(mRunnable, LOCATION_UPDATE_INTERVAL);
                }
            }, diContestDuration);*/
        new CountDownTimer(diContestDuration , 1000 ) {

            public void onTick(long millisUntilFinished) {
                long diff = millisUntilFinished;
                long sec = 0;
                long minute = 0;
                diff = diff/1000;
                String dsTitle = "Remain ";
                if(diff/60 > 0){
                    minute = diff/60;
                    dsTitle += minute + "min ";
                }
                if(diff%60 != 0){
                    sec = diff%60;
                    dsTitle += sec +"sec";
                }
                setTitle(dsTitle);
                //here you can have your logic to set text to edittext
            }
            public void onFinish() {
                setTitle("Finished");
                SubmitAnswerMethod();
            }

        }.start();
    }


    private boolean CheckIntentMethod(String dsTestIntent){
        if(TextUtils.isEmpty(dsTestIntent)) {
            dsTestIntent= "NO";
            Toast.makeText(getApplicationContext(), "intent NULL  " , Toast.LENGTH_SHORT).show();
        }else if (dsBookUID.equals("")){
            dsTestIntent= "NO";
            Toast.makeText(getApplicationContext(), "intent 404" , Toast.LENGTH_SHORT).show();
        }else{
            intentFoundError = false;
        }
        return intentFoundError;
    }
    String dsUserSelectedAnsered = "";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private void getUserResult() {
            DocumentReference quesRef = db.collection("Quiz").document("Data")
                    .collection("AllBooks").document(dsBookUID).collection("AllContest").document(dsContestUID);
            quesRef.collection("Participant").document(dUserUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        dsUserSelectedAnsered = documentSnapshot.getString("Choosed");
                        callViewModel();
                    }else{
                        Toast.makeText(getApplicationContext(), "User Answer Failed"+dsBookUID , Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onARadioBtnCLick(int position) {
        String dsA_Choosed = listQuestionItem.get(position).getAns();
        QuestionModel questionModelModify = listQuestionItem.get(position);
        questionModelModify.setUserSelectedAnswer(dsA_Choosed);
        listQuestionItem.set(position, questionModelModify);
        //Toast.makeText(getApplicationContext(),"Choosed = "+dsA_Choosed+"Answer="+questionModelModify.getAns(), Toast.LENGTH_SHORT).show();;

    }

    @Override
    public void onBRadioBtnCLick(int position) {
        String dsB_Choosed = listQuestionItem.get(position).getF2();
        QuestionModel questionModelModify = listQuestionItem.get(position);
        questionModelModify.setUserSelectedAnswer(dsB_Choosed);
        listQuestionItem.set(position, questionModelModify);
        //Toast.makeText(getApplicationContext(),"Choosed = "+dsB_Choosed+"Answer="+questionModelModify.getAns(), Toast.LENGTH_SHORT).show();;
    }

    @Override
    public void onCRadioBtnCLick(int position) {
        String dsC_Choosed = listQuestionItem.get(position).getF3();
        QuestionModel questionModelModify = listQuestionItem.get(position);
        questionModelModify.setUserSelectedAnswer(dsC_Choosed);
        listQuestionItem.set(position, questionModelModify);
        //Toast.makeText(getApplicationContext(),"Choosed = "+dsC_Choosed, Toast.LENGTH_SHORT).show();;
    }

    @Override
    public void onDRadioBtnCLick(int position) {
        String dsD_Choosed = listQuestionItem.get(position).getF4();
        QuestionModel questionModelModify = listQuestionItem.get(position);
        questionModelModify.setUserSelectedAnswer(dsD_Choosed);
        listQuestionItem.set(position, questionModelModify);
        //Toast.makeText(getApplicationContext(),"Choosed = "+dsD_Choosed, Toast.LENGTH_SHORT).show();;
    }
    @Override
    public String mQuestionSoltiontext(int position) {
        String dsSoltuionText = listQuestionItem.get(position).getSolution();
        return dsSoltuionText;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}