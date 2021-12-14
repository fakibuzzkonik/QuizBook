package com.codeontime.konik.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codeontime.konik.DatePickerFragment;
import com.codeontime.konik.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ContestAdd extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private ImageView mContestImage;
    private EditText mContestName, mContestSyllabus, mContestPriorityNo, mContestViewCount;
    private EditText mContestTotalQuestion,mTotalParticipent;
    private EditText mContestDuration;
    private TextView mContestDateSetText;

    private Button mContestUpdateBtn;


    //Firebase Auth
    private String dUserUID = "NO";
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener; //For going to Account Activity Page

    //Photo Selecting
    private final int CODE_IMG_GALLERY = 1;
    private final String SAMPLE_CROPPED_IMG_NAME = "SampleCropIng";
    Uri imageUri_storage;
    Uri imageUriResultCrop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_add);
        mContestImage = (ImageView) findViewById(R.id.contest_add_imageview);
        mContestName = (EditText) findViewById(R.id.contest_add_name_edit);
        mContestSyllabus = (EditText) findViewById(R.id.contest_add_syllabus_edit); 
        mContestPriorityNo = (EditText) findViewById(R.id.contest_add_priority_edit); 
        mContestViewCount = (EditText) findViewById(R.id.contest_add_view_count_edit);
        mContestTotalQuestion = (EditText) findViewById(R.id.contest_add_total_question_edit);
        mTotalParticipent = (EditText) findViewById(R.id.contest_add_total_participant_edit);
        mContestDuration = (EditText) findViewById(R.id.contest_add_duration_edit);
        
        mContestDateSetText = (TextView) findViewById(R.id.contest_add_date_text);
        mContestUpdateBtn = (Button)findViewById(R.id.contest_add_update_btn);

        //Login Check
        /*mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Toast.makeText(getApplicationContext(),"Contest Add Activity", Toast.LENGTH_SHORT).show();;

                }else{
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        };*/
        mContestImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent() //Image Selecting
                        .setAction(Intent.ACTION_GET_CONTENT)
                        .setType("image/*"), CODE_IMG_GALLERY);
            }
        });

        getIntentMethod();
        mContestUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckData();
            }
        });

        mContestDateSetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
    }
    private String dsContestName = "NO", dsContestSyllabus = "NO", dsContestPriority = "NO";
    private String dsContestViewCount = "NO", dsContestTotalQuestion = "NO",  dsContestTotalParticipent = "NO";
    private String dsContestDuration = "NO";
    private int diPriority = 0,  diContestTotalParticipent = 0, diContestDuration = 0;
    private int diContestViews= 0, diContestTotalQuestion = 0;
    private void CheckData() {
        dsContestName = mContestName.getText().toString();
        dsContestSyllabus = mContestSyllabus.getText().toString();
        dsContestPriority = mContestPriorityNo.getText().toString();
        dsContestViewCount = mContestViewCount.getText().toString();
        dsContestTotalQuestion = mContestTotalQuestion.getText().toString();
        dsContestTotalParticipent = mTotalParticipent.getText().toString();

        dsContestDuration = mContestDuration.getText().toString();
        //long contest date remain


        if(imageUriResultCrop == null){
            Toast.makeText(getApplicationContext(),"Click Image to add", Toast.LENGTH_SHORT).show();;
        }else if(dsContestName.equals("NO") || dsContestSyllabus.equals("NO") || dsContestPriority.equals("NO") || dsContestViewCount.equals("NO")
                || dsContestTotalParticipent.equals("NO") || dsContestDuration.equals("NO") ){
            Toast.makeText(getApplicationContext(),"Please fillup all ", Toast.LENGTH_SHORT).show();;
        }else if(dsContestName.equals("") || dsContestSyllabus.equals("") || dsContestPriority.equals("") || dsContestViewCount.equals("")
                || dsContestTotalParticipent.equals("") ||  dsContestDuration.equals("")  ){
            Toast.makeText(getApplicationContext(),"Please fillup all ", Toast.LENGTH_SHORT).show();;
        }else if(dlContestDate == 0){
            Toast.makeText(getApplicationContext(),"Select a date", Toast.LENGTH_SHORT).show();;
        }else if(intentFoundError){
            Toast.makeText(getApplicationContext(),"Intent error", Toast.LENGTH_SHORT).show();;
        }else{

            diContestViews = Integer.parseInt(dsContestViewCount);
            diContestTotalQuestion = Integer.parseInt(dsContestTotalQuestion);
            diPriority = Integer.parseInt(dsContestPriority);
            diContestTotalParticipent = Integer.parseInt(dsContestTotalParticipent);
            diContestDuration = Integer.parseInt(dsContestDuration);
            UploadCropedImageFunction(imageUriResultCrop);
        }

    }
    //Firebase Storage
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();;
    StorageReference ref;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private void UploadCropedImageFunction(Uri filePath) {
        if(filePath != null)
        {
            dUserUID = FirebaseAuth.getInstance().getUid();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            String dsTimeMiliSeconds = String.valueOf(System.currentTimeMillis());
            ref = storageReference.child("Quiz/"+"BookCoverPic"+"/"+dsBookUID+"/"+ dsContestName+" "+dsTimeMiliSeconds +"."+getFileExtention(filePath));
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        //Photo Uploaded now get the URL
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String dPhotoURL = uri.toString();
                                    Toast.makeText(getApplicationContext(), "Photo Uploaded", Toast.LENGTH_SHORT).show();

                                    Map<String, Object> note = new HashMap<>();
                                    note.put("CoName", dsContestName);
                                    note.put("CoPhotoUrl", dPhotoURL);
                                    note.put("CoSyllabus", dsContestSyllabus);
                                    note.put("CoPassword", "NO");
                                    note.put("CoExtra", "0");
                                    note.put("CoCreator", dUserUID);
                                    note.put("CoiViewCount", diContestViews);
                                    note.put("CoiTotalQues", diContestTotalQuestion);
                                    note.put("CoiPriority", diPriority);
                                    note.put("CoiCoins", 0);
                                    note.put("CoiTotalParticipant", diContestTotalParticipent);
                                    note.put("CoiDuration", diContestDuration);

                                    note.put("CoiDate", dlContestDate); // Date of Contest



                                    db.collection("Quiz").document("Data")
                                            .collection("AllBooks").document(dsBookUID).collection("AllContest").add(note)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(getApplicationContext(),"Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                    mContestUpdateBtn.setText("UPDATED");
                                                    mContestName.setText("");
                                                    mContestSyllabus.setText("");
                                                    mContestPriorityNo.setText("");
                                                    mContestViewCount.setText("");
                                                    mContestTotalQuestion.setText("");
                                                    finish();
                                                    Intent intent = new Intent(ContestAdd.this, Contests.class);    //Error Intent not sent
                                                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                    intent.putExtra("dsBookUID", dsBookUID);
                                                    intent.putExtra("dsBookName", dsBookName);
                                                    startActivity(intent);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            mContestUpdateBtn.setText("Try Again");
                                            mContestName.setText("Failed");
                                            mContestSyllabus.setText("");
                                            mContestPriorityNo.setText("");
                                            Toast.makeText(getApplicationContext(),"Failed Please Try Again", Toast.LENGTH_SHORT).show();

                                        }
                                    });



                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            mContestUpdateBtn.setText("Failed Photo Upload");
                            Toast.makeText(getApplicationContext(), "Failed Photo"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }else{
            Toast.makeText(getApplicationContext(), "Upload Failed Photo Not Found ", Toast.LENGTH_SHORT).show();
        }
    }

    String dsBookUID = "NO", dsBookName = "NO";
    private boolean intentFoundError = true;
    private void getIntentMethod() {
        //////////////GET INTENT DATA
        final Intent intentx = getIntent();
        if(intentx.getExtras() != null)
        {
            dsBookUID = intentx.getExtras().getString("dsBookUID");    //Sylhet
            dsBookName = intentx.getExtras().getString("dsBookName");    //Grocery or Food or Home Services
            intentFoundError = CheckIntentMethod(dsBookUID);
            intentFoundError = CheckIntentMethod(dsBookName);

            if(!intentFoundError){
                //callViewModel();
            }
        }else{
            dsBookUID = "NO";
            dsBookName = "NO";
        }

    }
    private boolean CheckIntentMethod(String dsTestIntent){
        if(TextUtils.isEmpty(dsTestIntent)) {
            dsTestIntent= "NO";
            Toast.makeText(getApplicationContext(), "intent NULL  " , Toast.LENGTH_SHORT).show();
        }else if (dsTestIntent.equals("")){
            dsTestIntent= "NO";
            Toast.makeText(getApplicationContext(), "intent 404" , Toast.LENGTH_SHORT).show();
        }else{
            intentFoundError = false;
        }
        return intentFoundError;
    }


    @Override   //Selecting Image
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CODE_IMG_GALLERY && resultCode == RESULT_OK &&  data.getData() != null && data != null){
            //Photo Successfully Selected

            imageUri_storage = data.getData();
            String dFileSize = getSize(imageUri_storage);       //GETTING IMAGE FILE SIZE
            double  dFileSizeDouble = Double.parseDouble(dFileSize);
            int dMB = 1000;
            dFileSizeDouble =  dFileSizeDouble/dMB;
            //dFileSizeDouble =  dFileSizeDouble/dMB;

            if(dFileSizeDouble <= 5000){
                Picasso.get().load(imageUri_storage).resize(200, 200).centerCrop().into(mContestImage);
                Toast.makeText(getApplicationContext(),"Selected",Toast.LENGTH_SHORT).show();
                imageUriResultCrop = imageUri_storage;
            }else{
                Toast.makeText(this, "Failed! (File is Larger Than 5MB)",Toast.LENGTH_SHORT).show();
            }


        }else {
            Toast.makeText(this, "Canceled",Toast.LENGTH_SHORT).show();
        }
    }
    public String getSize(Uri uri) {
        String fileSize = null;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {

                // get file size
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (!cursor.isNull(sizeIndex)) {
                    fileSize = cursor.getString(sizeIndex);
                }
            }
        } finally {
            cursor.close();
        }
        return fileSize;
    }
    private String getFileExtention(Uri uri){   //IMAGE
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        //Not worked in Croped File so i constant it
        return "JPEG";
    }
    private long dlContestDate = 0;
    @Override   //Date Picker, add implements also
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        dlContestDate  = c.getTimeInMillis();
        mContestDateSetText.setText(currentDateString);
    }
    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onStop() {
        super.onStop();

    }
}