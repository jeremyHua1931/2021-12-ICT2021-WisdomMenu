package com.example.ict2021menu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.ict2021menu.utils.fileUtil;
import com.example.ict2021menu.utils.huaweiCloudUtil;
import com.example.ict2021menu.utils.toastUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/*
主界面处理, 内容:
1.点击头像实现侧滑菜单, 再次点击头像返回主界面;
2.今日推荐=>随机推荐菜品??
3.智能交互=>问答系统
4.开始做饭=>启用摄像头进行开始扫描食材,推荐菜品
5.语音助手=>语音对话,识别后,跳转到交互窗口,返回结果
 */
public class home_Activity extends AppCompatActivity {

    //声明控件

    private SlideMenu slideMenu;

    private Button recommend;
    private Button intelligent_interaction;
    private Button startCooking;
    private Button voice_help;
    private Button back;

    public static final List<Msg> msgList = new ArrayList<>();//改为final


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initMegs();

        //语音文件临时保存目录
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.amr";
        //请求权限
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        /*1-找到控件*/

        // (1)实现点击头像展开侧滑菜单
        ImageView mIvHead = findViewById(R.id.iv_head);
        slideMenu = findViewById(R.id.slideMenu);

        //(2)找到今日推荐,智能交互,开始做饭,语音助手按钮
        recommend = findViewById(R.id.button_recommend);
        intelligent_interaction = findViewById(R.id.button_voice);
        startCooking = findViewById(R.id.button_start);
        voice_help = findViewById(R.id.button_voice_2);

        back = findViewById(R.id.button_menu_6);


        /*2-实现主要功能*/

        //(1)实现侧滑,点击头像侧滑
        mIvHead.setOnClickListener(v -> slideMenu.switchMenu());

        //(2)对今日推荐,语音交互,语音助手,开始做饭按钮进行监控
        setListener();
    }


    //对对今日推荐,语音交互,开始做饭,语音助手 按钮进行监控,点击进行操作,所以统一处理
    private void setListener() {
        //onclick
        OnClick onClick = new OnClick();

        //对每一个按钮进行
        recommend.setOnClickListener(onClick);
        intelligent_interaction.setOnClickListener(onClick);
        startCooking.setOnClickListener((onClick));
        voice_help.setOnClickListener(onClick);
        back.setOnClickListener(onClick);
    }

    //对对今日推荐,语音交互,开始做饭三个按钮进行监控
    private class OnClick implements View.OnClickListener {

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            Intent intent = null;

            if (v.getId() == R.id.button_menu_6) {
                gotoDesk();

            }

            switch (v.getId()) {
                //(1)处理今日推荐按钮
                case R.id.button_recommend:
                    intent = new Intent(home_Activity.this, recommend_Activity.class);
                    startActivity(intent);
                    break;
                case R.id.button_voice:
                    //直接跳转到聊天界面
                    intent = new Intent(home_Activity.this, voice_Activity.class);
                    startActivity(intent);
                    break;
                case R.id.button_voice_2:
                    //进行语音识别,跳转到聊天界面

                    Hold_to_talk();
                    if(mStartRecording) {
                        speech_recognition();
                    }



                    break;


                case R.id.button_start:

                    intent = new Intent(home_Activity.this, startCooking_Activity.class);
                    startActivity(intent);
                    break;
            }
        }
    }

    private void initMegs(){
        Msg msg1 = new Msg("你好呀,我是你的智慧菜谱小助手",Msg.TYPE_RECEIVED);
        msgList.add(msg1);
        Msg msg2 = new Msg("Hi",Msg.TYPE_SENT);
        msgList.add(msg2);
        Msg msg3 = new Msg("有什么需要吗?,home页直接点击语音就可以跟我对话哦", Msg.TYPE_RECEIVED);
        msgList.add(msg3);
        Msg msg4 = new Msg("西红柿炒鸡蛋怎么做",Msg.TYPE_SENT);
        msgList.add(msg4);
        Msg msg5 = new Msg("步骤如下：1.西红柿洗净改刀切小块，鸡蛋加少许盐打开,2.热锅温油，倒入打散的蛋液,3.把锅摇匀一圈，让蛋液充分散开,4.再迅速用锅铲戳散翻炒均匀,5.盛出备用,6.锅里留底油，放入西红柿，翻炒,7.西红柿炒到出汁水，倒入鸡蛋，继续翻炒均匀，调入少许盐调味，关火,8.美味快手的家常西红柿炒鸡蛋就做好了。", Msg.TYPE_RECEIVED);
        msgList.add(msg5);
    }

    //返回桌面
    public void gotoDesk() {
        Toast.makeText(getApplicationContext(), "返回桌面", Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
    }



    /********************************************************************************************************************************************/

    public void Hold_to_talk() {
        RecordtoFile();
    }

    /********************************************************************************************************************************************/


    public void speech_recognition() {
        String audiodata=fileUtil.readAudio2Base64(fileName);
        doRe2sAudio(audiodata);
    }


    //语音录入
    private static String fileName = null;
    private static final String LOG_TAG = "AudioRecord";
    private MediaRecorder recorder = null;
    private boolean mStartRecording = true;
    // Requesting permission to RECORD_AUDIO
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO,Manifest.permission.INTERNET};



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }


    private void RecordtoFile() {

        onRecord(mStartRecording);
        if (mStartRecording) {
            toastUtil.showMsg(getApplicationContext(), "开始录音");
        } else {
            toastUtil.showMsg(getApplicationContext(),"停止录音");
        }
        mStartRecording = !mStartRecording;
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }


    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            toastUtil.showMsg(getApplicationContext(),"prepare() failed");
        }
        recorder.start();
    }


    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    //异步网络请求
    //一句话语音识别
    private void doRe2sAudio(String audiodata){
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = huaweiCloudUtil.re4Audio(audiodata);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }//发送失败回调函数

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String string = response.body().string();
                //返回ui线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //ui线程操作主要在这里，对对话ui的处理
                        //toastUtil.showMsg(getApplicationContext(),string);
                        final String text=huaweiCloudUtil.parse4AuData(string);
                        Msg msg_no = new Msg(text,Msg.TYPE_SENT);
                        msgList.add(msg_no);
                        doRe2Question(huaweiCloudUtil.parse4AuData(string));
                    }
                });

            }
        });
    }
    //对请求的句子进行返回
    private void doRe2Question(String question){
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
                //返回ui线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //ui线程操作主要在这里，对ui的处理
                        //toastUtil.showMsg(getApplicationContext(),string);
                        Msg msg_no = new Msg(string,Msg.TYPE_RECEIVED);
                        msgList.add(msg_no);
                        //跳转逻辑
                        Intent intent = new Intent(home_Activity.this, voice_Activity.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }



}

