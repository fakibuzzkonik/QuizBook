package com.codeontime.munna;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.TextValueSanitizer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import static android.view.View.GONE;

public class LoginProfile extends AppCompatActivity {
    private ImageView mUserProfileImage;
    private TextView mHeadText, mUserNameText, mExamAmountText, mUserTypeText;
    private Button mSignOutBtn;

    //Firebase Auth
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_profile);
        mUserProfileImage = (ImageView)findViewById(R.id.profile_picture);
        mHeadText =         (TextView)findViewById(R.id.profile_head_text);
        mUserNameText =     (TextView)findViewById(R.id.profile_name_text);
        mExamAmountText =   (TextView)findViewById(R.id.profile_exam_amount_text);
        mUserTypeText =     (TextView)findViewById(R.id.profile_user_type_text);

        mSignOutBtn = (Button)findViewById(R.id.profile_sign_out_btn);

        mSignOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
            }
        });
        //Login Check
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    String dsUserName = user.getDisplayName();
                    mUserNameText.setText(dsUserName);
                    Toast.makeText(getApplicationContext(),"Welcome "+dsUserName, Toast.LENGTH_SHORT).show();;
                    ///getUserData();
                }else{
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        };
        getUserData();
    }
    private String dUserUID = "NO";
    private void getUserData() {
        dUserUID = FirebaseAuth.getInstance().getUid();
        if(dUserUID.equals("")){
            Toast.makeText(getApplicationContext(),"Logged in but UID 404", Toast.LENGTH_SHORT).show();;
        }else{
//Please Modify Database Auth READ WRITE Condition if its not connect to database
            Toast.makeText(getApplicationContext(), "Checking Database", Toast.LENGTH_SHORT).show();;
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference user_data_ref = db.collection("Quiz").document("REGISTER");
            user_data_ref.collection("NORMAL_USER").document(dUserUID).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                //Toast.makeText(getApplicationContext(),"User Information Found", Toast.LENGTH_SHORT).show();;

                                String dUserName = documentSnapshot.getString("name");
                                String dUserEmail = documentSnapshot.getString("email");
                                String dRegTime = documentSnapshot.getString("reg_date");
                                String dUserUniversity = documentSnapshot.getString("university");
                                String UserUID = documentSnapshot.getString("uid");
                                String dUserPhotoURL = documentSnapshot.getString("photoURL");
                                String dUserPhoneNo = documentSnapshot.getString("phone_no");
                                String dTotal = documentSnapshot.getString("total");
                                String dHomeAddress = documentSnapshot.getString("homeAddress");
                                String dUserType = documentSnapshot.getString("userType");

                                long diPoints = documentSnapshot.getLong("points");

                                if(UserUID.equals(dUserUID)){
                                    Picasso.get().load(dUserPhotoURL).into(mUserProfileImage);
                                    /* //Remain
                                    mUserProfileProgressBar.setVisibility(View.GONE);
                                    mCheckUserName.setText(dUserName);
                                    mCheckUserBio.setText(dHomeAddress);
                                    mCheckUserPhoneNumber.setText("Phone no : "+dUserPhoneNo);

                                    long diDate2 = 1623163000000L;
                                    diLastActivity2 = diLastActivity2 + diDate2;
                                    mCheckUserActivity.setText(TimeAgo(diLastActivity2));   //2 minutes ago setup

                                    if(dUserType.equals("User")){
                                        mRideOrdersBtn.setVisibility(GONE);
                                    }else if(dUserType.equals("Rider")){
                                        mMyShopsOrderListBtn.setVisibility(GONE);
                                    }

                                    methodGetUserTotalData(dTotal);
                                    methodGetBirthRegDate(dBirthRegTime);
                                    //savedUserDataPhoneMemory(dUserName, UserUID, dTotal, dUserPhotoURL);  //comment*/
                                }else{
                                    Toast.makeText(getApplicationContext(),"User Inforamtion Not Matched", Toast.LENGTH_SHORT).show();;
                                }

                            }else{
                                //User has no data saved
                                Toast.makeText(getApplicationContext(),"User Inforamtion 404", Toast.LENGTH_SHORT).show();;
                                Intent intent = new Intent(getApplicationContext(), LoginRegistration.class);
                                //intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent);
                                //finish();
                            }
                        }
                    });
        }
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