package com.example.FZU;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

public class Fragment_shouye extends Fragment {

    private FragmentShouyeViewModel mViewModel;

    private List<Animal> mData = null;
    private Context mContext;
    private AnimalAdapter mAdapter = null;
    private ListView list_animal;
    public Object lock =new Object();
    public JSONObject jsonObject=new JSONObject();
    public Socket socket=null;
    public String json = null;
    public class getMessage implements Runnable{
        @Override
        public void run() {
            try {
                jsonObject.put("request","@getMasterpage");//命令名称
                jsonObject.put("parameter",2);//参数个数
                //jsonObject.put(0,"%宿舍%");
                jsonObject.put(0,0);
                jsonObject.put(1,10);
                System.out.println(jsonObject.size());
                String sql=jsonObject.toString();
                socket= new Socket("47.102.115.203", 2333);
                System.out.println("!");
                /**向ip=url的地址发送一个socket申请请求*/
                DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

                /**新建一个建立在socket上的输入流*/
                System.out.println("发送的数据为：" + sql);
                outputStream.writeUTF(sql);
                /**将sql字符串载入发送缓存*/
                outputStream.flush();
                /**发送缓存内容*/
                DataInputStream inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                /**新建一个建立在socket上的输入流*/
                json = inputStream.readUTF();

                JSONArray jsonArray=JSONArray.fromObject(json);
                /**读取数据*/
                System.out.println("字符串形式：\n" + json);
                synchronized (lock) {//获取对象锁
                    lock.notify();//子线程唤醒
                }
            }catch (IOException e) {
                e.printStackTrace();
                throw new SecurityException("!!!");
            }

        }
    }

    public static Fragment_shouye newInstance() {
        return new Fragment_shouye();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_shouye_fragment, container, false);

        new Thread(new getMessage()).start();
        try{
            synchronized (lock) {//这里也是一样
                lock.wait();//主线程等待
            }
        }catch (InterruptedException e){

        }


        JSONArray jsonArray=JSONArray.fromObject(json);


        String []title=new String[jsonArray.size()];
        String []tag=new String[jsonArray.size()];
        String []Beijing=new String[jsonArray.size()];
        String []Touxiang=new String[jsonArray.size()];

        for(int i=0;i<jsonArray.size();++i){
            title[i]=jsonArray.getJSONObject(i).getString("PostContent");
            tag[i]=jsonArray.getJSONObject(i).getString("TagTruth");
            Touxiang[i]=jsonArray.getJSONObject(i).getString("UserPictureURL");
            Beijing[i]=jsonArray.getJSONObject(i).getString("PicturesURL");

        }

        mContext = getActivity();

        list_animal = (ListView) view.findViewById(R.id.listview);
//        String img = "https://www.baidu.com/img/bd_logo1.png";


        mData = new LinkedList<Animal>();
        for(int i=0;i<title.length;i++){



            mData.add(new Animal(tag[i],title[i],Touxiang[i],Beijing[i]));

        }

        //关联适配器
        mAdapter = new AnimalAdapter((LinkedList<Animal>) mData, mContext);
        list_animal.setAdapter(mAdapter);


    return view;

    }







    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FragmentShouyeViewModel.class);
        // TODO: Use the ViewModel
        
    }

}
