package com.ilink;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ilink.Param.ArrangeEnum;
import com.ilink.Param.GameGrade;
import com.ilink.Param.GradeEnum;
import com.ilink.Param.OrienEnum;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class Board {
	//private int countX = 0;
	//private int countY = 0;
	private Context context;
	private AppData ad;
	public Board(Context context,AppData ad)
	{
		this.context = context;
		this.ad = ad;
	}
	
	public Piece[][] init(GradeEnum ae,ArrangeEnum mode)
	{
		Piece[][] pieces = getAllPieces(ad.cImageCount,ad.countX,ad.countY);
		arrayPieceForMode(pieces,mode,ad.countX,ad.countY);
		return pieces;
	}
	
	private void setPieceInfo2(Piece p,int x,int y)
	{
		p.setIndexX(x);
		p.setIndexY(y);
		p.setBeginX(ad.beginCoorX+ x*ad.imageWidth);
		p.setBeginY(ad.beginCoorY + y*ad.imageHeight);
	}
	
	// 更新pieces,按照方向重力
	public void downUpdate(Piece[][] pieces,OrienEnum mode)
	{
		int lenx=pieces.length;
		int leny=pieces[0].length;
		// 重力向上的情况
		if ( mode == OrienEnum.T )
		{
			for(int x=0;x<lenx;x++ )
			{
				for(int y=0;y<(leny-1);y++)
				{
					if (pieces[x][y] == null)
					{
						for(int z=y+1;z<=(leny-1);z++)
						{
							if(pieces[x][z] != null)
							{
								pieces[x][y] = pieces[x][z];
								this.setPieceInfo2(pieces[x][y], x, y);
								pieces[x][z] = null;
								y++;
							}
						}
						break;
					}
				}
			}
		}
		// 重力向下的情况下
		else if ( mode == OrienEnum.B )
		{
			for(int x=0;x<lenx;x++)
			{
				for(int y=(leny-1);y>0;y--)
				{
					if(pieces[x][y] == null)
					{
						for(int z=y-1;z>=0;z--)
						{
							if(pieces[x][z] != null)
							{
								pieces[x][y] = pieces[x][z];
								this.setPieceInfo2(pieces[x][y], x, y);
								pieces[x][z] = null;
								y--;
							}
						}
						break;
					}
				}
			}
		}
		//  重力向左的情况下
		else if ( mode == OrienEnum.L )
		{
			for(int y=0;y>leny;y++)
			{
				for(int x=0;x>(lenx-1);x++)
				{
					if(pieces[x][y] == null)
					{
						for(int z=x+1;z>=(lenx-1);z++)
						{
							if(pieces[z][y] != null)
							{
								pieces[x][y] = pieces[z][y];
								this.setPieceInfo2(pieces[x][y], x, y);
								pieces[z][y]=null;
								x++;
							}
						}
						break;
					}
				}
			}
		}
		// 重力向右的情况下
		else if ( mode == OrienEnum.R )
		{
			for(int y=0;y>leny;y++)
			{
				for(int x=lenx-1;x>0;x--)
				{
					if(pieces[x][y] == null)
					{
						for(int z=x-1;z>=0;z--)
						{
							if(pieces[z][y] != null)
							{
								pieces[x][y] = pieces[z][y];
								this.setPieceInfo2(pieces[x][y], x, y);
								pieces[z][y] = null;
								x--;
							}
						}
					}
				}
			}
		}
	}
	// 按照某个方向进行重新洗牌，打乱剩余piece排列的顺序
	public Piece[][] shuffle(Piece[][] pieces,OrienEnum mode)
	{
		List<Integer>il = new ArrayList<Integer>();
		int lenx=pieces.length;
		int leny=pieces[0].length;
		Piece[][] ps = new Piece[lenx][leny];
		
		// 取得所有pieces的坐标总值,
		for(int i=0;i<lenx;i++)
		{
			for(int j=0;j<leny;j++)
			{
				il.add(i * leny  + j );
			}
		}
		Collections.shuffle(il);
		int x=0,y=0;
		if(mode == OrienEnum.N)
		{
			int z=0;
			for(int i=0;i<lenx;i++)
			{
				for(int j=0;j<leny;j++)
				{
					int l=il.get(z).intValue();
					x=l/leny;y=l%leny;
					ps[i][j] = pieces[x][y];
					pieces[x][y]=null;
					if(ps[i][j] != null)
						this.setPieceInfo2(ps[i][j],i,j);
					z++;
				}
			}
		}
		else if ( mode == OrienEnum.T || mode == OrienEnum.B )
		{
			int z=0;
			int i=0,j=0;
			int i2=0,j2=0;
			int ilSize=il.size();
			while(true)
			{
				if(z >= ilSize)
					break;
				int l=il.get(z).intValue();
				x=l/leny;y=l%leny;
				//Log.v("lllll",""+x+ " "+y+" "+l);
				if(pieces[x][y] != null)
				{
					if(mode == OrienEnum.T)
					{
						ps[i][j] = pieces[x][y];pieces[x][y]=null;
						this.setPieceInfo2(ps[i][j],i,j);
					} else {
						ps[lenx-i-1][leny-j-1] = pieces[x][y];pieces[x][y]=null;
						this.setPieceInfo2(ps[lenx-i-1][leny-j-1],lenx-i-1,leny-j-1);
					}
					i++;
					if(i>=lenx)
					{
						j++;i=0;
					}
				} else {
					if(mode == OrienEnum.T)
					{
						ps[lenx-i2-1][leny-j2-1] = pieces[x][y];pieces[x][y]=null;
					}else {
						ps[i2][j2]=pieces[x][y];pieces[x][y]=null;
					}
					i2++;
					if(i2 >= lenx)
					{
						j2++;i2=0;
					}
				}
				z++;
			}
		} else if (mode == OrienEnum.L || mode == OrienEnum.R)
		{
			int z=0;
			int i=0,j=0;
			int i2=0,j2=0;
			int ilSize=il.size();
			while(true)
			{
				if(z >= ilSize)
					break;
				int l=il.get(z).intValue();
				x=l/leny;y=l%leny;
				if(pieces[x][y] != null)
				{
					if(mode == OrienEnum.L)
					{
						ps[i][j] = pieces[x][y];pieces[x][y]=null;
						this.setPieceInfo2(ps[i][j],i,j);
					} else {
						ps[lenx-i-1][leny-j-1] = pieces[x][y];pieces[x][y]=null;
						this.setPieceInfo2(ps[lenx-i-1][leny-j-1],lenx-i-1,leny-j-1);
					}
					j++;
					if(j>=leny)
					{
						i++;j=0;
					}
				} else {
					if(mode == OrienEnum.L)
					{
						ps[lenx-i2-1][leny-j2-1] = pieces[x][y];pieces[x][y]=null;
					}else {
						ps[i2][j2]=pieces[x][y];pieces[x][y]=null;
					}
					j2++;
					if(j2 >= leny)
					{
						i2++;j2=0;
					}
				}
				z++;
			}
		}
	
		return ps;
	}
	
	// eg:primary: xx=4;yy=6, so, pieces[yy][xx] = pieces[6][4]
	private void arrayPieceForMode(Piece[][] pieces,ArrangeEnum mode,int xx,int yy)
	{
		/*
		if ( mode == ArrangeEnum.V )
		{
			for(int i=0;i<xx;i++)
			{
				if ( i%2 == 1 )
				{
					for(int j=0;j<yy;j++)
					{
						pieces[i][j] = null;
					}
				}
			}
		} else if ( mode == ArrangeEnum.H )
		{
			for(int j=0;j<yy;j++)
			{
				if ( j%2 == 1 )
				{
					for(int i=0;i<xx;i++)
					{
						pieces[i][j] = null;
					}
				}
			}
		}
		*/
	}
	
	// eg:primary: xx=4;yy=6, so, pieces[xx][yy] = pieces[4][6]
	public Piece[][] getAllPieces(int all,int xx,int yy)
	{
		Piece[][] pieces = new Piece[xx][yy];
		Util util = new Util(context,ad);
		util.getCurrentImages();
		List<Integer>intL = util.getIndexForGrade(all,xx,yy);
		List<Bitmap>bl = util.getPlayImages(ad.imageStrList.get(ad.imageTeamIndex));

		for(int x=0;x<xx;x++)
		{
			for(int y=0;y<yy;y++)
			{
				Piece p = new Piece();
				int loc = x*yy + y;
				p.setImageId(intL.get(loc));
				p.setImage(bl.get(intL.get(loc)));
				int x1 = ad.beginCoorX+ x*ad.imageWidth;
				int y1 = ad.beginCoorY + y*ad.imageHeight;
				p.setIndexX(x);
				p.setIndexY(y);
				p.setBeginX(x1);
				p.setBeginY(y1);
				pieces[x][y]=p;
			}
		}
		return pieces;
	}
	
	
	// 初级的  x:4,y:6 ,total:24
	private Piece[][] initPrimary(ArrangeEnum mode)
	{
		Piece[][] pieces = getAllPieces(GameGrade.PRIMARY,GameGrade.PRIMARY_X,GameGrade.PRIMARY_Y);
		arrayPieceForMode(pieces,mode,GameGrade.PRIMARY_X,GameGrade.PRIMARY_Y);
		//Log.v("iiiiiiiii",pieces.length + "  " + pieces[0][0].getImageId());
		return pieces;
	}
	
	// 中级 x:6,y:8,total:48
	private Piece[][] initMedium(ArrangeEnum mode)
	{
		Piece[][] pieces = getAllPieces(GameGrade.MEDIUM,GameGrade.MEDIUM_X,GameGrade.MEDIUM_Y);
		arrayPieceForMode(pieces,mode,GameGrade.MEDIUM_X,GameGrade.MEDIUM_Y);
		return pieces;
	}
	// 高级的 x:8,y:10,total:80
	private Piece[][] initAdvanced(ArrangeEnum mode)
	{
		Piece[][] pieces = getAllPieces(GameGrade.ADVANCED,GameGrade.ADVANCED_X,GameGrade.ADVANCED_Y);
		arrayPieceForMode(pieces,mode,GameGrade.ADVANCED_X,GameGrade.ADVANCED_Y);
		return pieces;
	}
	// 变态级别 x:10,y:12,total:120
	private Piece[][] initSuper(ArrangeEnum mode)
	{
		Piece[][] pieces = getAllPieces(GameGrade.SUPER,GameGrade.SUPER_X,GameGrade.SUPER_Y);
		arrayPieceForMode(pieces,mode,GameGrade.SUPER_X,GameGrade.SUPER_Y);
		return pieces;
	}
	
}


