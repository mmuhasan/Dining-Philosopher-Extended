package assognment3;

public class MyRandom 
{
	public static int rangeRandom(int s,int e)
	{
		return s+((int)(Math.random()*1000))%(e-s+1);
	}
	
	public static int randomPholosopher(){
		return rangeRandom(1, 5); 
	}
	
	public static int thinkIngTime(){
		return rangeRandom(0,10);
	}
	public static int eatingTime(){
		return rangeRandom(0,5);
	}

}