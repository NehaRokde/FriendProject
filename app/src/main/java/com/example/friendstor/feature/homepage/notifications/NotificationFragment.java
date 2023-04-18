package com.example.friendstor.feature.homepage.notifications;

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

import com.example.friendstor.R;
import com.example.friendstor.feature.homepage.MainActivity;
import com.example.friendstor.model.notification.NotificationResponse;
import com.example.friendstor.model.notification.NotificationsItem;
import com.example.friendstor.utils.ViewModelFactory;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    RecyclerView recyclerView;
    TextView defualtTxt;
    NotificationViewModel viewModel;
    Context context;

    NotificationAdapter notificationAdapter;
    List<NotificationsItem> notificationsItemsList = new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider((FragmentActivity) context, new ViewModelFactory())
                .get(NotificationViewModel.class);

        recyclerView = view.findViewById(R.id.notification_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        defualtTxt = view.findViewById(R.id.defualt_txt);

        fetchNotification();
    }

    private void fetchNotification() {

        ((MainActivity) getActivity()).showProgressBar();

        viewModel.getNotification(FirebaseAuth.getInstance().getUid())
                .observe(getViewLifecycleOwner(), new Observer<NotificationResponse>() {
                    @Override
                    public void onChanged(NotificationResponse notificationResponse) {
                        ((MainActivity) getActivity()).hideProgressBar();
                        if (notificationResponse.getStatus() == 200) {
                            notificationsItemsList.clear();
                            if(notificationResponse.getNotifications().size()>0){
                                defualtTxt.setVisibility(View.GONE);
                                notificationsItemsList.addAll(notificationResponse.getNotifications());
                                notificationAdapter = new NotificationAdapter(context, notificationsItemsList);
                                recyclerView.setAdapter(notificationAdapter);
                            }else{
                                defualtTxt.setVisibility(View.VISIBLE);
                            }

                        }
                    }
                });
    }
}