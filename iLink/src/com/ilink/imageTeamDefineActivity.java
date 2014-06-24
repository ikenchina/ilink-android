package com.ilink;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class imageTeamDefineActivity extends Activity {
	private Button applyButton;
	private ListView imageListView;
	private List<Bitmap>bmL;
	private ImageTeamAdapter myAdapter;
	private String currentStr;
	private String currentStr2;
	private boolean changed;
	//private String[] strList;
	private int position=0;
	private Util util;
	private AppData ad;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setTitle(this.getResources().getString(R.string.imageTeamDefineTitle));
		setContentView(R.layout.userdefineimageteam_layout);
		
		
		changed = false;
		ad = (AppData)getApplication();
		util = new Util(this,ad);
		currentStr = this.getIntent().getExtras().getString("imageTeamSpinner");
		currentStr2 = currentStr;
		bmL = util.getPlayImages(currentStr);
		myAdapter = new ImageTeamAdapter(this,bmL);
		
		applyButton = (Button)findViewById(R.id.applyButton);
		imageListView = (ListView)findViewById(R.id.imageListView);
		imageListView.setAdapter(myAdapter);
		
		imageTeamDefineActivity.this.setResult(1,getIntent());
		
		applyButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) {
				andFinish(currentStr2);
			}
		});
		
		imageListView.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) 
			{
				position = arg2;
				AlertDialog.Builder dialog = new AlertDialog.Builder(imageTeamDefineActivity.this);
				dialog.setPositiveButton("自定义",new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								
								Intent intent = new Intent();
								intent.setAction(Intent.ACTION_GET_CONTENT);
								intent.setType("image/*");
								//startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);
								startActivityForResult(intent,1);
							}
						});
				dialog.setNegativeButton("还原程序自带图标",new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String str3 = "(.*)([a-z])([us])(" + position + ")(\\D)(.*)";
						currentStr2 = currentStr2.replaceFirst(str3,"$1$2s$4$5$6");
						changed = true;
						bmL = util.getPlayImages(currentStr2);
						myAdapter.setBML(bmL);
						myAdapter.notifyDataSetChanged();
					}
				}
				);
				dialog.show();

				return false;
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if ( requestCode == 1 && resultCode == Activity.RESULT_OK )
		{
			Uri d = data.getData();
			try {
				InputStream is = getContentResolver().openInputStream(d);
				Bitmap bm = BitmapFactory.decodeStream(is);
				Matrix matrix = new Matrix();
				int x = bm.getWidth();
				int y = bm.getHeight();
				float sx = (float)Param.fixWidth/x;
				float sy = (float)Param.fixHeight/y;
				matrix.postScale(sx,sy);
				Bitmap bm2 = Bitmap.createBitmap(bm,0,0,x,y,matrix,true);
				
				String str3 = "(.*)([a-z])([us])(" + position + ")(\\D)(.*)";
				currentStr2 = currentStr2.replaceFirst(str3,"$1$2u$4$5$6");
				String newStr2 = currentStr2.replaceFirst(str3,"$2u$4.jpg");
				
				FileOutputStream os = new FileOutputStream("/mnt/sdcard/ilink/.pic/"+newStr2);

				boolean bb =bm2.compress(CompressFormat.JPEG,40,os);
				if ( !bb )
				{
					Toast.makeText(imageTeamDefineActivity.this,"error",Toast.LENGTH_SHORT);
					andFinish(currentStr);
				}
			} catch (Exception e) {
				Toast.makeText(imageTeamDefineActivity.this,"error",Toast.LENGTH_SHORT);
				e.printStackTrace();
				andFinish(currentStr);
			}
			changed = true;
			bmL = util.getPlayImages(currentStr2);
			myAdapter.setBML(bmL);
			myAdapter.notifyDataSetChanged();
		}
	}
	
	private void andFinish(String str)
	{
		Intent intent = this.getIntent();
		Bundle data = new Bundle();
		data.putString("imageTeamSpinner",str);
		data.putBoolean("changed",changed);
		intent.putExtras(data);
		imageTeamDefineActivity.this.setResult(0,intent);
		imageTeamDefineActivity.this.finish();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}

}

class ImageTeamAdapter extends BaseAdapter
{
	private List<Bitmap>bmL;
	private Context context;
	public ImageTeamAdapter(Context context,List<Bitmap>bmL)
	{
		this.bmL = bmL;
		this.context = context;
	}
	public void setBML(List<Bitmap>bb)
	{
		this.bmL = bb;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return bmL.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ImageView iv = new ImageView(context);
		iv.setImageBitmap(bmL.get(arg0));

		return iv;
	}
	
}

