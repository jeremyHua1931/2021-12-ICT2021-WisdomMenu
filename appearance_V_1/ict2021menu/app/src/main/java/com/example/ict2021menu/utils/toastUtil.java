package com.example.ict2021menu.utils;

import android.content.Context;
import android.widget.Toast;

/*
主要实现类似"登录成功"或者"登录失败"的提示
 */
public class toastUtil
{
    public static Toast mToast;
    public static void showMsg(Context context,String msg){
        if(mToast==null){
            mToast=Toast.makeText(context,msg,Toast.LENGTH_SHORT);
        }else{
            mToast.setText(msg);
        }
        mToast.show();
    }
}
