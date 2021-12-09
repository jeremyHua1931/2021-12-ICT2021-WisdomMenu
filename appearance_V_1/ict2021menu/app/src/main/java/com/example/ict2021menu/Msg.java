package com.example.ict2021menu;


//新建消息类 用content表示消息的内容，type表示消息的类型，消息类型有两个值，0代表收到的信息，1代表发送的信息
public class Msg {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;
    private final String content;//改为final
    private final int type;//改为final

    public Msg(String content,int type){
        this.content = content;
        this.type = type;
    }

    public String getContent(){
        return content;
    }
    public int getType(){
        return type;
    }

}
