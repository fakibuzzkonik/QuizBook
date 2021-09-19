package com.codeontime.munna;

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

import com.codeontime.munna.Adapter.BookAdapter;
import com.codeontime.munna.Model.BookModel;
import com.codeontime.munna.View.Contests;
import com.codeontime.munna.ViewModel.MainActivityVM;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecylerviewClickInterface  {
    private Button mLoginBtn;
    private Button mAddBook;

    //RecyclerView
    private RecyclerView mBook_RecyclerView;
    List<BookModel> listBookItem;
    BookAdapter mbook_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAddBook = (Button)findViewById(R.id.main_add_book);
        mLoginBtn = (Button)findViewById(R.id.main_login_btn);
        mBook_RecyclerView = (RecyclerView)findViewById(R.id.main_book_recylerview);
        listBookItem = new ArrayList<>();

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

        callViewModel();
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
}