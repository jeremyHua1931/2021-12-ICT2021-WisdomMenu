package com.example.ict2021menu;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class voice_Activity extends AppCompatActivity {


    private final List<Msg> msgList = new ArrayList<>();//改为final
    private EditText inputText;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        //初始化消息
        initMegs();

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

            }
            QA();
        });
    }
    private void initMegs(){
        Msg msg1 = new Msg("你好呀,我是你的智慧菜谱小助手",Msg.TYPE_RECEIVED);
        msgList.add(msg1);
        Msg msg2 = new Msg("Hi",Msg.TYPE_SENT);
        msgList.add(msg2);
        Msg msg3 = new Msg("有什么需要吗?,home页直接点击语音就可以跟我对话哦",
                Msg.TYPE_RECEIVED);
        msgList.add(msg3);}


    public void QA(){

        /*
        问答处理
         */
        Msg msg_no = new Msg("我不知道你在说什么,能换个说法吗?",Msg.TYPE_RECEIVED);
        msgList.add(msg_no);
    }

}