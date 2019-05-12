package com.nuaa.handwriting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import static com.nuaa.handwriting.constant.Constant.BUNDLE_KEY_URL;
import static com.nuaa.handwriting.constant.Constant.BUNDLE_KEY_PHONE;

public class MainActivity extends AppCompatActivity {

    private EditText mEditText;

    private EditText mServerIPEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText = findViewById(R.id.et_phone);
        mServerIPEditText = findViewById(R.id.et_server);
    }

    public void onClick(View view) {
        String username = mEditText.getText().toString();
        if (TextUtils.isEmpty(username)) {
            mEditText.requestFocus();
            mEditText.setError("手机号为空");
            return;
        }

        String serverIP = mServerIPEditText.getText().toString();
        if (TextUtils.isEmpty(serverIP)) {
            mServerIPEditText.requestFocus();
            mServerIPEditText.setError("服务器IP为空");
            return;
        }

        switch (view.getId()) {
            case R.id.btn_write:
                goRegisterPage(username, serverIP + "/register");
                break;
            case R.id.btn_log:
                goLogPage(username, serverIP + "/login");
                break;

        }
    }

    private void goRegisterPage(String username, String url) {
        Intent intent = new Intent(this, HandWritingActivity.class);
        intent.putExtra(BUNDLE_KEY_PHONE, username);
        intent.putExtra(BUNDLE_KEY_URL, url);
        startActivity(intent);
    }

    private void goLogPage(String username, String url) {
        Intent intent = new Intent(this, LogInActivity.class);
        intent.putExtra(BUNDLE_KEY_PHONE, username);
        intent.putExtra(BUNDLE_KEY_URL, url);
        startActivity(intent);
    }

}
