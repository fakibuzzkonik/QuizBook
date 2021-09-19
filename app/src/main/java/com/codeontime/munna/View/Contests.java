package com.codeontime.munna.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.codeontime.munna.LoginCheck;
import com.codeontime.munna.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Contests extends AppCompatActivity {
    private FloatingActionButton mContestAddBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contests);
        mContestAddBtn = (FloatingActionButton)findViewById(R.id.contest_add_btn);
        getIntentMethod();

        mContestAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginCheck.class);
                intent.setFlags( intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
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
        }else if (dsBookUID.equals("")){
            dsTestIntent= "NO";
            Toast.makeText(getApplicationContext(), "intent 404" , Toast.LENGTH_SHORT).show();
        }else{
            intentFoundError = false;
        }
        return intentFoundError;
    }
}