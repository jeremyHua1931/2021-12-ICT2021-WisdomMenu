package com.example.ict2021menu.utils;

import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class bafaCloudUtil {
    public static Request re4Image() {
        Request request = new Request.Builder()
                .url("https://images.bemfa.com/cloud/v1/get/?uid=a2fd6e67e9153a08b06291f610f2e67b&topic=test5types&num=1")
                .method("GET", null)
                .build();
        return  request;
    }
    public static String parse4ImgUrl(String result){
        response4bafa response = JSONObject.parseObject(result,response4bafa.class);
        return response.getData().get(0).getUrl();
    }
}
class response4bafa{
    private String status;
    private long code;
    private List<Data> data;
    private int count;
    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }

    public void setCode(long code) {
        this.code = code;
    }
    public long getCode() {
        return code;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
    public List<Data> getData() {
        return data;
    }

    public void setCount(int count) {
        this.count = count;
    }
    public int getCount() {
        return count;
    }
}
class Data {
    private String url;

    private String time;

    public void setUrl(String url){
        this.url = url;
    }
    public String getUrl(){
        return this.url;
    }
    public void setTime(String time){
        this.time = time;
    }
    public String getTime(){
        return this.time;
    }

}

