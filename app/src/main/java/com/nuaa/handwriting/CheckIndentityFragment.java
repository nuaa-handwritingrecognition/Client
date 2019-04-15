package com.nuaa.handwriting;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nuaa.handwriting.constant.Constant;
import com.nuaa.handwriting.data.DataManager;
import com.nuaa.handwriting.model.BaseResponse;
import com.nuaa.handwriting.model.EmptyResponse;
import com.nuaa.handwriting.model.HeaderModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CheckIndentityFragment extends DialogFragment {

    private static CheckIndentityFragment mFragment;

    private TextView mDescTv;

    private String mType;

    private String mUserName;

    private String mPhone;

    private ArrayList<String> mList;

    private CheckIndentityAdapter mAdapter;

    private ProgressDialog mDialog;

    public CheckIndentityFragment() {
    }

    public interface GoWriteListener {
        void wirite();
    }

    private static GoWriteListener mListener;

    public static CheckIndentityFragment getInstance(String userName, String phone, String type, ArrayList<String> list, GoWriteListener listener) {
        Bundle bundle = new Bundle();
        bundle.putString("name", userName);
        bundle.putString("phone", phone);
        bundle.putString("type", type);
        bundle.putStringArrayList("list", list);
        mListener = listener;
        mFragment = new CheckIndentityFragment();
        mFragment.setArguments(bundle);
        return mFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mType = getArguments().getString("type");
        mList = getArguments().getStringArrayList("list");
        mUserName = getArguments().getString("name");
        mPhone = getArguments().getString("phone");

        View view = inflater.inflate(R.layout.dialog_fragment_check_indentity, container, false);

        mDescTv = view.findViewById(R.id.tv_desc);
        if ("name".equalsIgnoreCase(mType)) {
            mDescTv.setText("见到系统中有相同的手机号，请确认以下用户名是不是你，并勾选");
        } else {
            mDescTv.setText("检测到系统中有相同的用户名，请确认以下手机号是不是你，并勾选");
        }
        RecyclerView recyclerView = view.findViewById(R.id.rv);
        Button notBtn = view.findViewById(R.id.btn_not);
        Button okBtn = view.findViewById(R.id.btn_ok);

        mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage("请求中...");

        mAdapter = new CheckIndentityAdapter(getActivity(), mType, mList);

        recyclerView.setAdapter(mAdapter);

        notBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // "以上都不是"
                if (mListener != null) {
                    mListener.wirite();
                }
                dismiss();
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter != null) {
                    HashSet<Integer> list = mAdapter.getSelectList();
                    if (list == null || list.size() == 0) {
                        Toast.makeText(getActivity(), "请勾选，如没有请选择以上都不是", Toast.LENGTH_SHORT).show();
                    } else {
                        Iterator<Integer> iterator = list.iterator();
                        final List<String> selectList = new ArrayList<>();
                        while (iterator.hasNext()) {
                            selectList.add(mList.get(iterator.next()));
                        }
                        ConfirmDialogFragment fragment = ConfirmDialogFragment.getInstance(new ConfirmDialogFragment.OnClickListener() {
                            @Override
                            public void onClick() {
                                mDialog.show();
                                DataManager.getInstance().mergeInfo(mUserName, mPhone, mType, selectList)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Consumer<BaseResponse<EmptyResponse>>() {
                                            @Override
                                            public void accept(BaseResponse<EmptyResponse> response) {
                                                HeaderModel headerModel = response.getHeader();
                                                if (Constant.OK.equals(headerModel.getErrorCode())) {
                                                    // 选择完了已确定
                                                    if (mListener != null) {
                                                        mListener.wirite();
                                                    }
                                                } else {
                                                    Toast.makeText(getActivity(), "网络错误:" + headerModel.getErrorMsg(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }, new Consumer<Throwable>() {
                                            @Override
                                            public void accept(Throwable throwable) {
                                                Toast.makeText(getActivity(), "网络错误:" + throwable.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }, new Action() {
                                            @Override
                                            public void run() {
                                                mDialog.dismiss();
                                                dismiss();
                                            }
                                        });
                            }
                        });
                        fragment.show(getFragmentManager(), "confirmDialog");
                    }
                }
            }
        });

        return view;
    }
}
