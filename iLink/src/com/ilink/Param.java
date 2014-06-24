package com.ilink;

public class Param {
	final static public int imageCount = 14;

	final static public int fixWidth=64;
	final static public int fixHeight=64;
	
	final static public int TIMER1SCHEDULE = 0x1;
	final static public int SUCCESSFUL=0x10;
	final static public int SUCESSVIBRATOR=100;
	final static public int ADDTIMEINT=10;
	static public int GAMETIMEINT=100;
	static public int SHUFFLETIMEINT=30;
	final static class GameGrade
	{
		static int PRIMARY=8;
		static int PRIMARY_X=4;
		static int PRIMARY_Y=6;
		
		static int MEDIUM=10;
		static int MEDIUM_X=6;
		static int MEDIUM_Y=8;
		
		static int ADVANCED=12;
		static int ADVANCED_X=8;
		static int ADVANCED_Y=10;
		
		static int SUPER=14;
		static int SUPER_X=10;
		static int SUPER_Y=12;
	};
	
	enum ArrangeEnum
	{
		F,H,V;
	}
	
	enum OrienEnum
	{
		N,T,B,L,R;
	}
	
	enum GradeEnum
	{
		P(1),M(2),A(3),S(4);
		private int grade;
		private GradeEnum(int i)
		{
			grade = i;
		}
		public void nextGrade()
		{
			if(grade < 4 )
				grade++;
		}
		public int getGrade()
		{
			return grade;
		}
	}
	
	enum StartEnum
	{
		START,RESTART,NEXT,NEXTGRADE;
	}
	
	
}
