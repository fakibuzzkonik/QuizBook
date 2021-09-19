package com.codeontime.munna.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.codeontime.munna.MainActivity;
import com.codeontime.munna.R;
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

import java.util.HashMap;
import java.util.Map;

public class BookAdd extends AppCompatActivity {
    private EditText mBookNameEdit, mBookBioEdit, mBookPasswordEdit, mBookPriority, mBookViewCount, mBookTotalLevelEdit;
    private ImageView mBookImage;
    private Button mBookUpdateBtn;

    private RadioGroup mBookPrivacyRadioGroup;
    private RadioButton mBookPublicRadioBtn, mBookPrivateRadioBtn;

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

    //Firebase Storage
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();;
    StorageReference ref;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //variablles
    private String dsBookName = "NO", dsBookBio = "NO", dsBookPassword= "NO", dsBookPriority= "NO", dsBookViewCount= "NO", dsBookTotalLeve = "NO";
    private int diBookPriority = 0 ,diBookViewCount = 0, diBookTotalLevel = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_add);

        mBookImage = (ImageView) findViewById(R.id.book_add_imageview);
        mBookNameEdit = (EditText) findViewById(R.id.book_add_name_edit);
        mBookBioEdit = (EditText) findViewById(R.id.book_add_bio_edit);
        mBookPasswordEdit = (EditText) findViewById(R.id.book_add_password_edit);
        mBookPriority = (EditText) findViewById(R.id.book_add_priority_edit);
        mBookViewCount = (EditText) findViewById(R.id.book_add_view_count_edit);
        mBookTotalLevelEdit= (EditText) findViewById(R.id.book_add_total_level_edit);

        mBookPrivacyRadioGroup = (RadioGroup)findViewById(R.id.book_add_privacy_radio_group);
        mBookPublicRadioBtn = (RadioButton)findViewById(R.id.book_add_public_radio);
        mBookPrivateRadioBtn = (RadioButton)findViewById(R.id.book_add_private_radio);
        mBookUpdateBtn = (Button)findViewById(R.id.book_add_update_btn);
        //Login Check
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() { ///for going to Account Activity Page
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Toast.makeText(getApplicationContext(),"Add Level4 Information", Toast.LENGTH_SHORT).show();;

                }else{
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        };
        mBookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent() //Image Selecting
                        .setAction(Intent.ACTION_GET_CONTENT)
                        .setType("image/*"), CODE_IMG_GALLERY);
            }
        });
        mBookUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckData();
            }
        });
        mBookPrivateRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBookPasswordEdit.setVisibility(View.VISIBLE);
            }
        });
        mBookPublicRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBookPasswordEdit.setVisibility(View.GONE);
            }
        });



    }

    private void CheckData() {
        dsBookName = mBookNameEdit.getText().toString();
        dsBookBio = mBookBioEdit.getText().toString();
        dsBookPassword = mBookPasswordEdit.getText().toString();
        dsBookPriority = mBookPriority.getText().toString();
        dsBookViewCount = mBookViewCount.getText().toString();
        dsBookTotalLeve = mBookTotalLevelEdit.getText().toString();
        if(imageUriResultCrop == null){
            Toast.makeText(getApplicationContext(),"Click on Image to Add", Toast.LENGTH_SHORT).show();;
        }else if(dsBookName.equals("NO") || dsBookBio.equals("NO") || dsBookPriority.equals("NO") || dsBookViewCount.equals("NO") || dsBookTotalLeve.equals("NO")  ){
            Toast.makeText(getApplicationContext(),"Please fillup all ", Toast.LENGTH_SHORT).show();;
        }else if(dsBookName.equals("") || dsBookBio.equals("") || dsBookPriority.equals("") || dsBookViewCount.equals("") || dsBookTotalLeve.equals("")  ){
            Toast.makeText(getApplicationContext(),"Please fillup all ", Toast.LENGTH_SHORT).show();;
        }else if(mBookPrivateRadioBtn.isChecked() && dsBookPassword.equals("NO")){
            Toast.makeText(getApplicationContext(),"Please Type Password", Toast.LENGTH_SHORT).show();;
        }else{

            diBookPriority = Integer.parseInt(dsBookPriority);
            diBookViewCount = Integer.parseInt(dsBookViewCount);
            diBookTotalLevel = Integer.parseInt(dsBookTotalLeve);
            UploadCropedImageFunction(imageUriResultCrop);
        }
    }


    private void UploadCropedImageFunction(Uri filePath) {
        if(filePath != null)
        {
            dUserUID = FirebaseAuth.getInstance().getUid();
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            String dsTimeMiliSeconds = String.valueOf(System.currentTimeMillis());
            ref = storageReference.child("Quiz/"+"BookCoverPic"+"/"+ dsBookName+" "+dsTimeMiliSeconds +"."+getFileExtention(filePath));
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
                                    note.put("BookName", dsBookName);
                                    note.put("BookPhotoUrl", dPhotoURL);
                                    note.put("BookBio", dsBookBio);
                                    note.put("BookCreator", dUserUID);
                                    note.put("BookExtra", "0");
                                    note.put("BookPassword", dsBookPassword);
                                    note.put("BookiPriority", diBookPriority);
                                    note.put("BookiViewCount", diBookViewCount);
                                    note.put("BookiTotalLevel", diBookTotalLevel);


                                    db.collection("Quiz").document("Data").collection("AllBooks").add(note)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(getApplicationContext(),"Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                    mBookUpdateBtn.setText("UPDATED");
                                                    mBookNameEdit.setText("");
                                                    mBookBioEdit.setText("");
                                                    mBookPasswordEdit.setText("");
                                                    mBookPriority.setText("");
                                                    mBookViewCount.setText("");
                                                    mBookTotalLevelEdit.setText("");
                                                    finish();
                                                    Intent intent = new Intent(BookAdd.this, MainActivity.class);
                                                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                    startActivity(intent);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            mBookUpdateBtn.setText("Try Again");
                                            mBookNameEdit.setText("Failed");
                                            mBookBioEdit.setText("");
                                            mBookPriority.setText("");
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
                            mBookUpdateBtn.setText("Failed Photo Upload");
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

    //Dont forget to add class code on MainfestXml
    @Override   //Selecting Image
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                Picasso.get().load(imageUri_storage).into(mBookImage);
                Toast.makeText(getApplicationContext(),"Selected",Toast.LENGTH_SHORT).show();
                //startCrop(imageUri_storage);
                imageUriResultCrop = imageUri_storage;
            }else{
                Toast.makeText(this, "Failed! (File is Larger Than 5MB)",Toast.LENGTH_SHORT).show();
            }
        }else{
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