package com.nuaa.handwriting;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nuaa.handwriting.constant.Constant;
import com.nuaa.handwriting.data.DataManager;
import com.nuaa.handwriting.model.BaseResponse;
import com.nuaa.handwriting.model.HeaderModel;
import com.nuaa.handwriting.model.QueryListResponse;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class QueryHistoryActivity extends AppCompatActivity {

    private ProgressDialog mDialog;

    private LinearLayout mLLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_history);

        String userName = getIntent().getStringExtra(Constant.BUNDLE_KEY_USER_NAME);
        String cellphone = getIntent().getStringExtra(Constant.BUNDLE_KEY_CELL_PHONE);

        mLLView = findViewById(R.id.ll_view);

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("请求中...");
        mDialog.show();
        DataManager.getInstance().queryList(userName, cellphone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResponse<List<QueryListResponse>>>() {
                    @Override
                    public void accept(BaseResponse<List<QueryListResponse>> response) {
                        mDialog.dismiss();
                        HeaderModel headerModel = response.getHeader();
                        if (Constant.OK.equals(headerModel.getErrorCode())) {
                            List<QueryListResponse> dataList = response.getBody();
                            if (dataList == null || dataList.size() == 0) {
                                Toast.makeText(QueryHistoryActivity.this, "暂无数据", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            for (QueryListResponse data : dataList) {
                                TextView textView1 = new TextView(QueryHistoryActivity.this);
                                textView1.setText(data.getValue());
                                textView1.setTextSize(20);
                                mLLView.addView(textView1);
                                TextView textView2 = new TextView(QueryHistoryActivity.this);
                                textView2.setText(data.getUrl());
                                textView2.setTextIsSelectable(true);
                                textView2.setTextSize(20);
                                mLLView.addView(textView2);
                            }
                        } else {
                            Toast.makeText(QueryHistoryActivity.this, headerModel.getErrorMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        mDialog.dismiss();
                        Toast.makeText(QueryHistoryActivity.this, "网络错误:" + throwable.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
