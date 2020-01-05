package com.example.campusnavigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddPathActivity extends AppCompatActivity {
    private Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchpath);

        TextView title = findViewById(R.id.textView);
        Button button = findViewById(R.id.search);
        ImageView back = findViewById(R.id.back);
        title.setText("增加路径");
        button.setText("确认");
        EditText startText = findViewById(R.id.et_start);
        EditText endText = findViewById(R.id.et_end);

        button.setOnClickListener(v -> {
            String start = startText.getText().toString();
            String dest = endText.getText().toString();
            if ("".equals(start) || start.length() == 0 || "".equals(dest) || dest.length() == 0) {
                Toast.makeText(context, "输入有误，请重新输入", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent();
                intent.putExtra("start", start);
                intent.putExtra("dest", dest);
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
