package com.ilink;

import java.io.File;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class optionActivity extends Activity {
	private ToggleButton gameMusicToggle;
	private ToggleButton backMusicToggle;
	private Spinner bgSpinner;
	private Spinner imageTeamSpinner;
	private Button imageTeamDefineButton;
	private Button applyButton;
	private AppData ad;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_layout);
        
        ad = (AppData) getApplication();
        backMusicToggle = (ToggleButton)findViewById(R.id.backMusicToggle);
        gameMusicToggle = (ToggleButton)findViewById(R.id.gameMusicToggle);
        bgSpinner = (Spinner)findViewById(R.id.bgSpinner);
        imageTeamSpinner = (Spinner)findViewById(R.id.imageTeamSpinner);
        applyButton = (Button)findViewById(R.id.applyButton);
        
        backMusicToggle.setChecked(ad.bgMusicState);
        gameMusicToggle.setChecked(ad.gameMusicState);
        bgSpinner.setSelection(ad.bgIndex);
        imageTeamSpinner.setSelection(ad.imageTeamIndex);
        imageTeamDefineButton = (Button)findViewById(R.id.imageTeamDefineButton);
        // 背景音乐开关监听
        backMusicToggle.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if ( arg1 )
				{
					ad.bgMusicState = true;
				} else {
					ad.bgMusicState = false;
				}
			}
        });
        // 游戏音乐开关监听
        gameMusicToggle.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				if ( isChecked )
				{
					ad.gameMusicState = true;
				} else {
					ad.gameMusicState = false;
				}
			}
        });
        // 背景图片样式spinner监听
        bgSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
        {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				ad.bgIndex = arg2;
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
        });
        // 
        imageTeamSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
        {
        	@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				ad.imageTeamIndex = arg2;
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
        });
        // 自定义image team,若外部存储器不存在则不弹出自定义activity
        imageTeamDefineButton.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View arg0) {
				boolean pathExist = false;
				if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
					String picStr = Environment.getExternalStorageDirectory().getPath() + "/ilink/pic/";
					File path = new File(picStr);
					if ( ! path.exists() )
					{
						try
						{
							boolean bb = path.mkdirs();
							if ( bb )
								pathExist = true;
						}catch(Exception e)
						{
						}
					} else {
						pathExist = true;
					}
				}
				if ( pathExist )
				{
					Intent intent = new Intent(optionActivity.this,imageTeamDefineActivity.class);
					intent.putExtra("imageTeamSpinner",ad.imageStrList.get(ad.imageTeamIndex));
					startActivityForResult(intent,0);
				} else {
					Toast.makeText(optionActivity.this,"picStr does not exist or can not been create!", Toast.LENGTH_LONG);
				}
			}
        });
        
        applyButton.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
        });
    }
	
	// image修改activity的修改内容进行保存AppData
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		//Log.v("resultCodedddddddd:",""+resultCode);
		
		//super.onActivityResult(requestCode, resultCode, data);

		if ( requestCode == 0 && resultCode == 0 )
		{
			Bundle b = data.getExtras();
			String str = b.getString("imageTeamSpinner");
			ad.imageStrList.set(ad.imageTeamIndex,str);
			// 是否有进行更改
			boolean changed = b.getBoolean("changed");
			/*
			if(changed)
			{
				Util util = new Util(this,ad);
				util.writeSettings();
			}
			*/
		}
	}

	// 退出时保存 AppData
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		Util util = new Util(this,ad);
		util.writeSettings();
		super.finish();
		overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
	}
}
