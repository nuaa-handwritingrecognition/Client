package com.nuaa.handwriting;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class CustomDialogFragment extends DialogFragment {

    private static CustomDialogFragment mFragment;

    public CustomDialogFragment() {
    }

    private OnClickListener mListener;

    public interface OnClickListener {
        void onClick(String actValue);
    }

    public static CustomDialogFragment getInstance(OnClickListener listener) {
        mFragment = new CustomDialogFragment();
        mFragment.mListener = listener;
        return mFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_custom, container, false);
        final EditText editText = view.findViewById(R.id.et_act_value);
        Button okBtn = view.findViewById(R.id.btn_ok);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String actValue = editText.getText().toString();
                if (TextUtils.isEmpty(actValue)) {
                    editText.requestFocus();
                    editText.setError("请输入文字");
                    return;
                }

                if (mListener != null) {
                    mListener.onClick(actValue);
                }

                dismiss();
            }
        });
        return view;
    }
}
