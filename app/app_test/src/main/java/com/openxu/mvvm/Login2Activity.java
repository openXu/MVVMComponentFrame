package com.openxu.mvvm;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Login2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        ((TextView)findViewById(R.id.tv_text)).setText("第1层（半透明）");
        startActivity(new Intent(this, Login3Activity.class));
    }

}
