package com.example.ict2021menu.utils;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class fileUtil {
    public static String readAudio2Base64(String filename){
        File file = new File(filename);
        String encodeStr=new String();
        FileInputStream inputStream = null;
        try{
            inputStream  = new FileInputStream(file);
            byte[] buffer = new byte[(int)file.length()];
            inputStream.read(buffer);
            inputStream.close();
            encodeStr = Base64.encodeToString(buffer,Base64.NO_WRAP);
        }catch(Exception e){
            e.printStackTrace();
        }
        return encodeStr;
    }
    public static String data2Base64(byte[] datas){
        String encodeStr = Base64.encodeToString(datas,Base64.NO_WRAP);
        return encodeStr;
    }
    public static String imageToBase64(String path){
        FileInputStream is = null;
        byte[] data = null;
        String result = null;
        try{
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data,Base64.NO_CLOSE);
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
