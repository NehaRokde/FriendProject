package com.example.friendstor.feature.comment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.friendstor.R;
import com.example.friendstor.model.comment.CommentResponse;
import com.example.friendstor.model.comment.CommentsItem;
import com.example.friendstor.utils.Util;
import com.example.friendstor.utils.ViewModelFactory;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class CommentRepliesBottomSheet extends BottomSheetDialogFragment {

    Context context;
    private TextView commentsCounterTxt;
    private RecyclerView recyclerView;
    private EditText commentEditText;
    private RelativeLayout commentSendWrapper;

    private CommentViewModel viewModel;

    private String postId, postuserId, commentOn, commentUserId, parentId;
    private boolean shouldOpenKeyboard = false;
    private int commentCounter = 0, postAdapterPosition = 0, adapterPosition = 0;

    private Util.IOCommentAdded ioCommentAdded;

    private CommentAdapter commentAdapter;
    private List<CommentsItem> commentsItems = new ArrayList<>();

    Util.IOnCommentRepliesAdded iOnCommentRepliesAdded;

    public CommentRepliesBottomSheet(Util.IOnCommentRepliesAdded iOnCommentRepliesAdded) {
        this.iOnCommentRepliesAdded = iOnCommentRepliesAdded;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        this.ioCommentAdded = (Util.IOCommentAdded) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, new ViewModelFactory()).get(CommentViewModel.class);

        postId = getArguments().getString("postId");
        postuserId = getArguments().getString("postUserId");
        commentOn = getArguments().getString("commentOn");
        commentUserId = getArguments().getString("commentUserId");
        parentId = getArguments().getString("parentId");
        commentCounter = getArguments().getInt("commentCounter");
        shouldOpenKeyboard = getArguments().getBoolean("shouldOpenKeyboard");
        postAdapterPosition = getArguments().getInt("postAdapterPosition");
        adapterPosition = getArguments().getInt("adapterPosition");

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = View.inflate(context, R.layout.bottomsheet_comment, null);
        dialog.setContentView(view);

        commentsCounterTxt = view.findViewById(R.id.commentsCounter);
        recyclerView = view.findViewById(R.id.comments_rv);
        commentEditText = view.findViewById(R.id.comment_edittext);
        commentSendWrapper = view.findViewById(R.id.wrapper_comment_send);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        if(shouldOpenKeyboard){
            commentEditText.requestFocus();
            InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            im.showSoftInput(commentEditText, InputMethodManager.SHOW_IMPLICIT);
        }

        getCommentReplies();

        commentsCounterTxt.setText("Replies");
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog dialog1 = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = dialog1.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                bottomSheet.setBackgroundColor(Color.TRANSPARENT);
                if(shouldOpenKeyboard){
                    BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
                }else {
                    BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });


        commentSendWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentTxt = commentEditText.getText().toString();
                if (commentTxt.isEmpty()) {
                    Toast.makeText(context, "Please enter comment first", Toast.LENGTH_SHORT).show();
                } else {
                    InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    commentEditText.setText("");
                    viewModel.postComment(new Util.PostComment(commentTxt, FirebaseAuth.getInstance().getUid(),
                            postId,
                            postuserId,
                            commentOn,
                            commentUserId,
                            parentId)).observe(getActivity(), new Observer<CommentResponse>() {
                        @Override
                        public void onChanged(CommentResponse commentResponse) {
                            Toast.makeText(context, commentResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            if (commentResponse.getStatus() == 200) {

                                //notify about newly added comment
                                CommentsItem postedComment = commentResponse.getComments().get(0);
                                commentsItems.add(0, postedComment);
                                commentAdapter.notifyItemInserted(0);
                                recyclerView.smoothScrollToPosition(0);

                                iOnCommentRepliesAdded.onCommentRepliesAdded(adapterPosition,
                                        new CommentsItem(
                                                commentResponse.getComments().get(0).getProfileUrl(),
                                                commentResponse.getComments().get(0).getCommentDate(),
                                                commentResponse.getComments().get(0).getName(),
                                                commentResponse.getComments().get(0).getComment()
                                        ));
                                ioCommentAdded.onCommandAdded(postAdapterPosition);
                            }
                        }
                    });
                }
            }
        });
    }

    private void getCommentReplies() {

        viewModel.getCommentReplies(postId, parentId).observe(this, new Observer<CommentResponse>() {
            @Override
            public void onChanged(CommentResponse commentResponse) {
                if (commentResponse.getStatus() == 200) {
                    commentsItems.clear();
                    commentsItems.addAll(commentResponse.getComments());
                    commentAdapter = new CommentAdapter(context, commentsItems, iOnCommentRepliesAdded, postAdapterPosition);
                    recyclerView.setAdapter(commentAdapter);

                } else {
                    Toast.makeText(context, commentResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
