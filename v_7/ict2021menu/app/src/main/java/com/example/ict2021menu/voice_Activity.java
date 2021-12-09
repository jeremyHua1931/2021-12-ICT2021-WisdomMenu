package com.example.ict2021menu;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ict2021menu.utils.bafaCloudUtil;
import com.example.ict2021menu.utils.fileUtil;
import com.example.ict2021menu.utils.huaweiCloudUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.example.ict2021menu.utils.toastUtil;

public class voice_Activity extends AppCompatActivity {


    private final List<Msg> msgList = home_Activity.msgList;//改为final
    private EditText inputText;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private ImageButton imageButton_voice;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        //语音文件临时保存目录
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.amr";
        //请求权限
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);


        //初始化消息
        //initMegs();

        //找到输入文本和发送按钮控件
        inputText = (EditText)findViewById(R.id.input_text);
        Button send = (Button) findViewById(R.id.send);

        imageButton_voice=findViewById(R.id.voice_button_1);


        msgRecyclerView = (RecyclerView)findViewById(R.id.msg_recycle_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        msgRecyclerView.setLayoutManager(layoutManager);

        adapter = new MsgAdapter(msgList);

        msgRecyclerView.setAdapter(adapter);



        /*
        将语音识别结果内容以String传递到这
        Msg msg4 = new Msg("String result",Msg.TYPE_RECEIVED);
        msgList.add(msg4);
         */


        send.setOnClickListener(v -> {
            String content = inputText.getText().toString();

            if(!"".equals(content)){
                Msg msg = new Msg(content,Msg.TYPE_SENT);

                msgList.add(msg);
                //当有新消息，刷新RecyclerView的显示
                adapter.notifyItemInserted(msgList.size() - 1);
                //将RecyclerView定位到最后一行
                msgRecyclerView.scrollToPosition(msgList.size() - 1);
                //清空输入框内容
                inputText.setText("");

            }
            doRe2Question(content);
           // doRe2Bafa();
        });


        imageButton_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO 语音按钮处理
                //这一块我直接复制了home_activity里的语音识别,但是还是有点问题
                Hold_to_talk();
                if(mStartRecording) {
                    speech_recognition();
                }


            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public String QA(String question){


        StringBuffer tip1=new StringBuffer("对于您的问题: ");
        StringBuffer tip_question=new StringBuffer(question);
        tip1.append(tip_question);

        Msg msg_no = new Msg(tip1.toString(), Msg.TYPE_RECEIVED);
        msgList.add(msg_no);

        /*
        问答处理
         */
        Toast.makeText(getApplicationContext(),"正在处理中",Toast.LENGTH_SHORT).show();


        String tip="没有找到您想要的答案,或许您可以换个说法再试试?";
        return tip;
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
                String needphoto="[\"need_photo\"]";
                if(string.equals(needphoto)){
                    //在这里进行处理图片调用链条
                    getImage_url();
                }else{
                    //返回ui线程
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //ui线程操作主要在这里，
                            //toastUtil.showMsg(getApplicationContext(),string);
                            String text=huaweiCloudUtil.parse4Query(string);
                            Msg msg_no = new Msg(text,Msg.TYPE_RECEIVED);
                            msgList.add(msg_no);
                            //刷新
                            adapter.notifyItemInserted(msgList.size() - 1);
                            msgRecyclerView.scrollToPosition(msgList.size() - 1);
                        }
                    });
                }
            }
        });
    }

    //图像识别相关
    //巴法云请求图片url
    private void getImage_url(){
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = bafaCloudUtil.re4Image();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                //TODO:失败处理

            }//发送失败回调函数

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String string = response.body().string();
                String url=bafaCloudUtil.parse4ImgUrl(string);
                getImg4Bafa(url);
            }
        });
    }
    //巴法云请求图片base64
    private void getImg4Bafa(String url){
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                //TODO:失败处理
            }//发送失败回调函数

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final byte[] bytes = response.body().bytes();
                String base64= fileUtil.data2Base64(bytes);
                getItem4Huawei(base64);
            }
        });
    }
    //根据图片base64华为云图像识别
    private void getItem4Huawei(String base64){
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
                doRe2Question(name);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //在这里对ui内识别到的食材列表material_name进行得处理，例如输出:识别到的有xxxxx
                        Msg msg_no = new Msg("识别到的食材有"+material_name,Msg.TYPE_RECEIVED);
                        msgList.add(msg_no);
                        //刷新
                        adapter.notifyItemInserted(msgList.size() - 1);
                        msgRecyclerView.scrollToPosition(msgList.size() - 1);
                    }
                });
            }
        });
    }
    //根据食材列表进行构建问句查询
    private void doRe2Question(List<String> name){
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
                //得到了关于菜名列表的回答
                List<String> text= huaweiCloudUtil.parse4Query12(string);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //T在这里对ui内识别到的菜名列表text进行得处理，例如输出:推荐的菜品有xxxxx
                        //在这里对ui内识别到的食材列表material_name进行得处理，例如输出:推荐菜：xxxxx
                        Msg msg_no = new Msg("推荐菜:"+text.toString(),Msg.TYPE_RECEIVED);
                        msgList.add(msg_no);
                        //刷新
                        adapter.notifyItemInserted(msgList.size() - 1);
                        msgRecyclerView.scrollToPosition(msgList.size() - 1);
                    }
                });
            }
        });
    }






    //语音处理

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
    private void doRe2Question1(String question){
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
                        adapter.notifyItemInserted(msgList.size() - 1);
                        //将RecyclerView定位到最后一行
                        msgRecyclerView.scrollToPosition(msgList.size() - 1);
                        //跳转逻辑

                    }
                });
            }
        });
    }





}