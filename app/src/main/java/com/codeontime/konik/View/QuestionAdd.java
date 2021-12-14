package com.codeontime.konik.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codeontime.konik.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class QuestionAdd extends AppCompatActivity {
    private TextView mHeadText;
    private EditText mTypeQuestionEdit, mTypeAnswerEdit, mF2AnswerEdit, mF3AnswerEdit, mF4AnswerEdit, mSolutionDetailsEdit;
    private Button mAddBtn;

    private String dsQuestion = "NO", dsAnswer = "NO", dsF2 = "NO", dsF3 = "NO", dsF4="NO", dsSolution = "NO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_add);
        getIntentMethod();
        mHeadText = (TextView)findViewById(R.id.ques_add_head_text);
        mTypeQuestionEdit = (EditText)findViewById(R.id.ques_add_edit);
        mTypeAnswerEdit = (EditText)findViewById(R.id.ques_add_answer_edit);
        mF2AnswerEdit = (EditText)findViewById(R.id.ques_add_false2_edit);
        mF3AnswerEdit = (EditText)findViewById(R.id.ques_add_false3_edit);
        mF4AnswerEdit = (EditText)findViewById(R.id.ques_add_false4_edit);
        mSolutionDetailsEdit = (EditText)findViewById(R.id.ques_add_solution);
        mAddBtn = (Button)findViewById(R.id.ques_add_btn) ;
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkData();
            }
        });


    }

    private void checkData() {
        dsQuestion = mTypeQuestionEdit.getText().toString();
        dsAnswer = mTypeAnswerEdit.getText().toString();
        dsF2 = mF2AnswerEdit.getText().toString();
        dsF3 = mF3AnswerEdit.getText().toString();
        dsF4 = mF4AnswerEdit.getText().toString();
        dsSolution = mSolutionDetailsEdit.getText().toString();
        if(dsQuestion.equals("NO") || mTypeAnswerEdit.equals("NO") || mF2AnswerEdit.equals("NO") || mF3AnswerEdit.equals("NO") || mF4AnswerEdit.equals("NO") ){
            Toast.makeText(getApplicationContext(),"Please Fillup All", Toast.LENGTH_SHORT).show();;
        }else if(dsQuestion.equals("") || mTypeAnswerEdit.equals("") || mF2AnswerEdit.equals("") || mF3AnswerEdit.equals("") || mF4AnswerEdit.equals("") ) {

        }else{
            uploadData();
        }
    }
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private void uploadData() {

        Map<String, Object> note = new HashMap<>();
        note.put("Ques", dsQuestion);
        note.put("Ans", dsAnswer);
        note.put("F2", dsF2);
        note.put("F3", dsF3);
        note.put("F4", dsF4);
        note.put("Solution", dsSolution);
        note.put("PhotoUrl", "NO");

        note.put("Extra", "NO"); // Date of Contest


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        DocumentReference quesRef = db.collection("Quiz").document("Data")
                .collection("AllBooks").document(dsBookUID).collection("AllContest").document(dsContestUID);

        quesRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
               if(documentSnapshot.exists()){
                   long Serial = documentSnapshot.getLong("CoiTotalQues");

                   note.put("Serial", Serial+1);
                   quesRef.collection("AllQuestion").add(note)
                           .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                               @Override
                               public void onSuccess(DocumentReference documentReference) {
                                   Toast.makeText(getApplicationContext(),"Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                   progressDialog.dismiss();
                                   quesRef.update("CoiTotalQues",Serial+1);
                                   mTypeQuestionEdit.setText("");
                                   mTypeAnswerEdit.setText("");
                                   mF2AnswerEdit.setText("");
                                   mF3AnswerEdit.setText("");
                                   mF4AnswerEdit.setText("");
                                   mSolutionDetailsEdit.setText("");

                               }
                           }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           progressDialog.dismiss();
                           mAddBtn.setText("Try Again");
                           Toast.makeText(getApplicationContext(),"Failed Please Try Again", Toast.LENGTH_SHORT).show();

                       }
                   });
               }
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
}