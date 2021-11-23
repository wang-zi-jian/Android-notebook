package ch.neet.wzj.notebook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {
    private Button login;//登录按钮
    private Button save;//登录按钮
    private Button restore;//登录按钮
    private EditText nameEdit;//用户名输入框
    private String Author;
    SharedPreferences prefs;//定义一个SharedPreferences对象
    SharedPreferences.Editor editor;//调用SharedPreferences对象的edit()方法来获取一个SharedPreferences.Editor对象，用以添加要保存的数据


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        Button save = (Button) findViewById(R.id.save_data);
        Button restore = (Button) findViewById(R.id.restore_data);
        Button login = (Button) findViewById(R.id.login_button);


        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editor = getSharedPreferences("data",
                        MODE_PRIVATE).edit();
                //获取姓名
                EditText name = (EditText) findViewById(R.id.name);
                editor.putString("name", name.getText().toString());
                //获取年龄
                EditText age = (EditText) findViewById(R.id.age);
                if(TextUtils.isEmpty(age.getText().toString()))
                    editor.putInt("age", 0);
                else
                    editor.putInt("age", Integer.parseInt(age.getText().toString()));
                editor.commit();

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //读出用户名和密码并判断是否正确
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                //结束当前活动
                finish();
            }
        });

        restore.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                prefs = getSharedPreferences("data", MODE_PRIVATE);
                String name = prefs.getString("name", "");
                //获取年龄
                int age = prefs.getInt("age", 0);
                EditText nameText = (EditText) findViewById(R.id.name);
                nameText.setText(String.valueOf(name));
                EditText ageText = (EditText) findViewById(R.id.age);
                ageText.setText(String.valueOf(age));
            }
        });
    }
}

