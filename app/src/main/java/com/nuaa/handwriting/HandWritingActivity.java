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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.nuaa.handwriting.constant.Constant.BUNDLE_KEY_ACT_VALUE;
import static com.nuaa.handwriting.constant.Constant.BUNDLE_KEY_CELL_PHONE;
import static com.nuaa.handwriting.constant.Constant.BUNDLE_KEY_URL;
import static com.nuaa.handwriting.constant.Constant.BUNDLE_KEY_USER_NAME;

public class HandWritingActivity extends AppCompatActivity {

    private TouchView mTouchView;

    private String mUserName;

    private String mCellphone;

    private String mUrl;

    private TextView mCountTv;

    private ProgressDialog mDialog;

    private int mSubmitCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hand_writing);
        Log.d("HandWritingActivity", "onCreate execute");

        mUserName = getIntent().getStringExtra(BUNDLE_KEY_USER_NAME);
        mCellphone = getIntent().getStringExtra(BUNDLE_KEY_CELL_PHONE);
        mUrl = getIntent().getStringExtra(BUNDLE_KEY_URL);

        mTouchView = findViewById(R.id.touch_view);
        mCountTv = findViewById(R.id.tv_count);

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
        ArrayList<String> transList = new ArrayList<>();
//        System.out.println("数据样本");
        for (VectorBean bean : vectorList) {
//            Log.e("cyw",bean.getVectorX()+ "!!!" + bean.getVectorY());

            String data = String.valueOf(bean.getVectorX()) + "!" + String.valueOf(bean.getVectorY()) + "!" +
                    String.valueOf(bean.getPress());
            transList.add(data);
        }
        SendMessage(mUrl, mUserName, mCellphone, transList);
    }


    private void SendMessage(String url, final String userName, String passWord, ArrayList<String> list) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
//        System.out.println("username"+ userName);
        formBuilder.add("username", userName);
        formBuilder.add("password", passWord);
//        formBuilder.add("length", String.valueOf(list.size()));
        String data = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            data = String.join("\n", list);
        }
        formBuilder.add("data", data);

        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
        Call call = client.newCall(request);
        System.out.println("cyw");
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(HandWritingActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                mDialog.cancel();//如果服务器有反应就取消对话框
                final String res = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res.equals("1")) {//服务器返回1说明成功
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(HandWritingActivity.this, "成功", Toast.LENGTH_SHORT).show();
                                }
                            });
                            if (mSubmitCount >= 10) {//如果注册超过了10次
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(HandWritingActivity.this, "注册完毕", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                startActivity(new Intent(HandWritingActivity.this, MainActivity.class));
                            } else {
                                if (mTouchView != null) {//重置面板
                                    mTouchView.reset();
                                }
                                mCountTv.setText("此时是第(" + ++mSubmitCount + " / 10)次书写");
                            }
                        } else {//返回其他值说明出现了问题
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(HandWritingActivity.this, "该用户名已被注册", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
    }


    public void reset(View view) {
        if (mTouchView != null) {
            mTouchView.reset();
        }
    }

}
