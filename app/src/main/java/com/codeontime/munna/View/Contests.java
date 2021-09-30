package com.codeontime.munna.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codeontime.munna.Adapter.ContestAdapter;
import com.codeontime.munna.Model.ContestsModel;
import com.codeontime.munna.R;
import com.codeontime.munna.RecylerviewClickInterface;
import com.codeontime.munna.ViewModel.ContestsVM;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Contests extends AppCompatActivity  implements RecylerviewClickInterface {
    private FloatingActionButton mContestAddBtn;

    //RecyclerView
    private RecyclerView mContest_RecylcerView;
    List<ContestsModel> listContestItem = new ArrayList<>();;
    ContestAdapter mContest_adapter;
    //Firebase Auth
    private String dUserUID = "NO";
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contests);
        mContestAddBtn = (FloatingActionButton)findViewById(R.id.contest_add_btn);
        mContest_RecylcerView = (RecyclerView)findViewById(R.id.contests_recylerview);

        getIntentMethod();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    dUserUID = user.getUid();
                    checkUserType();
                }else{
                    mContestAddBtn.setVisibility(View.GONE);
                    /*Toast.makeText(getApplicationContext(),"Please Login", Toast.LENGTH_SHORT).show();;
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);*/
                }
            }
        };

        mContestAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ContestAdd.class);
                intent.putExtra("dsBookUID", dsBookUID);
                intent.putExtra("dsBookName", dsBookName);
                startActivity(intent);
            }
        });

    }



    public void checkUserType(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference user_data_ref = db.collection("Quiz").document("REGISTER");
        user_data_ref.collection("NORMAL_USER").document(dUserUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String dUserType = documentSnapshot.getString("userType");
                    if(dUserType.equals("Teacher")){
                        mContestAddBtn.setVisibility(View.VISIBLE);
                    }else{
                        mContestAddBtn.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
    //View Model
    private ContestsVM contestsVM;
    private void callViewModel() {
        Log.d("ViewModel", "allViewModel:2 ContestActivityVM start");
        contestsVM = new ViewModelProvider(this).get(ContestsVM.class);
        contestsVM.ContestsList(dsBookUID).observe(this, new Observer<List<ContestsModel>>() {
            @Override
            public void onChanged(List<ContestsModel> contest_model_list) {
                Log.d("ViewModel", "allViewModel:2 onChanged listview4 size = "+contest_model_list.size());
                if (contest_model_list.get(0).getCoName().equals("NULL")){
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
                    //Collections.reverse(listBook);
                    mContest_adapter = new ContestAdapter(Contests.this,contest_model_list,Contests.this);
                    mContest_adapter.notifyDataSetChanged();
                    //
                    listContestItem = contest_model_list;
                    int orientation = getResources().getConfiguration().orientation;
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        mContest_RecylcerView.setLayoutManager(new GridLayoutManager(Contests.this,2));
                        mContest_RecylcerView.setAdapter(mContest_adapter);
                    } else {
                        mContest_RecylcerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));
                        mContest_RecylcerView.setAdapter(mContest_adapter);
                    }
                }
            }
        });
    }

    String dsBookUID = "NO", dsBookName = "NO";
    private boolean intentFoundError = true;
    private void getIntentMethod() {
        //////////////GET INTENT DATA
        final Intent intent = getIntent();
        if(intent.getExtras() != null)
        {
            dsBookUID = intent.getExtras().getString("dsBookUID");    //Sylhet
            dsBookName = intent.getExtras().getString("dsBookName");    //Grocery or Food or Home Services
            intentFoundError = CheckIntentMethod(dsBookUID);
            intentFoundError = CheckIntentMethod(dsBookName);

            if(!intentFoundError){
                callViewModel();
            }else{
                Toast.makeText(getApplicationContext(),"Intent error", Toast.LENGTH_SHORT).show();;
            }
        }else{
            dsBookUID = "NO";
            dsBookName = "NO";
            Toast.makeText(getApplicationContext(),"Intent error", Toast.LENGTH_SHORT).show();
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
    public void onItemClick(int position) {
        String dsContestUID = listContestItem.get(position).getCoUID();
        String dsContestName = listContestItem.get(position).getCoName();

        Intent intent = new Intent(getApplicationContext(), ContestDetails.class);
        intent.putExtra("dsBookUID", dsBookUID);
        intent.putExtra("dsBookName", dsBookName);
        intent.putExtra("dsContestUID", dsContestUID);
        intent.putExtra("dsContestName", dsContestName);
        startActivity(intent);
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