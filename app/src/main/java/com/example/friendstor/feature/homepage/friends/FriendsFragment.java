package com.example.friendstor.feature.homepage.friends;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.friendstor.R;
import com.example.friendstor.feature.homepage.MainActivity;
import com.example.friendstor.feature.homepage.newsfeed.MainViewModel;
import com.example.friendstor.model.friend.FriendResponse;
import com.example.friendstor.model.friend.Friends;
import com.example.friendstor.model.friend.Requests;
import com.example.friendstor.utils.ViewModelFactory;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {

    private MainViewModel mainViewModel;
    private Context context;
    private RecyclerView friendRequestRecyclerView, friendsRecyclerView;
    private TextView friendTitle, requestTitle, defaultTitle;

    private FriendAdapter friendAdapter;
    private FriendRequestAdapter friendRequestAdapter;
    private List<Friends> friendsList = new ArrayList<>();
    private List<Requests> requestsList = new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainViewModel = new ViewModelProvider((FragmentActivity) context, (ViewModelProvider.Factory) new ViewModelFactory()).get(MainViewModel.class);

        friendRequestRecyclerView = view.findViewById(R.id.friend_request_rv);
        friendsRecyclerView = view.findViewById(R.id.friend_rv);
        friendTitle = view.findViewById(R.id.friend_title);
        requestTitle = view.findViewById(R.id.request_title);
        defaultTitle = view.findViewById(R.id.default_textview);


        friendAdapter = new FriendAdapter(context, friendsList);
        friendRequestAdapter = new FriendRequestAdapter(context, requestsList);

        friendsRecyclerView.setAdapter(friendAdapter);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        friendRequestRecyclerView.setAdapter(friendRequestAdapter);
        friendRequestRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        loadFriends();
    }

    private void loadFriends() {
        ((MainActivity) getActivity()).showProgressBar();
        mainViewModel.loadFriends(FirebaseAuth.getInstance().getUid()).observe(this, new Observer<FriendResponse>() {
            @Override
            public void onChanged(FriendResponse friendResponse) {
                ((MainActivity) getActivity()).hideProgressBar();
                loadData(friendResponse);
            }
        });
    }

    private void loadData(FriendResponse friendResponse) {

        if (friendResponse.getStatus() == 200) {
            friendsList.clear();
            friendsList.addAll(friendResponse.getResult().getFriends());
            friendAdapter.notifyDataSetChanged();

            requestsList.clear();
            requestsList.addAll(friendResponse.getResult().getRequests());
            friendRequestAdapter.notifyDataSetChanged();

            if (friendResponse.getResult().getFriends().size() > 0) {
                friendTitle.setVisibility(View.VISIBLE);
            } else {
                friendTitle.setVisibility(View.GONE);
            }


            if (friendResponse.getResult().getRequests().size() > 0) {
                requestTitle.setVisibility(View.VISIBLE);
            } else {
                requestTitle.setVisibility(View.GONE);
            }

            if (friendResponse.getResult().getFriends().size() == 0 &&
                    friendResponse.getResult().getRequests().size() == 0) {
                defaultTitle.setVisibility(View.VISIBLE);
            }
        } else {
            Toast.makeText(context, friendResponse.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}