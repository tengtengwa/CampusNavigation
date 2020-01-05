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

public class QueryActivity extends AppCompatActivity {
    private Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        Button query = findViewById(R.id.query);
        EditText editText = findViewById(R.id.et_end);
        ImageView back = findViewById(R.id.back);

        back.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        });

        query.setOnClickListener(v -> {
            String spotName = editText.getText().toString();
            if ("".equals(spotName) || spotName.length() == 0) {
                Toast.makeText(context, "输入有误，请重新输入", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent();
                intent.putExtra("spotName", spotName);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
