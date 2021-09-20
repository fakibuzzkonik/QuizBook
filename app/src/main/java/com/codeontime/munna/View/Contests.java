package com.codeontime.munna.View;

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
import com.codeontime.munna.Adapter.ContestAdapter;
import com.codeontime.munna.ContestAdd;
import com.codeontime.munna.MainActivity;
import com.codeontime.munna.Model.ContestsModel;
import com.codeontime.munna.R;
import com.codeontime.munna.RecylerviewClickInterface;
import com.codeontime.munna.ViewModel.ContestsVM;
import com.codeontime.munna.ViewModel.MainActivityVM;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class Contests extends AppCompatActivity  implements RecylerviewClickInterface {
    private FloatingActionButton mContestAddBtn;

    //RecyclerView
    private RecyclerView mBook_RecyclerView;
    List<ContestsModel> listContestItem = new ArrayList<>();;
    ContestAdapter mContest_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contests);
        mContestAddBtn = (FloatingActionButton)findViewById(R.id.contest_add_btn);
        mBook_RecyclerView = (RecyclerView)findViewById(R.id.contests_recylerview);

        getIntentMethod();

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
                        mBook_RecyclerView.setLayoutManager(new GridLayoutManager(Contests.this,2));
                        mBook_RecyclerView.setAdapter(mContest_adapter);
                    } else {
                        mBook_RecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));
                        mBook_RecyclerView.setAdapter(mContest_adapter);
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

    @Override
    public void onItemClick(int position) {
        
    }
}