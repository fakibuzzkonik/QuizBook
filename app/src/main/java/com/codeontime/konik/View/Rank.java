package com.codeontime.konik.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codeontime.konik.Adapter.RankAdapter;
import com.codeontime.konik.Model.RankModel;
import com.codeontime.konik.R;
import com.codeontime.konik.RecylerviewClickInterface;
import com.codeontime.konik.ViewModel.RankVM;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class Rank extends AppCompatActivity implements RecylerviewClickInterface {

    //RecyclerView
    private RecyclerView mRank_RecylcerView;
    List<RankModel> mRankItemList = new ArrayList<>();;
    RankAdapter mRank_Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        mRank_RecylcerView = (RecyclerView)findViewById(R.id.rank_recyclerview);

        getIntentMethod();

    }
    private RankVM rankVM;
    private void callViewModel() {
        Log.d("ViewModel", "allViewModel:2 ContestActivityVM start");
        rankVM = new ViewModelProvider(this).get(RankVM.class);
        rankVM.RankUserListt(dsBookUID, dsContestUID).observe(this, new Observer<List<RankModel>>() {
            @Override
            public void onChanged(List<RankModel> rank_user_list) {
                Log.d("ViewModel", "allViewModel:2 onChanged RankModel size = "+rank_user_list.size());
                if (rank_user_list.get(0).getUid().equals("NULL")){
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "No Rank Found", Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                }else{
                    mRank_Adapter = new RankAdapter(Rank.this,rank_user_list,Rank.this);
                    mRank_Adapter.notifyDataSetChanged();

                    mRankItemList = rank_user_list;
                    mRank_RecylcerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));
                    mRank_RecylcerView.setAdapter(mRank_Adapter);

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


            if(!intentFoundError){  //Inter error found not true means error pawa jay nai
                callViewModel();
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
        }else if (dsTestIntent.equals("")){
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