package com.codeontime.konik.View;

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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codeontime.konik.Adapter.BookAdapter;
import com.codeontime.konik.LoginCheck;
import com.codeontime.konik.LoginRegistration;
import com.codeontime.konik.Model.BookModel;
import com.codeontime.konik.R;
import com.codeontime.konik.RecylerviewClickInterface;
import com.codeontime.konik.ViewModel.MainActivityVM;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecylerviewClickInterface {
    private Button mLoginBtn;
    private Button mAddBook;

    //RecyclerView
    private RecyclerView mBook_RecyclerView;
    List<BookModel> listBookItem;
    BookAdapter mbook_adapter;

    //Firebase Auth
    private String dUserUID = "NO";
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAddBook = (Button)findViewById(R.id.main_add_book);
        mLoginBtn = (Button)findViewById(R.id.main_login_btn);
        mBook_RecyclerView = (RecyclerView)findViewById(R.id.main_book_recylerview);
        listBookItem = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    dUserUID = user.getUid();
                    mLoginBtn.setText("My Profile");
                    checkUserType();
                }else{
                    callViewModel();
                    /*Toast.makeText(getApplicationContext(),"Please Login", Toast.LENGTH_SHORT).show();;
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);*/
                }
            }
        };

        mAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BookAdd.class);
                startActivity(intent);
            }
        });
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginCheck.class);
                intent.setFlags( intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
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
                        mAddBook.setVisibility(View.VISIBLE);
                    }else{
                        mAddBook.setVisibility(View.GONE);

                    }
                    callViewModel();
                }else{
                    Toast.makeText(getApplicationContext(),"Error User Information Not Found", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoginRegistration.class);
                    //intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        });
    }
    //View Model
    private MainActivityVM mainActivityVM;
    private void callViewModel() {
        Log.d("ViewModel", "allViewModel:1 mainActivityVM start");
        mainActivityVM = new ViewModelProvider(this).get(MainActivityVM.class);
        mainActivityVM.LoadLevel4List().observe(this, new Observer<List<BookModel>>() {
            @Override
            public void onChanged(List<BookModel> level_d_models) {
                Log.d("ViewModel", "allViewModel:1 onChanged listview4 size = "+level_d_models.size());
                if (level_d_models.get(0).getBookName().equals("NULL")){
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
                    mbook_adapter = new BookAdapter(MainActivity.this,level_d_models,MainActivity.this);
                    mbook_adapter.notifyDataSetChanged();
                    //
                    listBookItem = level_d_models;
                    int orientation = getResources().getConfiguration().orientation;
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        mBook_RecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,2));
                        mBook_RecyclerView.setAdapter(mbook_adapter);
                    } else {
                        mBook_RecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));
                        mBook_RecyclerView.setAdapter(mbook_adapter);
                    }
                }
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        String dsBookUID = listBookItem.get(position).getBookUID();
        String dsBookName = listBookItem.get(position).getBookName();

            Intent intent = new Intent(getApplicationContext(), Contests.class);
            intent.putExtra("dsBookUID", dsBookUID);
            intent.putExtra("dsBookName", dsBookName);
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