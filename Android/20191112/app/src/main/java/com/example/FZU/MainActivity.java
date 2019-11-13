package com.example.FZU;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private Button button1;//登录按钮
    private TextView textView1,textView2;//“忘记密码”“注册”，点击可跳转到相关页面
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        init();

        textView1=findViewById(R.id.text_zhuce);
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {      //绑定监听点击事件，点击后跳转注册页面
                Intent intent=new Intent();
                intent.setClass(com.example.FZU.MainActivity.this,register.class);
                startActivity(intent);
            }
        });
        textView2=findViewById(R.id.text_wanglimima);
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {     //绑定监听点击事件，点击后跳转忘记密码页面
                Intent intent=new Intent();
                intent.setClass(com.example.FZU.MainActivity.this,forgetpassword.class);
                startActivity(intent);
            }
        });
    }

    // 发送的内容
    private EditText cEdit_zhanghao;
    private EditText cEdit_mima;
    // 接收的内容
    private TextView cTextViewContent1;
    // Socket用于连接服务器获取输入输出流
    private Socket cSocket;
    // 服务器server/IP地址(当前PC的IP地址)
    private final String ADDRESS = "47.102.115.203";
    // 服务器端口
    private final int PORT = 2333;
    // 消息处理的线程
    private Thread cThread;
    // 消息的内容
    private String cContent=null;
    // 处理消息机制
    private  String account;//接收输入的账号
    private String password;//接收输入的密码
    private String sql;//将账号密码转换为sql语句
    public Handler cHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x01:
                    cThread = new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            DataInputStream dis = null;
                            DataOutputStream dos = null;
                            try {
                                // 阻塞函数，正常连接后才会向下继续执行
                                cSocket = new Socket(ADDRESS, PORT);
                                dis = new DataInputStream(cSocket.getInputStream());
                                dos = new DataOutputStream(
                                        cSocket.getOutputStream());
                                // 向服务器写数据
                                dos.writeUTF(sql);
                                // 读取服务器发来的数据
                                cContent = dis.readUTF();
                                //JSONArray jsonArray=JSONArray.fromObject(cContent);
                                if(cContent.equals("[{\"StudentID\":\""+account+"\",\"UserPassword\":\""+password+"\"}]"))
                                {
                                    cContent="\0";
                                    Intent intent=new Intent();
                                    intent.setClass(com.example.FZU.MainActivity.this,fragment.class);
                                    startActivity(intent);
                                }
                                else
                                    cContent+="您输入的账号密码不正确";
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    if (dis != null) {
                                        dis.close();
                                    }
                                    if (dos != null) {
                                        dos.close();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                cHandler.sendEmptyMessage(0x02);
                            }
                        }

                    };
                    cThread.start();
                    break;
                case 0x02:
                    cTextViewContent1.setText(cContent);
                    break;

                default:
                    break;
            }
        }

    };
    private void init() {
       button1 = findViewById(R.id.button_denglu);
        cEdit_zhanghao =findViewById(R.id.edit_xuehao);
        cEdit_mima=findViewById(R.id.edit_mima);
        cTextViewContent1 = findViewById(R.id.text_zhanghaoxinxi);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account=cEdit_zhanghao.getText().toString();
                password=cEdit_mima.getText().toString();
                sql="select StudentID,UserPassword from User where StudentID=\'"+account+"\' and "+"UserPassword=\'"+password+"\'";
                cHandler.sendEmptyMessage(0x01);

            }
        });
    }
}
