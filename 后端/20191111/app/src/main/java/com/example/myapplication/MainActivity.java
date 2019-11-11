package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;

public class MainActivity extends Activity {
    // 服务器server/IP地址(当前PC的IP地址)
    private final String ADDRESS = "47.102.115.203";
    // 服务器端口
    private final int PORT = 2333;
    // 发送按钮
    private Button cButtonSend;
    private Button cButtonLogin;
    // 发送的内容
    private EditText cEditTextContent1;
    // 接收的内容
    private EditText cEditTextContent2;
    private EditText cEditTextContent3;
    private TextView cTextViewContent;
    // Socket用于连接服务器获取输入输出流
    private Socket cSocket;
    // 消息处理的线程
    private Thread cThread;
    private Thread cThreadLogin;
    // 消息的内容
    private String cContent;
    // 处理消息机制
    private boolean ifLogin=false;
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
                                dos = new DataOutputStream(cSocket.getOutputStream());
                                // 向服务器写数据
                                dos.writeUTF(cEditTextContent3.getText().toString());
                                cContent = "I Say:";
                                cContent += cEditTextContent3.getText().toString();
                                cContent += "\n";
                                cContent += "Server Say:\n";
                                // 读取服务器发来的数据
                                String string= dis.readUTF();
                                if(isJsonArray(string)==true){
                                    cContent+=toTable(string);
                                }
                                else {
                                    cContent+="数据格式不为json数组，无法转换";
                                }
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
                    cTextViewContent.setText(cContent);
                    break;
                case 0x03:
                    cThreadLogin =new Thread(){
                        @Override
                        public void run() {
                            DataInputStream dis = null;
                            DataOutputStream dos = null;
                            try {
                                cSocket = new Socket(ADDRESS, PORT);
                                dis = new DataInputStream(cSocket.getInputStream());
                                dos = new DataOutputStream(cSocket.getOutputStream());
                                String account=cEditTextContent1.getText().toString();
                                String password=cEditTextContent2.getText().toString();
                                String sql="select * from User where StudentID=\'"+account+"\' and "+"UserPassword=\'"+password+"\'";
                                dos.writeUTF(sql);
                                dos.flush();
                                String result=dis.readUTF();
                                JSONArray jsonArray=JSONArray.fromObject(result);
                                if(!jsonArray.isEmpty()){
                                    JSONObject jsonObject=jsonArray.getJSONObject(0);
                                    if(jsonObject.get("UserID")!=null) {
                                        cContent="登录成功,您的信息为\n";
                                        cContent+=toTable(result);
                                        cTextViewContent.setText(cContent);
                                        ifLogin=true;
                                    }
                                    else {
                                        cTextViewContent.setText(toTable(result)+cContent);
                                    }
                                }
                                else{
                                    cTextViewContent.setText("未找到用户信息");
                                }
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

                            }
                        }
                    };
                    cThreadLogin.start();
                default:
                    break;
            }
        }
    };
    public static boolean isJsonArray(String content) {
        if(content==null||content.isEmpty()) {
            return false;
        }
        try {
            JSONArray jsonArray =JSONArray.fromObject(content);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("数据格式不为json数组，无法转换");
            return false;
        }
    }
    public static String toTable(String string) {
        String table="";
        JSONArray jsonArray= JSONArray.fromObject(string);
        if(jsonArray.isEmpty()){
            return table;
        }
        Iterator iterator = jsonArray.getJSONObject(0).keys();
        while(iterator.hasNext()){
            table+= iterator.next()+"\t";
        }
        table+="\n";
        int num=jsonArray.size();
        for(int i=0;i<num;++i) {
            Iterator it = jsonArray.getJSONObject(i).keys();
            while(it.hasNext()){
                table+=jsonArray.getJSONObject(i).getString((String)it.next());
                table+="\t";
            }
            table+="\n";
        }
        return table;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        init();
    }
    /**
     * 初始化
     *
     * @time 注释时间：2013-12-23 下午4:41:33
     */
    private void init() {
        cButtonSend = (Button) findViewById(R.id.btn);
        cButtonLogin= (Button) findViewById(R.id.login);
        cEditTextContent1 = (EditText) findViewById(R.id.account);
        cEditTextContent2 = (EditText) findViewById(R.id.password);
        cEditTextContent3= (EditText) findViewById(R.id.op);
        cTextViewContent = (TextView) findViewById(R.id.tv);
        cButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ifLogin==true){
                    cHandler.sendEmptyMessage(0x01);
                }
                else {
                    cTextViewContent.setText("您还未登录！");
                }
            }
        });
        cButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cHandler.sendEmptyMessage(0x03);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, Menu.FIRST + 1, 1, "关于").setIcon(

                android.R.drawable.ic_menu_info_details);

        menu.add(Menu.NONE, Menu.FIRST + 2, 2, "帮助").setIcon(

                android.R.drawable.ic_menu_help);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Menu.FIRST + 1:
                new AlertDialog.Builder(this).setMessage("作者:蔡有飞\n\n版权归上海持创信息技术有限公司所有\n\n任何人不得修改本程序后宣传本作品 ").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialoginterface, int i) {
                                        // 按钮事件
                                    }
                                }).setIcon(android.R.drawable.ic_menu_info_details).setTitle("作者").show();
                break;

            case Menu.FIRST + 2:

                new AlertDialog.Builder(this).setMessage("使用过程中如有问题或建议\n请发邮件至caiyoufei@looip.cn").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialoginterface, int i) {
                                        // 按钮事件
                                    }
                                }).setTitle("帮助").setIcon(android.R.drawable.ic_menu_help).show();
                break;
        }
        return false;
    }

}
