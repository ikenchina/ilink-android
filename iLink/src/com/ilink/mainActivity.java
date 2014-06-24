package com.ilink;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class mainActivity extends Activity implements OnClickListener{

	private Button startButton;
	private Button optionButton;
	private Button helpButton;
	private Button exitButton;
	private AppData ad;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		
		startButton = (Button)findViewById(R.id.startButton);
		optionButton = (Button)findViewById(R.id.optionButton);
		helpButton = (Button)findViewById(R.id.helpButton);
		exitButton = (Button)findViewById(R.id.exitButton);
		
		startButton.setOnClickListener(this);
		optionButton.setOnClickListener(this);
		helpButton.setOnClickListener(this);
		exitButton.setOnClickListener(this);
		
		// mainifest.xml add 
		ad = (AppData)this.getApplicationContext();
		Util util = new Util(this,ad);
		util.readSettings();


	}

	
	@Override
	public void onClick(View v) {
		if ( v.getId() == R.id.startButton )
		{
			Intent intent = new Intent(mainActivity.this,ILinkActivity.class);
			startActivity(intent);
		} else if ( v.getId() == R.id.optionButton )
		{
			Intent intent = new Intent(mainActivity.this,optionActivity.class);
			startActivity(intent);
		} else if ( v.getId() == R.id.helpButton )
		{
			Intent intent = new Intent(mainActivity.this,helpActivity.class);
			startActivity(intent);
		} else if ( v.getId() == R.id.exitButton )
		{
			finish();
		}
		overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
		//overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
	}
	

	
}
