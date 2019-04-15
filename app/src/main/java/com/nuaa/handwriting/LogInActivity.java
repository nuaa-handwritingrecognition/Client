package com.nuaa.handwriting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
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

import static com.nuaa.handwriting.constant.Constant.BUNDLE_KEY_ACT_VALUE;
import static com.nuaa.handwriting.constant.Constant.BUNDLE_KEY_CELL_PHONE;
import static com.nuaa.handwriting.constant.Constant.BUNDLE_KEY_USER_NAME;

public class LogInActivity extends AppCompatActivity {

    private TouchView mTouchView;

    private String mUserName;

    private String mCellphone;

    private TextView mCountTv;

    private ProgressDialog mDialog;

    private int mSubmitCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Log.d("LogInActivity","LogIn execute");

        mUserName = getIntent().getStringExtra(BUNDLE_KEY_USER_NAME);
        mCellphone = getIntent().getStringExtra(BUNDLE_KEY_CELL_PHONE);

        mTouchView = findViewById(R.id.touch_view1);
        mCountTv = findViewById(R.id.tv_count);

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
        DataManager.getInstance().addList(mUserName, mCellphone, "方", true, vectorList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResponse<EmptyResponse>>() {
                    @Override
                    public void accept(final BaseResponse<EmptyResponse> response) {
                        HeaderModel headerModel = response.getHeader();
                        if (Constant.OK.equals(headerModel.getErrorCode())) {
                            Toast.makeText(LogInActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                            if (mSubmitCount >= 1) {
                                DataManager.getInstance().queryValue(mUserName, mCellphone)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Consumer<BaseResponse<String>>() {
                                            @Override
                                            public void accept(BaseResponse<String> responseBaseResponse) {
                                                mDialog.dismiss();
                                                Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                                                intent.putExtra(BUNDLE_KEY_USER_NAME, mUserName);
                                                intent.putExtra(BUNDLE_KEY_CELL_PHONE, mCellphone);
                                                intent.putExtra(BUNDLE_KEY_ACT_VALUE, responseBaseResponse.getBody());
                                                startActivity(intent);
                                                /*if (TextUtils.isEmpty(responseBaseResponse.getBody())) {
                                                    CustomDialogFragment fragment = CustomDialogFragment.getInstance(new CustomDialogFragment.OnClickListener() {
                                                        @Override
                                                        public void onClick(String actValue) {
                                                            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                                                            intent.putExtra(BUNDLE_KEY_USER_NAME, mUserName);
                                                            intent.putExtra(BUNDLE_KEY_CELL_PHONE, mCellphone);
                                                            intent.putExtra(BUNDLE_KEY_ACT_VALUE, actValue);
                                                            startActivity(intent);
                                                        }
                                                    });
                                                    fragment.setCancelable(false);
                                                    fragment.show(getSupportFragmentManager(), "");
                                                } else {
                                                    Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                                                    intent.putExtra(BUNDLE_KEY_USER_NAME, mUserName);
                                                    intent.putExtra(BUNDLE_KEY_CELL_PHONE, mCellphone);
                                                    intent.putExtra(BUNDLE_KEY_ACT_VALUE, responseBaseResponse.getBody());
                                                    startActivity(intent);
                                                }*/
                                            }
                                        }, new Consumer<Throwable>() {
                                            @Override
                                            public void accept(Throwable throwable) {
                                                mDialog.dismiss();
                                                Toast.makeText(LogInActivity.this, "网络错误:" + throwable.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                mDialog.dismiss();
                                if (mTouchView != null) {
                                    mTouchView.reset();
                                }
                                //mCountTv.setText("此时是第(" + ++mSubmitCount + " / 10)次书写");
                            }
                        } else {
                            mDialog.dismiss();
                            Toast.makeText(LogInActivity.this, headerModel.getErrorMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        mDialog.dismiss();
                        Toast.makeText(LogInActivity.this, "网络错误:" + throwable.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void reset(View view) {
        if (mTouchView != null) {
            mTouchView.reset();
        }
    }
}
