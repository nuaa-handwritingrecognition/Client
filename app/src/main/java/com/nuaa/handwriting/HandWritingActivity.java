package com.nuaa.handwriting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nuaa.handwriting.model.PointBean;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.nuaa.handwriting.constant.Constant.BUNDLE_KEY_PHONE;
import static com.nuaa.handwriting.constant.Constant.BUNDLE_KEY_URL;
import static com.nuaa.handwriting.constant.Constant.FAILURE;
import static com.nuaa.handwriting.constant.Constant.SUCCESS;


/*
 *  注册界面后台逻辑
 * */
public class HandWritingActivity extends AppCompatActivity {

    private TouchView mTouchView;

    private String mPhone;

    private String mUrl;

    private TextView mCountTv;

    private ProgressDialog mDialog;

    private int mSubmitCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hand_writing);
//        Log.d("HandWritingActivity", "onCreate execute");

        mPhone = getIntent().getStringExtra(BUNDLE_KEY_PHONE);
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

//        如果数据不满150，用最后一个数据填充到3s
        PointBean firstPoint = pointList.get(0);
        PointBean lastPoint = pointList.get(pointList.size() - 1);
        int count = 150 - pointList.size();
        for (int i = 0; i < count; i++) {
            pointList.add(lastPoint);
        }
        ArrayList<String> transList = new ArrayList<>();
        for (PointBean pointBean : pointList) {
            float vectorX = pointBean.getX() - firstPoint.getX();
            float vectorY = pointBean.getY() - firstPoint.getY();
            float press = pointBean.getPress();
//            封装数据
            String data = String.valueOf(vectorX) + "!" + String.valueOf(vectorY) + "!" + String.valueOf(press);
            transList.add(data);
        }
        mDialog.show();
//        发送给服务器
        SendMessage(mUrl, mPhone, transList);
    }


    private void SendMessage(String url, final String phone, ArrayList<String> list) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("phone", phone);
        String data = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            data = String.join("\n", list);
        }
        formBuilder.add("data", data);

        Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            // 长时间没响应也出出现Failure
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(HandWritingActivity.this, "服务器或网络错误", Toast.LENGTH_SHORT).show();
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
                        if (res.equals(SUCCESS)) {
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
                        } else if (res.equals(FAILURE)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(HandWritingActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(HandWritingActivity.this, "未知错误，请详细管理员", Toast.LENGTH_SHORT).show();
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
