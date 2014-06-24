package com.ilink;

import android.graphics.Bitmap;

public class PieceImage {
	private Bitmap image;
	private int imageId;
	public Bitmap getImage()
	{
		return image;
	}
	public void setImage(Bitmap bm)
	{
		this.image = bm;
	}
	public int getImageId()
	{
		return imageId;
	}
	public void setImageId(int id)
	{
		imageId = id;
	}
}
