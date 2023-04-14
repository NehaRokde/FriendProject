package com.example.friendstor.feature.homepage.newsfeed;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.friendstor.R;
import com.example.friendstor.feature.homepage.MainActivity;
import com.example.friendstor.feature.postupload.PostUploadViewModel;
import com.example.friendstor.feature.profile.ProfileActivity;
import com.example.friendstor.model.post.PostResponse;
import com.example.friendstor.model.post.PostsItem;
import com.example.friendstor.model.reaction.ReactResponse;
import com.example.friendstor.utils.Util;
import com.example.friendstor.utils.ViewModelFactory;
import com.example.friendstor.utils.adapter.PostAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsFeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private MainViewModel viewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PostAdapter postAdapter;
    private List<PostsItem> postsItems;

    private Context context;
    private Boolean isFirstLoading = true;

    private int limit = 1;
    private int offset = 0;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.newsfeed_rv);
        viewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelFactory())
                .get(MainViewModel.class);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        postsItems = new ArrayList<>();


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isLastItemReached()) {
                    offset += limit;
                    fetchNews();
                }
            }
        });

    }

    private boolean isLastItemReached() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int position = layoutManager.findLastCompletelyVisibleItemPosition();
        int noOfItems = postAdapter.getItemCount();
        return (position >= noOfItems - 1);

    }

    @Override
    public void onResume() {
        super.onResume();
        fetchNews();
    }

    private void fetchNews() {

        Map<String, String> params = new HashMap<>();
        params.put("uid", FirebaseAuth.getInstance().getUid());
        params.put("limit", limit + "");
        params.put("offset", offset + "");

        ((MainActivity) getActivity()).showProgressBar();

        viewModel.getNewsFeed(params).observe(getViewLifecycleOwner(), new Observer<PostResponse>() {
            @Override
            public void onChanged(PostResponse postResponse) {

                ((MainActivity) getActivity()).hideProgressBar();

                if (postResponse.getStatus() == 200) {
                    if(swipeRefreshLayout.isRefreshing()){
                        postsItems.clear();
                        postAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    postsItems.addAll(postResponse.getPosts());

                    if (isFirstLoading) {
                        postAdapter = new PostAdapter(context, postsItems);
                        recyclerView.setAdapter(postAdapter);
                    } else {
                        postAdapter.notifyItemRangeInserted(postsItems.size(), postResponse.getPosts().size());
                    }

                    if(postResponse.getPosts().size() ==0){
                        offset -= limit;
                    }
                    isFirstLoading = false;
                } else {
                    if(swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(context, postResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        offset = 0;
        postsItems.clear();
        isFirstLoading = true;
    }

    @Override
    public void onRefresh() {
        offset = 0;
        isFirstLoading = true;
        fetchNews();

    }

    public void updateUserReaction(String uid, int postId, String postOwnerId, String previousReactionType, String newReactionType, int adpaterPosition) {

        viewModel.performReaction(new Util.PerformReaction(
                uid,
                postId+"",
                postOwnerId,
                previousReactionType,
                newReactionType
        )).observe(this, new Observer<ReactResponse>() {
            @Override
            public void onChanged(ReactResponse reactResponse) {
                if(reactResponse.getStatus() == 200){
                    postAdapter.updatePostAfterReaction(adpaterPosition, reactResponse.getReaction());
                }else {
                    Toast.makeText(context, reactResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}