package com.example.friendstor.feature.comment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.example.friendstor.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PostCommentRepliesBottomSheet extends BottomSheetDialogFragment {

    Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context =  context;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view =  View.inflate(context, R.layout.bottomsheet_comment, null);
        dialog.setContentView(view);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog dialog1 = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = dialog1.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                bottomSheet.setBackgroundColor(Color.TRANSPARENT);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }
}
