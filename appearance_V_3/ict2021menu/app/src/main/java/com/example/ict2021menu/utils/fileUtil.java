package com.example.ict2021menu.utils;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;

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
    public static String data2Base64(String data){
        byte[] datas = data.getBytes();
        String encodeStr = Base64.encodeToString(datas,Base64.NO_WRAP);
        return encodeStr;
    }
}
