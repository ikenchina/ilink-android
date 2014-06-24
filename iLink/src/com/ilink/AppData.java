package com.ilink;

import java.util.ArrayList;
import java.util.List;
import android.app.Application;
import android.graphics.Bitmap;

public class AppData extends Application {
	// background bitmap
	public Bitmap bgBM;
	// the class of icons
	public int imageTeamIndex;
	// the state of background music
	public boolean bgMusicState;
	// game music
	public boolean gameMusicState;
	public List<Integer>iconIndex;
	public int bgIndex;
	
	public int beginCoorX;
	public int beginCoorY;
	public int imageWidth=64;
	public int imageHeight=64;
	public int totalTime;

	public List<String>imageStrList=null;
	
	public int countX = 14;
	public int countY = 14;
	public int cImageCount = 0;

	public AppData() 
	{
		imageStrList = new ArrayList<String>();
		imageStrList.add("hs0+hs1+hs2+hs3+hs4+hs5+hs6+hs7+hs8+hs9+hs10+hs11+hs12+hs13");
		imageStrList.add("as0+as1+as2+as3+as4+as5+as6+as7+as8+as9+as10+as11+as12+as13");
		imageStrList.add("ps0+ps1+ps2+ps3+ps4+ps5+ps6+ps7+ps8+ps9+ps10+ps11+ps12+ps13");
	}

}
