package com.example.ict2021menu;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ict2021menu.utils.bafaCloudUtil;
import com.example.ict2021menu.utils.fileUtil;
import com.example.ict2021menu.utils.huaweiCloudUtil;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class voice_Activity extends AppCompatActivity {


    private final List<Msg> msgList = home_Activity.msgList;//改为final
    private EditText inputText;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        //初始化消息
        //initMegs();

        //找到输入文本和发送按钮控件
        inputText = (EditText)findViewById(R.id.input_text);
        Button send = (Button) findViewById(R.id.send);


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

            };
            doRe2Question(content);
            doRe2Bafa();
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
                //返回ui线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //ui线程操作主要在这里，TODO:在这里更改返回字符串后对ui的处理
                        //toastUtil.showMsg(getApplicationContext(),string);
                        Msg msg_no = new Msg(string,Msg.TYPE_RECEIVED);
                        msgList.add(msg_no);
                        //刷新
                        adapter.notifyItemInserted(msgList.size() - 1);
                        msgRecyclerView.scrollToPosition(msgList.size() - 1);
                    }
                });
            }
        });
    }



    //巴法云请求图片url
    private void doRe2Bafa(){
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
                getImg4Bafa(url);
            }
        });
    }


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

            }//发送失败回调函数

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String string = response.body().string();
                String base64= fileUtil.data2Base64(string);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TODO:在这里对base64进行得处理
                    }
                });
            }
        });
    }

}