package com.codeontime.munna.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codeontime.munna.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ContestDetails extends AppCompatActivity {
    private ImageView mDetailsImage;
    private TextView mDetailsName, mDetailsSyllabus, mDetailsTotalParticipant, mDetailsDuration;
    private LinearLayout mRankLinear, mResultLinear, mSolutionLinear;
    private Button mAddQuestionBtn, mStartBtn;

    //Firebase Auth
    private String dUserUID = "NO";
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_details);
        mStartBtn = (Button)findViewById(R.id.contest_details_btn_start) ;
        mAddQuestionBtn = (Button)findViewById(R.id.contest_details_btn_add_ques);
        mSolutionLinear = (LinearLayout)findViewById(R.id.contest_details_linear_solution);
        getIntentMethod();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    dUserUID = user.getUid();
                    Toast.makeText(getApplicationContext(),"Contest Add Activity", Toast.LENGTH_SHORT).show();;

                }else{
                    /*Toast.makeText(getApplicationContext(),"Please Login", Toast.LENGTH_SHORT).show();;
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);*/
                }
            }
        };

        mAddQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QuestionAdd.class);
                intent.putExtra("dsBookUID", dsBookUID);
                intent.putExtra("dsBookName", dsBookName);
                intent.putExtra("dsContestUID", dsContestUID);
                intent.putExtra("dsContestName", dsContestName);
                startActivity(intent);
            }
        });
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPartipant("StartExam");
            }
        });
        mSolutionLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPartipant("ShowResult");
                /*Intent intent = new Intent(getApplicationContext(), QuestionsList.class);
                intent.putExtra("dsBookUID", dsBookUID);
                intent.putExtra("dsBookName", dsBookName);
                intent.putExtra("dsContestUID", dsContestUID);
                intent.putExtra("dsContestName", dsContestName);
                intent.putExtra("dsBtnMode", "ShowResult");
                startActivity(intent);*/
            }
        });
    }
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private void CheckPartipant(String dsBtnMode) {
        DocumentReference quesRef = db.collection("Quiz").document("Data")
                .collection("AllBooks").document(dsBookUID).collection("AllContest").document(dsContestUID);
        quesRef.collection("Participant").document(dUserUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(dsBtnMode.equals("ShowResult")){
                        Intent intent = new Intent(getApplicationContext(), QuestionsList.class);
                        intent.putExtra("dsBookUID", dsBookUID);
                        intent.putExtra("dsBookName", dsBookName);
                        intent.putExtra("dsContestUID", dsContestUID);
                        intent.putExtra("dsContestName", dsContestName);
                        intent.putExtra("dsBtnMode", "ShowResult");
                        startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(),"You have all ready Participated!", Toast.LENGTH_SHORT).show();; //ERROR
                    }
                }else{
                    if(dsBtnMode.equals("StartExam")){
                        Intent intent = new Intent(getApplicationContext(), QuestionsList.class);
                        intent.putExtra("dsBookUID", dsBookUID);
                        intent.putExtra("dsBookName", dsBookName);
                        intent.putExtra("dsContestUID", dsContestUID);
                        intent.putExtra("dsContestName", dsContestName);
                        intent.putExtra("dsBtnMode", "StartExam");
                        startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(),"You have not Participated!", Toast.LENGTH_SHORT).show();; //ERROR
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    String dsBookUID = "NO", dsBookName = "NO";
    String dsContestUID = "NO", dsContestName = "NO";
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
            intentFoundError = CheckIntentMethod(dsBookUID);
            intentFoundError = CheckIntentMethod(dsBookName);
            intentFoundError = CheckIntentMethod(dsContestUID);
            intentFoundError = CheckIntentMethod(dsContestName);


            if(!intentFoundError){
                //callViewModel();
            }else{
                Toast.makeText(getApplicationContext(),"Intent error", Toast.LENGTH_SHORT).show();;
            }
        }else{
            dsBookUID = "NO";
            dsBookName = "NO";
            dsContestUID = "NO";
            dsContestName = "NO";
            Toast.makeText(getApplicationContext(),"Intent error", Toast.LENGTH_SHORT).show();;
        }

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