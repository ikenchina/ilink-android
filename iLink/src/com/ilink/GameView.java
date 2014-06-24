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
	// ��Ϸ�߼���ʵ����
	private GameService gameService;
	// ���浱ǰ�Ѿ���ѡ�еķ���
	private Piece selectedPiece;
	private Piece currentPiece;
	// ������Ϣ����
	private LinkInfo linkInfo;
	private Paint paint;
	// ѡ�б�ʶ��ͼƬ����
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
		// ���������ߵ���ɫ
		this.paint.setColor(Color.RED);
		// ���������ߵĴ�ϸ
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
			// ��ʾ֮ǰû��ѡ���κ�һ��Piece
			if ( this.selectedPiece == null )
			{
				this.selectedPiece = currentPiece;
				this.postInvalidate();
				return false;
			}
			// ��ʾ֮ǰ�Ѿ�ѡ����һ��
			if (this.selectedPiece != null)
			{
				LinkInfo linkInfo2 = this.gameService.link(this.selectedPiece,currentPiece);
				// ����Piece������, linkInfoΪnull
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
					// ����ɹ�����
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
		// ������֮ǰ��view��gameservice����board�е�pieces��ָ��ͬһ�����飬
		// ���� boar.shuffle�У��н����е�pieces����Ϊnull�ˣ����� gameService.shuffle��
		//�ָ�ֵ����pieces��������gameService�е�pieces�Ѿ�����ָ��ԭ���ĵط��ˣ����Ƿ��ص�ps���飡
		// ��view��piecesָ���ԭ���������Ѿ�ȫ��Ϊnull�ˡ����Դ˴����¸�ֵ��֤view��pieces�Ժ�gameService
		// ��piecesָ��ͬһ���ط���
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
			// ����pieces��ά����
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
		// �����ǰ��������linkInfo����, ��������Ϣ
		if (this.linkInfo != null)
		{
			// ����������
			drawLine(this.linkInfo, canvas);
			// ����������linkInfo����
			this.linkInfo = null;
		}
		// ��ѡ�б�ʶ��ͼƬ
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

	// ����LinkInfo���������ߵķ�����
	private void drawLine(LinkInfo linkInfo, Canvas canvas)
	{
		// ��ȡLinkInfo�з�װ���������ӵ�
		List<Point> points = linkInfo.getLinkPoints();
		// ���α���linkInfo�е�ÿ�����ӵ�
		for (int i = 0; i < points.size() - 1; i++)
		{
			// ��ȡ��ǰ���ӵ�����һ�����ӵ�
			Point currentPoint = points.get(i);
			Point nextPoint = points.get(i + 1);
			// ��������
			canvas.drawLine(currentPoint.x , currentPoint.y,
				nextPoint.x, nextPoint.y, this.paint);
		}
	}

	// ���õ�ǰѡ�з���ķ���
	public void setSelectedPiece(Piece piece)
	{
		this.selectedPiece = piece;
	}

	// ��ʼ��Ϸ����
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
