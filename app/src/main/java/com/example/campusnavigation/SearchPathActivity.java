package com.example.campusnavigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SearchPathActivity extends AppCompatActivity {
    private EditText start;
    private EditText destination;
    private Button searchpath;
    private ImageView back;
    private Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchpath);

        start = findViewById(R.id.et_start);
        destination = findViewById(R.id.et_end);
        searchpath = findViewById(R.id.search);
        back = findViewById(R.id.back);

        searchpath.setOnClickListener(v -> {
            String startStr = start.getText().toString();
            String destStr = destination.getText().toString();
            if ("".equals(startStr) || startStr.length() == 0 || "".equals(destStr) || destStr.length() == 0) {
                Toast.makeText(context, "请输入正确的起点或终点", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent();
                intent.putExtra("start", startStr);
                intent.putExtra("dest", destStr);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        back.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        });
    }
}
