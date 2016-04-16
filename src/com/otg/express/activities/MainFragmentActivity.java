package com.otg.express.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.*;
import com.otg.express.R;
import com.otg.express.fragments.MeFragment;
import com.otg.express.fragments.OcrFragment;
import com.otg.express.fragments.OtgFragment;

import java.lang.reflect.Method;

public class MainFragmentActivity extends FragmentActivity implements OnClickListener
{
	private LinearLayout mTabOtg;
	private LinearLayout mTabOcr;
	private LinearLayout mTabMe;

	private ImageButton mImgOtg;
	private ImageButton mImgOcr;
	private ImageButton mImgMe;

	private Fragment mFragmentOtg;
	private Fragment mFragmentOcr;
	private Fragment mFragmentMe;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_fragment_main);
		initView();
		initEvent();
		setSelect(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_menu_admin:
				startActivity(new Intent(MainFragmentActivity.this, SetAdminActivity.class));
//				finish();
				return true;
			case R.id.action_menu_otg:
				startActivity(new Intent(MainFragmentActivity.this, SetOtgActivity.class));
//				finish();
				return true;
		}
		return false;
	}



	/**
	 * 设置menu显示icon
	 */
	@Override
	public boolean onMenuOpened(int featureId, Menu menu)
	{

		if (featureId == Window.FEATURE_ACTION_BAR && menu != null)
		{
			if (menu.getClass().getSimpleName().equals("MenuBuilder"))
			{
				try
				{
					Method m = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		return super.onMenuOpened(featureId, menu);
	}

	private void initEvent()
	{
		mTabOtg.setOnClickListener(this);
		mTabOcr.setOnClickListener(this);
		mTabMe.setOnClickListener(this);
	}

	private void initView()
	{
		mTabOtg = (LinearLayout) findViewById(R.id.id_tab_weixin);
		mTabOcr = (LinearLayout) findViewById(R.id.id_tab_frd);
		mTabMe = (LinearLayout) findViewById(R.id.id_tab_address);

		mImgOtg = (ImageButton) findViewById(R.id.id_tab_weixin_img);
		mImgOcr = (ImageButton) findViewById(R.id.id_tab_frd_img);
		mImgMe = (ImageButton) findViewById(R.id.id_tab_address_img);
	}




	private void setSelect(int i){
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		hideFragment(transaction);
		// 把图片设置为亮的
		// 设置内容区域
		switch (i)
		{
			case 0:
				if (mFragmentOtg == null)
				{
					mFragmentOtg = new OtgFragment();
					transaction.add(R.id.id_content, mFragmentOtg);
				} else
				{
					transaction.show(mFragmentOtg);
				}
				mImgOtg.setImageResource(R.drawable.tab_settings_pressed);

				break;
			case 1:
				if (mFragmentOcr == null)
				{
					mFragmentOcr = new OcrFragment();
					transaction.add(R.id.id_content, mFragmentOcr);

				} else
				{
					transaction.show(mFragmentOcr);

				}
				mImgOcr.setImageResource(R.drawable.tab_settings_pressed);
				break;
			case 2:
				if (mFragmentMe == null)
				{
					mFragmentMe = new MeFragment();
					transaction.add(R.id.id_content, mFragmentMe);
				} else
				{
					transaction.show(mFragmentMe);
				}
				mImgMe.setImageResource(R.drawable.tab_settings_pressed);
				break;
			default:
				break;
		}

		transaction.commit();
	}

	private void hideFragment(FragmentTransaction transaction)
	{
		if (mFragmentOtg != null)
		{
			transaction.hide(mFragmentOtg);
		}
		if (mFragmentOcr != null)
		{
			transaction.hide(mFragmentOcr);
		}
		if (mFragmentMe != null)
		{
			transaction.hide(mFragmentMe);
		}
	}

	@Override
	public void onClick(View v)
	{
		resetImgs();
		switch (v.getId())
		{
			case R.id.id_tab_weixin:
				setSelect(0);
				break;
			case R.id.id_tab_frd:
				setSelect(1);
				break;
			case R.id.id_tab_address:
				setSelect(2);
				break;
			default:
				break;
		}
	}

	/**
	 * 切换图片至暗色
	 */
	private void resetImgs()
	{
		mImgOtg.setImageResource(R.drawable.tab_settings_normal);
		mImgOcr.setImageResource(R.drawable.tab_settings_normal);
		mImgMe.setImageResource(R.drawable.tab_settings_normal);
	}

}
