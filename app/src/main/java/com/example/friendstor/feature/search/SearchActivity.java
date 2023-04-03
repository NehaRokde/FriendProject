package com.example.friendstor.feature.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.friendstor.R;
import com.example.friendstor.feature.profile.ProfileViewModel;
import com.example.friendstor.model.search.SearchResponse;
import com.example.friendstor.model.search.User;
import com.example.friendstor.utils.ViewModelFactory;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private TextView defaultTextview;

    private SearchViewModel viewModel;

    private SearchAdapter searchAdapter;

    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        viewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelFactory()).get(SearchViewModel.class);

        toolbar = findViewById(R.id.toolbar);
        searchView = findViewById(R.id.search);
        recyclerView = findViewById(R.id.searchRecyclerView);
        defaultTextview = findViewById(R.id.defaultTextview);

        userList =  new ArrayList<>();
        searchAdapter = new SearchAdapter(this, userList);

        LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(searchAdapter);

        searchView.setQueryHint("Search People");

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.super.onBackPressed();
            }
        });
        
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchDb(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length()>2){
                    searchDb(newText);
                }else{
                    defaultTextview.setVisibility(View.VISIBLE);
                    userList.clear();
                    searchAdapter.notifyDataSetChanged();

                }
                return true;
            }
        });

    }

    private void searchDb(String query) {
        Map<String,String> params =  new HashMap<>();
        params.put("keyword", query);
        viewModel.search(params).observe(SearchActivity.this, new Observer<SearchResponse>() {
            @Override
            public void onChanged(SearchResponse searchResponse) {
                if(searchResponse.getStatus() == 200){
                    defaultTextview.setVisibility(View.GONE);
                    userList.clear();
                    userList.addAll(searchResponse.getUser());
                    searchAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(SearchActivity.this, searchResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    defaultTextview.setVisibility(View.VISIBLE);
                }
            }
        });

    }
}