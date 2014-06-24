package com.ilink;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import com.ilink.Param.GameGrade;
import android.R.drawable;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class Util
{
	private Context context;
	private AppData ad;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private List<Bitmap>bmL;
	public Util(Context context,AppData adp)
	{
		this.context = context;
		preferences = context.getSharedPreferences("ilinksettings",Context.MODE_PRIVATE);
		editor = preferences.edit();
	this.ad = adp;
		boolean existB = preferences.contains("appName");
		bmL = new ArrayList<Bitmap>();
		if (!existB )
		{
			editor.putString("appName","iLink");
			editor.putInt("bgIndex",0);
			editor.putInt("imageTeamIndex",0);
			editor.putBoolean("gameMusicState",true);
			editor.putBoolean("bgMusicState",true);
			editor.putString("humanIconStr",ad.imageStrList.get(0));
			editor.putString("animalIconStr",ad.imageStrList.get(1));
			editor.putString("plantIconStr",ad.imageStrList.get(2));
			editor.commit();
		}
	}
	
	public boolean readSettings()
	{
		ad.bgIndex = preferences.getInt("bgIndex",0);
		ad.imageTeamIndex = preferences.getInt("imageTeamIndex",0);
		ad.bgMusicState = preferences.getBoolean("bgMusicState",true);
		ad.gameMusicState = preferences.getBoolean("gameMusicState",true);
		ad.imageStrList.set(0,preferences.getString("humanIconStr","hs0+hs1+hs2+hs3+hs4+hs5+hs6+hs7+hs8+hs9+hs10+hs11+hs12+hs13"));
		ad.imageStrList.set(1,preferences.getString("animalIconStr","as0+as1+as2+as3+as4+as5+as6+as7+as8+as9+as10+as11+as12+as13"));
		ad.imageStrList.set(2,preferences.getString("plantIconStr","ps0+ps1+ps2+ps3+ps4+ps5+ps6+ps7+ps8+ps9+ps10+ps11+ps12+ps13"));

		return true;
	}
	
	public boolean writeSettings()
	{
		editor.putInt("bgIndex",ad.bgIndex);
		editor.putInt("imageTeamIndex",ad.imageTeamIndex);
		editor.putBoolean("bgMusicState",ad.bgMusicState);
		editor.putBoolean("gameMusicState",ad.gameMusicState);
		editor.putString("humanIconStr",ad.imageStrList.get(0));
		editor.putString("animalIconStr",ad.imageStrList.get(1));
		editor.putString("plantIconStr",ad.imageStrList.get(2));
		
		editor.commit();
		return true;
	}

	// 随机产生grade大小的一个整形数组,
	// 作为图片数组的下表
	public List<Integer> getIndexForGrade(int grade,int x,int y)
	{
		Random dom = new Random();
		// 从AppData.count范围中随机抽出grade个整形作为il
		//List<Integer>il = new ArrayList<Integer>();
		List<Integer>zl=new ArrayList<Integer>();
		for(int i=0;i<Param.imageCount;i++)
		{
			zl.add(Integer.valueOf(i));
		}
		Collections.shuffle(zl);
		while(zl.size() > grade)
		{
			zl.remove(zl.size()-1);
		}
		
		// 从grade范围中抽出x*y/2个数字作为il2，再添加一次il2给其自身，让piece形成偶数
		// 然后再洗牌
		List<Integer>il2 = new ArrayList<Integer>();
		
		for(int i=0,j=0;i<((x*y)/2);i++,j++)
		{
			if(j >= zl.size())
				j=0;
			il2.add(zl.get(j));
		}
		
		il2.addAll(il2);
		Collections.shuffle(il2);
		return il2;
	}
	
	public Bitmap getBitmap(int index)
	{
		return bmL.get(index);
	}
	
	public void getCurrentImages()
	{
		bmL =  getPlayImages(ad.imageStrList.get(ad.imageTeamIndex));
	}
	
	// 将图片组字符串转成 Bitmap List
	public List<Bitmap> getPlayImages(String teamStr)
	{
		List<Bitmap>bmL = new ArrayList<Bitmap>();
		boolean sdExistB = false;
		File sdFile = null;
		if ( Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) )
		{
			sdExistB = true;
		}
		//Class<com.ilink.R.drawable> draw = R.drawable.class;
		String[] strList = teamStr.split("\\+");
		for(String str:strList)
		{
			// if str is app's picture
			//Log.v("str",str);
			if ( str.subSequence(1,2).equals("s") )
			{
				int id = context.getResources().getIdentifier(str,"drawable",context.getPackageName());
				Bitmap bm = BitmapFactory.decodeResource(context.getResources(),id);
				bmL.add(bm);
			}
			// if str is user defined picture
			else if ( str.subSequence(1,2).equals("u"))
			{
				File appSdFile = new File("/mnt/sdcard/ilink/.pic/" + str + ".jpg");
				// if sd card does not exist or picture does not exist
				if ( (!sdExistB) || (!appSdFile.exists()) )
				{
					String str2 = str.replace("pu","ps");
					int id = context.getResources().getIdentifier(str2,"drawable",context.getPackageName());
					Bitmap bm = BitmapFactory.decodeResource(context.getResources(),id);
					bmL.add(bm);
				} else {
					Bitmap bm = BitmapFactory.decodeFile(appSdFile.getAbsolutePath());
					bmL.add(bm);
				}
			}
		}
		
		return bmL;
	}

	// 获取选中标识的图片
	public static Bitmap getSelectImage(Context context)
	{
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(),R.drawable.selected);
		return bm;
	}
}
