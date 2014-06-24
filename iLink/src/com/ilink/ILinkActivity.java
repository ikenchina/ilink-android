package com.ilink;

import java.util.Timer;
import java.util.TimerTask;

import com.ilink.Param.ArrangeEnum;
import com.ilink.Param.GameGrade;
import com.ilink.Param.GradeEnum;
import com.ilink.Param.OrienEnum;
import com.ilink.Param.StartEnum;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

public class ILinkActivity extends Activity {
	// 计时
	private Chronometer timerMeter;
	private ProgressBar timerProgressbar;
	// 游戏view
	private GameView gameView;
	// 洗牌
	private TextView shuffleTV;
	private ImageButton shuffleIB;
	// 提示
	private TextView tipTV;
	private ImageButton tipIB;
	// 加时
	private TextView addtimeTV;
	private ImageButton addtimeIB;
	// 计分
	private TextView scoreTV;
	private ImageButton scoreIB;
	private ImageButton startIB;
	//private AlertDialog.Builder lostDialog;
	//private AlertDialog.Builder successDialog;
	private AppData appData;
	private GameService gameService;
	// 游戏时间timer
	private Timer timer1;
	// 规则时间timer
	//private Timer timer2;
	private int gameTime1;
	private int gameTime2;
	
	private boolean isPlaying;
	private boolean canShuffle;
	
	private Vibrator vibrator;
	// 状态按钮的animation
	private Animation statusAnimation;
	
	private Piece selected = null;
	//private Piece[][] pieces;
	
	private GradeEnum gradeEnum = GradeEnum.P;
	private ArrangeEnum arrangeEnum = ArrangeEnum.F;
	private OrienEnum orienEnum = OrienEnum.N;
	private int scoreShuffle=5;
	private int scoreTip=5;
	private int scoreAddTime=3;
	private int scoreInt=0;
	private Handler handler=null;
	// 每个等级分5关,此记录当前关序号
	private int innerGradeInt =0;
	private int screenWidth;
	private int screenHeight;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ilink);
        statusAnimation = AnimationUtils.loadAnimation(this,R.anim.shake);
        
        startIB = (ImageButton)findViewById(R.id.startIB);
        timerProgressbar = (ProgressBar)findViewById(R.id.timeProgressbar);
        gameView =(GameView)findViewById(R.id.gameView1);
        shuffleTV = (TextView)findViewById(R.id.shuffleTV);
        shuffleIB = (ImageButton)findViewById(R.id.shuffleIB);
        tipTV = (TextView)findViewById(R.id.tipTV);
        tipIB = (ImageButton)findViewById(R.id.tipIB);
        addtimeTV = (TextView)findViewById(R.id.addtimeTV);
        addtimeIB = (ImageButton)findViewById(R.id.addtimeIB);
        scoreTV = (TextView)findViewById(R.id.scoreTV);
        scoreIB = (ImageButton)findViewById(R.id.scoreIB);
        
        appData = (AppData)getApplication();
        gameService = new GameService(this,appData);
        gameView.setGameService(gameService);
        gameView.setHandler(handler);
        gameTime1 = Param.GAMETIMEINT;
        timerProgressbar.setMax(gameTime1);
        gameTime2 = Param.SHUFFLETIMEINT;
        
        shuffleTV.setText(String.valueOf(scoreShuffle));
        tipTV.setText(String.valueOf(scoreTip));
        addtimeTV.setText(String.valueOf(scoreAddTime));
        scoreTV.setText(String.valueOf(scoreInt));

        isPlaying = false;
        canShuffle=false;
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight=dm.heightPixels;

 
        
        // 处理类
        handler = new Handler()
        {
    		@Override
    		public void handleMessage(Message msg) {
    			// 若是timer1的作业,
    			if ( msg.what == Param.TIMER1SCHEDULE )
    			{
    				if ( isPlaying )
    				{
    					// shuffle
    					if(canShuffle)
    					{
    						gameTime2--;
    						if(gameTime2<0)
    						{
    							gameTime2=Param.SHUFFLETIMEINT;
    							shuffleIB.startAnimation(statusAnimation);
    							gameView.shuffle();
    						}
    					}
    					gameTime1--;
    					timerProgressbar.setProgress(gameTime1);
    					if(gameTime1 <=0 )
    					{
    						isPlaying=false;
    						Builder b = new AlertDialog.Builder(ILinkActivity.this);
        			    	b.setTitle(R.string.fail);
        					b.setPositiveButton(getResources().getString(R.string.playAgain), 
        					new OnClickListener()
        					{
    							@Override
    							public void onClick(DialogInterface dialog,int which) {
    								start(StartEnum.RESTART);
    							}
        					});
        					b.setNegativeButton(getResources().getString(R.string.cancel),
        					new OnClickListener()
        					{
    							@Override
    							public void onClick(DialogInterface dialog,int which) {
    								finish();
    							}
        					});
        					b.show();
    					}
    				}
    			} else if ( msg.what == Param.SUCCESSFUL )
    			{
    				vibrator.vibrate(Param.SUCESSVIBRATOR);
    				addScoreInt();
    				scoreTV.setText(String.valueOf(scoreInt));
    				scoreIB.startAnimation(statusAnimation);
    				
    				if ( ! gameService.hasPieces() )
    				{
    					isPlaying=false;
    					
    					Builder b = new AlertDialog.Builder(ILinkActivity.this);
    			    	b.setTitle(R.string.win);
    			    	String[] strs = getResources().getStringArray(R.array.gradeArray);
    					b.setPositiveButton(getResources().getString(R.string.next), 
    					new OnClickListener()
    					{
							@Override
							public void onClick(DialogInterface dialog,int which) {
								start(StartEnum.NEXT);
							}
    					});
    					b.setNegativeButton(getResources().getString(R.string.cancel),
    					new OnClickListener()
    					{
							@Override
							public void onClick(DialogInterface dialog,int which) {
								finish();
							}
    					});
    					b.show();
    				}
    			}
    		}
        };
        gameView.setHandler(handler);
        
        // 重新洗牌按钮
        shuffleIB.setOnClickListener(new View.OnClickListener()
        {
			@Override
			public void onClick(View v) {
				if(scoreShuffle<=0)
					return;
				scoreShuffle--;
				shuffleTV.setText(String.valueOf(scoreShuffle));
				shuffleIB.startAnimation(statusAnimation);
				gameView.shuffle();
			}
        });
        // 
  
        addtimeIB.setOnClickListener(new View.OnClickListener()
        {
			@Override
			public void onClick(View v) {
				if(scoreAddTime <=0)
					return;
				scoreAddTime--;
				addtimeTV.setText(String.valueOf(scoreAddTime));
				gameTime1 += Param.ADDTIMEINT;
				timerProgressbar.setProgress(gameTime1);
			}
        });
        
        
        
        // 开始暂停按钮
        startIB.setOnClickListener(new View.OnClickListener()
        {
			@Override
			public void onClick(View v) {
				if(isPlaying)
				{
					isPlaying=false;
				}else{
					isPlaying=true;
				}
			}
        });
        showGradeDialog();
    }
    
    void showGradeDialog()
    {
    	Builder b = new AlertDialog.Builder(this);
    	b.setTitle(R.string.gradetext);
    	String[] strs = getResources().getStringArray(R.array.gradeArray);
    	b.setSingleChoiceItems(strs,0,new OnClickListener()
    	{
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO 
				switch(arg1)
				{
				case 0:
					gradeEnum = GradeEnum.P;
					break;
				case 1:
					gradeEnum = GradeEnum.M;
					//orienEnum = OrienEnum.B;
					break;
				case 2:
					gradeEnum = GradeEnum.A;
					break;
				default:
					gradeEnum = GradeEnum.S;
					break;
				}
			}
    	});
    	b.setPositiveButton("OK", new OnClickListener()
    	{
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				start(StartEnum.START);
			}
    	});
    	b.show();
    }
    
    @Override
	protected void onDestroy() {
		// delete the threads of timer1,timer2,else they are still running until you delete it manually.
    	
    	timer1.cancel();
    	//timer2.cancel();
		super.onDestroy();
	}


	// start function
    private void start(StartEnum se)
    {
    	
    	//timer1.cancel();
    	//timer2.cancel();
    	
    	this.innerGradeInt++;
    	// 开始
    	if ( se == StartEnum.START )
    	{
    	}
    	else if (se == StartEnum.RESTART)
    	{
    	}
    	//  下一关
    	else if ( se == StartEnum.NEXT )
    	{
    		if ( this.innerGradeInt > 5 )
    		{
    			this.innerGradeInt=1;
    			gradeEnum.nextGrade();
    			this.arrangeEnum = ArrangeEnum.F;
    			this.orienEnum = OrienEnum.N;
    		}else {
        		if(this.gradeEnum == GradeEnum.P)
        		{
        			orienEnum = OrienEnum.N;
        			if(this.innerGradeInt==1)
        			{
        				arrangeEnum = ArrangeEnum.H;
        			} else if ( this.innerGradeInt == 2 )
        			{
        				arrangeEnum = ArrangeEnum.V;
        			} else
        			{
        				arrangeEnum = ArrangeEnum.F;
        			}
        		} else
        		{
        			arrangeEnum = ArrangeEnum.F;
        			if(this.innerGradeInt == 1)
        			{
        				orienEnum = OrienEnum.N;
        			} else if ( this.innerGradeInt == 2 )
        			{
        				orienEnum = OrienEnum.T;
        			} else if ( this.innerGradeInt == 3 )
        			{
        				orienEnum = OrienEnum.B;
        			} else if ( this.innerGradeInt == 4 )
        			{
        				orienEnum = OrienEnum.L;
        			} else if ( this.innerGradeInt == 5 )
        			{
        				orienEnum = OrienEnum.R;
        			}
        		}
    		}
    	}
    	// 下一个等级
    	else if ( se == StartEnum.NEXTGRADE )
    	{
    		this.innerGradeInt = 1;
    		this.arrangeEnum = ArrangeEnum.F;
    		this.orienEnum = OrienEnum.N;
    		gradeEnum.nextGrade();
    	}
    	canShuffle=true;
    	
    	if ( gradeEnum == GradeEnum.P )
		{
    		canShuffle=false;
			appData.countX = GameGrade.PRIMARY_X;
			appData.countY = GameGrade.PRIMARY_Y;
			appData.cImageCount = GameGrade.PRIMARY;
		} else if ( gradeEnum == GradeEnum.M )
		{
			appData.countX = GameGrade.MEDIUM_X;
			appData.countY = GameGrade.MEDIUM_Y;
			appData.cImageCount = GameGrade.MEDIUM;
		} else if ( gradeEnum == GradeEnum.A )
		{
			appData.countX = GameGrade.ADVANCED_X;
			appData.countY = GameGrade.ADVANCED_Y;
			appData.cImageCount = GameGrade.ADVANCED;
		} else {
			appData.countX = GameGrade.SUPER_X;
			appData.countY = GameGrade.SUPER_Y;
			appData.cImageCount = GameGrade.SUPER;
		}
    	
    	
    	appData.imageWidth = gameView.getWidth()/appData.countX;
    	appData.imageHeight = gameView.getHeight()/appData.countY;
    	if ( appData.imageWidth > appData.imageHeight )
    	{
    		appData.imageWidth = appData.imageHeight;
    	} else {
    		appData.imageHeight = appData.imageWidth;
    	}

    	appData.beginCoorX = (gameView.getWidth() - appData.countX*appData.imageWidth)/2 ;
    	appData.beginCoorY = (gameView.getHeight() - appData.countY*appData.imageHeight)/2;
    	
    	//Log.v("cc", gameView.getLeft() + " " + appData.beginCoorX + " " + gameView.getWidth() + " "+ appData.countY*appData.imageWidth + " "+screenWidth);
    	//Log.v("ss", gameView.getTop() + " " + appData.beginCoorY + " " + gameView.getHeight() + " "+ appData.countX*appData.imageHeight + " "+screenHeight);
    	
    	if(se != StartEnum.START)
    	{
    		timer1.cancel();
        	//timer2.cancel();
        	timer1 = new Timer();
        	//timer2 = new Timer();
    	} else {
    		timer1 = new Timer();
        	//timer2 = new Timer();
    	}
    	
    	gameView.startGame(gradeEnum,arrangeEnum,orienEnum,appData);
    	gameTime2=Param.SHUFFLETIMEINT;
    	isPlaying=true;
    	gameTime1 = Param.GAMETIMEINT;
        timerProgressbar.setMax(gameTime1);
    	timer1.schedule(new TimerTask()
    	{
			@Override
			public void run() {
				handler.sendEmptyMessage(Param.TIMER1SCHEDULE);
			}
    	},0, 1000);
    	
    }

    private void addScoreInt()
    {
    	if(gradeEnum == GradeEnum.P)
		{
			scoreInt++;
		} else if ( gradeEnum == GradeEnum.M || gradeEnum == GradeEnum.A)
		{
			scoreInt += 2;
		} else {
			scoreInt += 3;
		}
    }
}




