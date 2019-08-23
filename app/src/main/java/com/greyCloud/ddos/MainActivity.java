package com.greyCloud.ddos;

import android.content.*;
import android.os.*;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.*;
import android.view.*;

import android.app.AlertDialog;
public class MainActivity extends ActionBarActivity
{

	//MyHandler mHandler = new MyHandler();

	//tab选项卡
	private SimpleFragmentPagerAdapter pagerAdapter;
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
					.setMessage("作者:菜问先生\nQQ:2970046657\n出品:灰色云团队\ngithub:https://github.com/greyCloudTeam/FuckDdos\n灰色云商店:http://app.hsyun.ml/#fuckDdos")//设置对话框的内容
					//设置对话框的按钮
					.setNegativeButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
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
					.setMessage("1.4更新内容:\n修复了所有bug\n直接强制停止所有线程，方便快捷\n代码优化，更省资源\n总死亡线程数量不再统计，直接统计每秒死多少线程\n点击开始攻击时不再卡死\n\n1.4_fix1修复内容:\n修复了点击开始攻击后出现警告弹窗，然后再次点击开始攻击显示正在攻击，然后再点击停止攻击直接闪退\n改了下代码，应该能缓解存活线程统计有误差\n修复了停止攻击后再点击开始攻击，存活线程不动的bug\n优化了部分ui")
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

