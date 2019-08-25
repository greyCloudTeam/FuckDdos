package com.greyCloud.ddos;

import android.content.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.*;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.*;
import android.view.*;

import android.app.AlertDialog;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity
{

	//MyHandler mHandler = new MyHandler();

	//tab选项卡
	private ViewPager viewPager;
	private TabLayout tabLayout;
	private ContentPagerAdapter contentAdapter;

	MainActivity t=null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		//设置tab选项卡
		//pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this);
		viewPager = (ViewPager) findViewById(R.id.pager);
		tabLayout = (TabLayout) findViewById(R.id.tab);
		contentAdapter = new ContentPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(contentAdapter);
		tabLayout.setupWithViewPager(viewPager);

		/*
		pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(),this);
		viewPager.setAdapter(pagerAdapter);
		tabLayout.setupWithViewPager(viewPager);
		*/
		ConnectivityManager con = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = con.getActiveNetworkInfo();
		if(activeNetworkInfo.getType()==ConnectivityManager.TYPE_MOBILE){
			AlertDialog dialog1=new AlertDialog.Builder(this)
					.setTitle("警告")//设置对话框的标题
					.setMessage("您现在正在使用移动数据，不建议使用本软件。如果使用将会产生高额的流量费用")
					//设置对话框的按钮
					.setNegativeButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).create();
			dialog1.show();
		}
		//t=this;
    }

	class ContentPagerAdapter extends FragmentPagerAdapter {
    	public String[] arr={"欢迎","ddos"};
    	public Fragment[] arr2={new fragment_welcome(),new PageFragment()};
		public ContentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return arr2[position];
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return arr[position];
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate main_menu.xml 
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.about:
				AlertDialog dialog = new AlertDialog.Builder(this)
					.setTitle("关于")//设置对话框的标题
					.setMessage("作者:菜问先生\nQQ:2970046657\n出品:灰色云团队\ngithub:https://github.com/greyCloudTeam/FuckDdos\nEmail:753707290@qq.com(所反馈的bug和建议将在24小时内回复)\n特别鸣谢:2514228574")//设置对话框的内容
					//设置对话框的按钮
					.setPositiveButton("复制github地址", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
							ClipData mClipData = ClipData.newPlainText("Label", "https://github.com/greyCloudTeam/FuckDdos");
							cm.setPrimaryClip(mClipData);
							Toast.makeText(MainActivity.this,"已复制",Toast.LENGTH_SHORT).show();
						}
					}).setNeutralButton("关闭", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							dialogInterface.dismiss();
						}
					}).setNegativeButton("复制Email地址", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
							ClipData mClipData = ClipData.newPlainText("Label", "753707290@qq.com");
							cm.setPrimaryClip(mClipData);
							Toast.makeText(MainActivity.this,"已复制",Toast.LENGTH_SHORT).show();
						}
					}).create();

				dialog.show();
				//AlertDialog dialog=new AlertDialog.Builder(this).create();
				//dialog.setTitle("关于");
				//dialog.setMessage("作者:菜问先生\nQQ:2970046657\n出品:灰色云团队\ngithub:https://github.com/greyCloudTeam/FuckDdos");
				//dialog.show();
				return true;
			case R.id.updata:
				AlertDialog dialog1=new AlertDialog.Builder(this)
					.setTitle("关于")//设置对话框的标题
					.setMessage("1.5更新内容:\n"+
							"1.前面几个版本都是作者拿手机的aide编写的（电脑运行不起来），这次向朋友借来了电脑。项目的配置文件经过修改已经能顺利在当前的androidStudio上编译，并且代码进行优化，加强了可读性，添加了注释\n"+
							"2.上个版本所修复的存活线程数量的统计问题并没有真正修复，经过作者对java进一步学习，此版本已经修复\n"+
							"3.ui重写，将主题色改为绿油油的原谅色\n"+
							"4.修复了释放线程和创建缓存以及回收线程时进度条显示不出来的问题\n"+
							"5.代码重写，代码要比前几个版本更加简洁\n"+
							"6.新增了使用移动数据的提醒，关于处增加了复制github地址和复制email地址\n\n"+
							"1.4_fix1修复内容:\n" +
							"修复了点击开始攻击后出现警告弹窗，然后再次点击开始攻击显示正在攻击，然后再点击停止攻击直接闪退\n" +
							"改了下代码，应该能缓解存活线程统计有误差\n" +
							"修复了停止攻击后再点击开始攻击，存活线程不动的bug\n" +
							"优化了部分ui\n\n"+
							"1.4更新内容:\n" +
							"修复了所有bug\n" +
							"直接强制停止所有线程，方便快捷\n" +
							"代码优化，更省资源\n" +
							"总死亡线程数量不再统计，直接统计每秒死多少线程\n" +
							"点击开始攻击时不再卡死"
							)
					//设置对话框的按钮
					.setNegativeButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).create();
				dialog1.show();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

