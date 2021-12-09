package com.example.ict2021menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ict2021menu.utils.toastUtil;

/*
登录界面的处理
内容:1.实现账户和密码的匹配=====>>未实现注册功能,目前"admin"(用户名和密码均为这个)可登录或者直接点击登录
 */
public class login_Activity extends AppCompatActivity implements View.OnClickListener{

    private EditText et_Username;
    private EditText et_Password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //找到控件
        //声明控件
        Button buttonLogin = findViewById(R.id.button_login);
        et_Username=findViewById(R.id.et_userName);
        et_Password=findViewById(R.id.et_userPassword);

        /*实现跳转 方法1
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=null;
                intent=new Intent(MainActivity.this,functionActivity.class);
                startActivity(intent);
            }
       });*/


        buttonLogin.setOnClickListener(this);
    }

    //账户与密码匹配即可跳转
    public void onClick(View v){
        //需要获取输入的用户名和密码
        String userName =et_Username.getText().toString();
        String password =et_Password.getText().toString();
        //弹出内容
        String ok="登陆成功!";
        String fail="账号或者密码有误,请重新登陆!";

        //假设正确的账号和密码分别是 admin admin
        if((userName.equals("admin")&&password.equals("admin"))||(userName.equals("")&&password.equals("")))
        {
            //toast 普通版
            //Toast.makeText(getApplicationContext(),ok,Toast.LENGTH_SHORT).show();

            //封装好的类
            toastUtil.showMsg(getApplicationContext(),ok);
            try {
                Thread.sleep(1000); //1000 毫秒，也就是1秒.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            //正确的话跳转
            Intent intent=new Intent(login_Activity.this,home_Activity.class);
            startActivity(intent);
        } else{
            //不正确,弹出登陆失败toast
            //普通版
            //Toast.makeText(getApplicationContext(),fail,Toast.LENGTH_SHORT).show();
            //封装版
            toastUtil.showMsg(getApplicationContext(),fail);
        }

    }

}

