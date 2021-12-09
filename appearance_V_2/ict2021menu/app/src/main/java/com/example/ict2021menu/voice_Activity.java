package com.example.ict2021menu;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class voice_Activity extends AppCompatActivity {


    private final List<Msg> msgList = home_Activity.msgList;//改为final
    private EditText inputText;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;


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

            }

            Msg question_result = new Msg(QA(),Msg.TYPE_RECEIVED);
            msgList.add(question_result);

        });
    }


    public String QA(){

        Msg msg_no = new Msg("对于您询问的问题:正在处理中!", Msg.TYPE_RECEIVED);
        msgList.add(msg_no);

        /*
        问答处理
         */

        String tip="没有找到您想要的答案,或许您可以换个说法再试试?";
        return tip;
    }

}