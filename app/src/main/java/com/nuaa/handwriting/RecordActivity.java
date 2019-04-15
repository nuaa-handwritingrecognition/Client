package com.nuaa.handwriting;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.nuaa.handwriting.constant.Constant;
import com.nuaa.handwriting.data.DataManager;
import com.nuaa.handwriting.model.BaseResponse;
import com.nuaa.handwriting.model.HeaderModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RecordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        final TextView urlTv = findViewById(R.id.tv_url);

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("请求中...");
        dialog.show();
        DataManager.getInstance().pullData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResponse<String>>() {
                    @Override
                    public void accept(BaseResponse<String> response) {
                        dialog.dismiss();
                        HeaderModel headerModel = response.getHeader();
                        if (Constant.OK.equals(headerModel.getErrorCode())) {
                            urlTv.setText(response.getBody());
                        } else {
                            Toast.makeText(RecordActivity.this, headerModel.getErrorMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        dialog.dismiss();
                        Toast.makeText(RecordActivity.this, "网络错误:" + throwable.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
