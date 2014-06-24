package com.ilink;


import android.graphics.Bitmap;
import android.graphics.Point;

public class Piece {
	private Bitmap image;
	private int imageId;
	
	private int beginX;
	private int beginY;
	private int indexX;
	private int indexY;

	
	public void setPiece(Piece p)
	{
		this.image = p.getImage();
		this.imageId = p.getImageId();
	}
	/*
	public void nullBM()
	{
		this.setImage(null);
	}
	
	public boolean bmIsNull()
	{
		return this.image == null;
	}
	*/
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
	
	public void setBeginX(int bx)
	{
		beginX = bx;
	}
	
	public int getBeginX()
	{
		return beginX;
	}
	
	public int getBeginY()
	{
		return beginY;
	}
	
	public void setBeginY(int by)
	{
		beginY = by;
	}
	
	public int getIndexX()
	{
		return indexX;
	}
	
	public void setIndexX(int ix)
	{
		indexX = ix;
	}
	
	public int getIndexY()
	{
		return indexY;
	}
	
	public void setIndexY(int iy)
	{
		indexY = iy;
	}
	
	public Point getCenter(int x,int y)
	{
		return new Point(x / 2
				+ getBeginX(), getBeginY()
				+ y/ 2);
	}
	
	public boolean isSameImage(Piece other)
	{
		if (image == null)
		{
			if (other.image != null)
				return false;
		}
		// 只要Piece封装图片ID相同，即可认为两个Piece相等。
		return getImageId() == other.getImageId();
	}

	
	
}
