package com.example.ict2021menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ict2021menu.utils.MyImageView;
import com.example.ict2021menu.utils.bafaCloudUtil;
import com.example.ict2021menu.utils.fileUtil;
import com.example.ict2021menu.utils.huaweiCloudUtil;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/*
今日推荐界面显示:=======>功能未定
 */
public class recommend_Activity extends AppCompatActivity {

    private TextView result_view;
    //private ImageView result_image;//1600*1200
    private Button button_recommend;
    private String url_pic;
    private TextView food_names;
    private TextView food_information;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);


        //1-找到控件

        food_names=(TextView)findViewById((R.id.Food_identification)) ;
        food_information=(TextView)findViewById(R.id.recommend_result) ;
        button_recommend =findViewById(R.id.recommend_button);

       // result_image= ( ImageView )findViewById(R.id.recommend_image);

        final MyImageView myImageView = (MyImageView) findViewById(R.id.recommend_image);


        //2-实现功能

        /*
        ===》图片更新：控件名.setImageURL(url);  url字符串
        ===》信息更新：控件名.setText(information);  information
         */
        //点击按钮实现图片和菜品信息更新
        button_recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //（a）获取更新图片url，传入myimageView控件进行更新图片
                //（b)返回识别结果
                //（c)更新菜品信息
                image_url(myImageView,food_names,food_information);
            }
        });


    }

    //巴法云请求图片url
    private void image_url(MyImageView image_1,TextView food_name0,TextView food_information0){
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = bafaCloudUtil.re4Image();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }//发送失败回调函数

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String string = response.body().string();
                String url=bafaCloudUtil.parse4ImgUrl(string);
                image_1.setImageURL(url);
                getImg4_Bafa(url,food_name0,food_information0);
            }
        });
    }




    //巴法云请求图片base64
    private void getImg4_Bafa(String url,TextView food_name1,TextView food_information1){
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }//发送失败回调函数

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final byte[] bytes = response.body().bytes();
                String base64= fileUtil.data2Base64(bytes);
                getItem4Huawei(base64, food_name1,food_information1);
            }
        });
    }

    //根据图片base64华为云图像识别
    private void getItem4Huawei(String base64, TextView food_name2, TextView food_information2){
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();
        Request request= huaweiCloudUtil.re4imgData(base64);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }//发送失败回调函数
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String string = response.body().string();
                List<String> name=huaweiCloudUtil.parse4ImgData(string);
                String material_name=name.toString();
                doRe2Question(name,food_information2);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        food_name2.setText(material_name);
                    }
                });
            }
        });
    }

    //根据食材列表进行构建问句查询
    private void doRe2Question(List<String> name, TextView food_information1){
        String question="我有"+name.toString()+"可以做什么";
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = huaweiCloudUtil.re4Answer(question);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }//发送失败回调函数

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String string = response.body().string();
                //得到了关于菜名的回答，随机选取一道菜名
                String text="青椒土豆丝";
                doInfo2DishName(text,"难度和口味",food_information1);
            }
        });
    }


    //根据菜的名字获得相关信息
    private void doInfo2DishName(String name,String info,TextView food_information2){
        String question=name+"的"+info+"怎么样";
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = huaweiCloudUtil.re4Answer(question);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }//发送失败回调函数

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String string = response.body().string();
                String text="这道菜的名字是青椒土豆丝，它的难度是简单，它的口味是微辣";
                //得到了关于菜名的相关信息回答
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TODO:在这里对ui内返回的菜名相关信息 text 进行处理
                        food_information2.setText(text);
                    }
                });
            }
        });
    }

}