package com.nuaa.handwriting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nuaa.handwriting.constant.Constant;
import com.nuaa.handwriting.data.DataManager;
import com.nuaa.handwriting.model.BaseResponse;
import com.nuaa.handwriting.model.EmptyResponse;
import com.nuaa.handwriting.model.HeaderModel;
import com.nuaa.handwriting.model.PointBean;
import com.nuaa.handwriting.model.VectorBean;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class HandWritingCustomActivity extends AppCompatActivity {

    private TouchView mTouchView;

    private String mUserName;

    private String mCellphone;

    private String mActValue;

    private TextView mDescTv;

    private TextView mCountTv;

    private ProgressDialog mDialog;

    private int mSubmitCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hand_writing_custom);

        mUserName = getIntent().getStringExtra(Constant.BUNDLE_KEY_USER_NAME);
        mCellphone = getIntent().getStringExtra(Constant.BUNDLE_KEY_CELL_PHONE);
        mActValue = getIntent().getStringExtra(Constant.BUNDLE_KEY_ACT_VALUE);

        mTouchView = findViewById(R.id.touch_view);
        mCountTv = findViewById(R.id.tv_count);
        mDescTv = findViewById(R.id.tv_desc);

        mDescTv.setText("请在下方空白处手写【" + mActValue + "】：");
        mCountTv.setText("此时是第(1 / 10)次书写");

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("请求中...");
    }

    public void submit(View view) {
        ArrayList<PointBean> pointList = new ArrayList<>(mTouchView.getPointList());

        if (pointList == null || pointList.size() == 0) {
            Toast.makeText(this, "没有手写数据, 请重试", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<VectorBean> vectorList = new ArrayList<>();

        PointBean firstPoint = pointList.get(0);
        PointBean lastPoint = pointList.get(pointList.size() - 1);
        int count = 150 - pointList.size();
        for (int i = 0; i < count; i++) {
            pointList.add(lastPoint);
        }

        for (PointBean pointBean : pointList) {
            float vectorX = pointBean.getX() - firstPoint.getX();
            float vectorY = pointBean.getY() - firstPoint.getY();
            float press = pointBean.getPress();
            VectorBean vectorBean = new VectorBean(vectorX, vectorY, press);
            vectorList.add(vectorBean);
        }

        // 发送vectorList给服务端
        mDialog.show();
        DataManager.getInstance().addList(mUserName, mCellphone, mActValue, false, vectorList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResponse<EmptyResponse>>() {
                    @Override
                    public void accept(BaseResponse<EmptyResponse> response) {
                        mDialog.dismiss();
                        HeaderModel headerModel = response.getHeader();
                        if (Constant.OK.equals(headerModel.getErrorCode())) {
                            Toast.makeText(HandWritingCustomActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                            if (mSubmitCount >= 10) {
                                startActivity(new Intent(HandWritingCustomActivity.this, MainActivity.class));
                            } else {
                                if (mTouchView != null) {
                                    mTouchView.reset();
                                }
                                mCountTv.setText("此时是第(" + ++mSubmitCount + " / 10)次书写");
                            }

                        } else {
                            Toast.makeText(HandWritingCustomActivity.this, headerModel.getErrorMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        mDialog.dismiss();
                        Toast.makeText(HandWritingCustomActivity.this, "网络错误:" + throwable.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void reset(View view) {
        if (mTouchView != null) {
            mTouchView.reset();
        }
    }
}
