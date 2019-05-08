package com.nuaa.handwriting;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ConfirmDialogFragment extends DialogFragment {

    private static ConfirmDialogFragment mFragment;

    public ConfirmDialogFragment() {

    }

    private static OnClickListener mListener;

    public interface OnClickListener {
        void onClick();
    }

    public static ConfirmDialogFragment getInstance(OnClickListener listener) {
        mFragment = new ConfirmDialogFragment();
        mListener = listener;
        return mFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_confirm, container, false);
        Button okBtn = view.findViewById(R.id.btn_ok);
        Button notBtn = view.findViewById(R.id.btn_not);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null) {
                    mListener.onClick();
                }
            }
        });

        notBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

}
