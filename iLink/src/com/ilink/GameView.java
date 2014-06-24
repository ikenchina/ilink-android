package com.ilink;

import java.util.List;
import com.ilink.Param.ArrangeEnum;
import com.ilink.Param.GradeEnum;
import com.ilink.Param.OrienEnum;

import android.graphics.Point;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View
{
	// 游戏逻辑的实现类
	private GameService gameService;
	// 保存当前已经被选中的方块
	private Piece selectedPiece;
	private Piece currentPiece;
	// 连接信息对象
	private LinkInfo linkInfo;
	private Paint paint;
	// 选中标识的图片对象
	private Bitmap selectImage;
	private Handler handler;
	private Piece[][] pieces;
	private OrienEnum orienEnum;
	private AppData ad;
	//private Context context;
	public GameView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		//this.context = context;
		
		this.paint = new Paint();
		// 设置连接线的颜色
		this.paint.setColor(Color.RED);
		// 设置连接线的粗细
		this.paint.setStrokeWidth(3);
		this.selectImage = Util.getSelectImage(context);
		orienEnum = OrienEnum.N;
	}

	public void setHandler(Handler h)
	{
		handler = h;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		if ( event.getAction() == MotionEvent.ACTION_DOWN )
		{
			float touchX = event.getX();
			float touchY = event.getY();
			currentPiece = gameService.findPiece(touchX, touchY);
			if(currentPiece == null)
				return false;
			//this.selectedPiece = currentPiece;
			// 表示之前没有选中任何一个Piece
			if ( this.selectedPiece == null )
			{
				this.selectedPiece = currentPiece;
				this.postInvalidate();
				return false;
			}
			// 表示之前已经选择了一个
			if (this.selectedPiece != null)
			{
				LinkInfo linkInfo2 = this.gameService.link(this.selectedPiece,currentPiece);
				// 两个Piece不可连, linkInfo为null
				if (linkInfo2 == null)
				{
					this.selectedPiece = currentPiece;
					this.postInvalidate();
				}
				else
				{
					setLinkInfo(linkInfo2);
					this.postInvalidate();
					pieces[selectedPiece.getIndexX()][selectedPiece.getIndexY()] = null;
					pieces[currentPiece.getIndexX()][currentPiece.getIndexY()] = null;
					selectedPiece = null;
					currentPiece=null;
					if ( orienEnum != OrienEnum.N )
					{
						gameService.downUpdate(orienEnum);
					}
					handler.sendEmptyMessage(Param.SUCCESSFUL);
					// 处理成功连接
					//handleSuccessLink(linkInfo, this.selectedPiece, currentPiece, pieces);
				}
			}
		} else if (event.getAction() == MotionEvent.ACTION_UP  )
		{
			this.postInvalidate();
		}
		//return super.onTouchEvent(event);
		return true;
	}

	public void shuffle()
	{
		gameService.shuffle(orienEnum);
		// 在运行之前，view和gameservice还有board中的pieces都指向同一个数组，
		// 但在 boar.shuffle中，有将所有的pieces都设为null了，而在 gameService.shuffle中
		//又赋值给了pieces本身，所以gameService中的pieces已经不是指向原来的地方了，而是返回的ps数组！
		// 而view的pieces指向的原来的数组已经全部为null了。所以此处重新赋值保证view的pieces仍和gameService
		// 的pieces指向同一个地方！
		pieces = gameService.getPieces(); 
		
		this.postInvalidate();		
	}
	
	public void setLinkInfo(LinkInfo linkInfo)
	{
		this.linkInfo = linkInfo;
	}

	public void setGameService(GameService gameService)
	{
		this.gameService = gameService;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		//Rect rect = new Rect(0,0,ad.imageWidth,ad.imageHeight);
		if (this.gameService == null)
			return;
		//Piece[][] pieces = gameService.getPieces();
		//Log.v("pppppppp", ""+ (pieces != null));
		if (pieces != null)
		{
			// 遍历pieces二维数组
			for (int i = 0; i < pieces.length; i++)
			{
				for (int j = 0; j < pieces[i].length; j++)
				{
					if (pieces[i][j] != null)
					{
						Piece p = pieces[i][j];
						//Log.v("zzzz",""+i+" "+j+" "+p.getIndexX()+" "+p.getIndexY());
						
						Rect rect = new Rect(p.getBeginX(),p.getBeginY(),p.getBeginX()+ad.imageWidth,p.getBeginY()+ad.imageHeight);
						canvas.drawBitmap(p.getImage(),null,rect,null);
					}
				}
			}
		}
		// 如果当前对象中有linkInfo对象, 即连接信息
		if (this.linkInfo != null)
		{
			// 绘制连接线
			drawLine(this.linkInfo, canvas);
			// 处理完后清空linkInfo对象
			this.linkInfo = null;
		}
		// 画选中标识的图片
		if (this.selectedPiece != null)
		{
			//Rect rect = new Rect(selectedPiece.getBeginX(),selectedPiece.getBeginY(),selectedPiece.getBeginX()+ad.imageWidth,selectedPiece.getBeginY()+ad.imageHeight);
			//canvas.drawBitmap(selectImage,null,rect,null);
			
			Rect rect = new Rect(selectedPiece.getBeginX()-5,selectedPiece.getBeginY()-5,selectedPiece.getBeginX()+ad.imageWidth+5,selectedPiece.getBeginY()+ad.imageHeight+5);
			canvas.drawBitmap(selectedPiece.getImage(),null,rect,null);
			
			//canvas.drawBitmap(this.selectImage, this.selectedPiece.getBeginX(),
			//	this.selectedPiece.getBeginY(), null);
		}
	}

	// 根据LinkInfo绘制连接线的方法。
	private void drawLine(LinkInfo linkInfo, Canvas canvas)
	{
		// 获取LinkInfo中封装的所有连接点
		List<Point> points = linkInfo.getLinkPoints();
		// 依次遍历linkInfo中的每个连接点
		for (int i = 0; i < points.size() - 1; i++)
		{
			// 获取当前连接点与下一个连接点
			Point currentPoint = points.get(i);
			Point nextPoint = points.get(i + 1);
			// 绘制连线
			canvas.drawLine(currentPoint.x , currentPoint.y,
				nextPoint.x, nextPoint.y, this.paint);
		}
	}

	// 设置当前选中方块的方法
	public void setSelectedPiece(Piece piece)
	{
		this.selectedPiece = piece;
	}

	// 开始游戏方法
	public void startGame(GradeEnum ae,ArrangeEnum mode,OrienEnum eo,AppData ad2)
	{
		this.ad = ad2;
		this.orienEnum = eo;
		this.gameService.start(ae,mode);
		pieces = gameService.getPieces();
		//Util u= new Util(context,ad);
		
		//Log.v("tttttttt","" + gameService.hasPieces());
		this.postInvalidate();
	}
}
