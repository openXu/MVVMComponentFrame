package com.openxu;

import android.content.Intent;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.openxu.base.BaseActivity;
import com.openxu.core.base.XLifecycleOwnerActivity;
import com.openxu.mvc.LoginActivity;
import com.openxu.mvvm.R;

public class MainActivity extends BaseActivity {

    private Button btn_mvc, btn_mvp, btn_mvvm, btn_lifecycle;

    @Override
    protected int getContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        btn_mvc = findViewById(R.id.btn_mvc);
        btn_mvp = findViewById(R.id.btn_mvp);
        btn_mvvm = findViewById(R.id.btn_mvvm);
        btn_lifecycle = findViewById(R.id.btn_lifecycle);
        btn_mvc.setOnClickListener(v->startActivity(new Intent(this, LoginActivity.class)));
        btn_mvp.setOnClickListener(v->startActivity(new Intent(this, com.openxu.mvp.view.LoginActivity .class)));
        btn_mvvm.setOnClickListener(v->startActivity(new Intent(this, com.openxu.mvvm.LoginActivity .class)));
        btn_lifecycle.setOnClickListener(v->startActivity(new Intent(this, XLifecycleOwnerActivity.class)));
    }
}
