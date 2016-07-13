package assognment3;

/**
 * A class produce randoem numbers 
 * according to the assignments constrains.
 * @author mmuhasan
 */

public class MyRandom 
{
	/** 
	 * @param s
	 * @param e
	 * @return a random integer number with 
	 * the range from s to e inclusive
	 */
	public static int rangeRandom(int s,int e)
	{
		return s+((int)(Math.random()*1000))%(e-s+1);
	}
	
	/**
	 * @return a random number between 1 to 5
	 */
	public static int randomPholosopher(){
		return rangeRandom(1, 5); 
	}
	
	/**
	 * @return a random thinking time.
	 */
	public static int thinkIngTime(){
		return rangeRandom(0,10);
	}

	/**
	 * @return a random eating time.
	 */
	public static int eatingTime(){
		return rangeRandom(0,5);
	}

}