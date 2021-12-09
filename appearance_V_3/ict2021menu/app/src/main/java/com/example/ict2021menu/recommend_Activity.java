package com.example.ict2021menu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.ict2021menu.utils.ImageViewLoadAndSetSizeAndCircle;

import java.net.URL;


/*
今日推荐界面显示:=======>功能未定
 */
public class recommend_Activity extends AppCompatActivity {

    private TextView result_view;
    private ImageView result_image;//1600*1200
    private Button button_recommend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);


        //1-找到控件
        result_view=(TextView) findViewById(R.id.recommend_result);
        button_recommend =findViewById(R.id.recommend_button);
        result_image= ( ImageView )findViewById(R.id.recommend_image);

        //对文本内容进行修改


        //2-实现功能

        button_recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri=Uri.parse("http://www.taopic.com/uploads/allimg/110928/41-11092PSF482.jpg");
                result_image.setImageURI(uri);

                //new ImageViewLoadAndSetSizeAndCircle().setImage(result_image,"http://www.taopic.com/uploads/allimg/110928/41-11092PSF482.jpg", 70,70,35);

                result_view.setText(return_result());

            }
        });


    }

    //返回结果
    private String return_result(){
        return "    推荐菜品：青椒土豆丝；\n   食材：土豆，青椒；\n     口味：微辣；\n    难度：简单";
    }




}