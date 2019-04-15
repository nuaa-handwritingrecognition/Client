package com.nuaa.handwriting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nuaa.handwriting.constant.Constant;
import com.nuaa.handwriting.data.DataManager;
import com.nuaa.handwriting.model.BaseResponse;
import com.nuaa.handwriting.model.CheckIndentityResponse;
import com.nuaa.handwriting.model.HeaderModel;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.nuaa.handwriting.constant.Constant.BUNDLE_KEY_CELL_PHONE;
import static com.nuaa.handwriting.constant.Constant.BUNDLE_KEY_USER_NAME;

public class MainActivity extends AppCompatActivity {

    private EditText mEditText;

    private EditText mCellphoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText = findViewById(R.id.et_username);
        mCellphoneEditText = findViewById(R.id.et_cellphone);
    }

    public void onClick(View view) {
        String username = mEditText.getText().toString();
        if (TextUtils.isEmpty(username)) {
            mEditText.requestFocus();
            mEditText.setError("用户名为空");
            return;
        }

        String cellphone = mCellphoneEditText.getText().toString();
        if (TextUtils.isEmpty(cellphone)) {
            mCellphoneEditText.requestFocus();
            mCellphoneEditText.setError("手机号为空");
            return;
        }

        switch (view.getId()) {
            case R.id.btn_write:
                checkIndentity(username, cellphone);
                break;
            case R.id.btn_log:
                checkIndentity_log(username,cellphone);
                break;
            case R.id.btn_query:
                Intent intent2 = new Intent(this, QueryHistoryActivity.class);
                intent2.putExtra(BUNDLE_KEY_USER_NAME, username);
                intent2.putExtra(BUNDLE_KEY_CELL_PHONE, cellphone);
                startActivity(intent2);
                break;
        }
    }

    public void record(View view) {
        startActivity(new Intent(this, RecordActivity.class));
    }

    private void checkIndentity(final String userName, final String phone) {//用于注册时判断用户
        DataManager.getInstance().checkIndentity(userName, phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResponse<CheckIndentityResponse>>() {
                    @Override
                    public void accept(BaseResponse<CheckIndentityResponse> response) throws Exception {
                        HeaderModel headerModel = response.getHeader();
                        if (Constant.OK.equals(headerModel.getErrorCode())) {
                            CheckIndentityResponse body = response.getBody();
                            //System.out.println("Responce:"+response.toString());
                            if (body.isValidate()) {//服务器上面没有该用户名或者用户名和手机号全部与服务器存储相同
                                goWritePage(userName, phone);
                            } else {
                                String type = body.getType();
                                ArrayList<String> list = new ArrayList(body.getList());
                                CheckIndentityFragment fragment = CheckIndentityFragment.getInstance(userName, phone, type, list, new CheckIndentityFragment.GoWriteListener() {
                                    @Override
                                    public void wirite() {
                                        goWritePage(userName, phone);
                                    }
                                });
                                fragment.show(getSupportFragmentManager(), "checkIndentity");
                            }
                        } else {
                            Toast.makeText(MainActivity.this, headerModel.getErrorMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "网络错误:" + throwable.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkIndentity_log(final String userName, final String phone) {//用于登录时判断用户
        DataManager.getInstance().checkIndentity(userName, phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResponse<CheckIndentityResponse>>() {
                    @Override
                    public void accept(BaseResponse<CheckIndentityResponse> response) throws Exception {
                        HeaderModel headerModel = response.getHeader();
                        if (Constant.OK.equals(headerModel.getErrorCode())) {
                            CheckIndentityResponse body = response.getBody();
                            //System.out.println(body.toString());
                            if (body.isValidate()) {//服务器上面没有这个用户名或者用户名和手机号全部与服务器存储相同
                                goLogPage(userName, phone);
                            } else {
                                String type = body.getType();
                                ArrayList<String> list = new ArrayList(body.getList());
                                CheckIndentityFragment fragment = CheckIndentityFragment.getInstance(userName, phone, type, list, new CheckIndentityFragment.GoWriteListener() {
                                    @Override
                                    public void wirite() {//在这里要改成老用户直接登录
                                        goWritePage(userName, phone);
                                    }
                                });
                                fragment.show(getSupportFragmentManager(), "checkIndentity");
                            }
                        } else {
                            Toast.makeText(MainActivity.this, headerModel.getErrorMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "网络错误:" + throwable.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goWritePage(String username, String cellphone) {
        Intent intent = new Intent(this, HandWritingActivity.class);
        intent.putExtra(BUNDLE_KEY_USER_NAME, username);
        intent.putExtra(BUNDLE_KEY_CELL_PHONE, cellphone);
        startActivity(intent);
    }

    private  void goLogPage(String username, String cellphone) {
        Intent intent = new Intent(this, LogInActivity.class);
        intent.putExtra(BUNDLE_KEY_USER_NAME, username);
        intent.putExtra(BUNDLE_KEY_CELL_PHONE, cellphone);
        startActivity(intent);
    }
}
