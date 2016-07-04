package assognment3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Phaser;

/**
 * A class to represent a Philosopher
 * @author mmuhasan
 */
public class Philosopher 
{
	int 				timeLeft;	/** Keep track of time of the 
										work the philosopher on at 
										any given time. **/
	int 				idPhilosopher; /** id of philosopher that use for their name */
	int 				idTable;	/** the table id where the philosopher sit at 
										any given time. **/ 
	int 				position; /** the position in the table where the philosopher sit at 
									any given time. **/
	Phaser 				guard;	/** a Phaser that allow threads to be synchronized 
									with the simulated clock **/
	PhilosopherState 	state;
	PhilosopierThread 	t;		/** the thread that simulate the philosopher. **/
	int					spoonHold; /** keep track of how many spoon a phiolosophier is currently holding **/
	
	/** constructor */
	public Philosopher(int i, Phaser simulationGuard, ExecutorService pool) 
	{
		idPhilosopher 	= i;
		idTable 		= i/5;
		position		= i%5;
		timeLeft		= 0;
		t 				= new PhilosopierThread(this, simulationGuard);
		spoonHold		= 0;
		pool.execute(t);		
	}
	
	/** Start thinking */
	public void thinkIng()
	{
		state 		= PhilosopherState.THINKING;
		timeLeft	= MyRandom.thinkIngTime();
	}
	
	/**
	 * @return the table id
	 */
	public int getTable() {
		return idTable;
	}
	
	/**
	 * The philosopher move to the sixth table 
	 */
	public void changeTable(int position6) {
		reset();
		idTable = 5;
		position = position6;
	}
	
	/**
	 * @return what the philosopher is currently doing.
	 */
	public PhilosopherState getStatus() {
		return state;
	}

	/**
	 * Stop a philosopher thread.
	 */
	public void stopThread() {
		t.kill();
	}
	
	/**
	 * Picking up the left spoon.
	 */
	private boolean takeLeftSpoon()
	{
		int iPosition = position-1;
		if(position==0)
			iPosition = 4;
		return Simulator.objSpoons[idTable][iPosition].take();
	}
	
	/**
	 * Picking up the right spoon.
	 */
	private boolean takeRightSpoon(){

		return Simulator.objSpoons[idTable][position].take();
	}

	/**
	 * Picking up the a spoon (left or right)
	 */
	public void PickSpoon() {
		if(spoonHold==0){
			if(takeLeftSpoon())
			{
				state 		= PhilosopherState.WAITING_ON_SYSTEM;
				timeLeft 	= 4;
				spoonHold	= 1;
			}
			else
			{
				state 		= PhilosopherState.WAITING_FOR_FORK;
			}
		}
		else if(spoonHold == 1)
		{
			if(takeRightSpoon())
			{
				state 		= PhilosopherState.EATING;
				spoonHold	= 2;
				timeLeft 	= MyRandom.eatingTime();
			}
			else
			{
				state 		= PhilosopherState.WAITING_FOR_FORK;
			}
		
		}
	}

	/** 
	 * reset the status of the philosopher
	 * Reset the relevant spoon status 
	 * and start thinking
	 **/
	public void reset() 
	{
		int iPosition = position-1;
		if(position==0)
			iPosition = 4;
		
		if(spoonHold > 0)
			Simulator.objSpoons[idTable][iPosition].leave();
		if(spoonHold == 2)	
			Simulator.objSpoons[idTable][position].leave();
		spoonHold = 0;
		
		thinkIng();
	}

	public void debugPrint() 
	{
		System.out.println("id: "+idPhilosopher+" Table: "+idTable+" ("+position+") state: "+state+ "("+timeLeft+")"+ " spoon: "+spoonHold);
	}
}

/**
 * The thread that represent a philosopher
 * @author mmuhasan
 */
class PhilosopierThread extends Thread {
					 Phaser 			Guard;				/** a phaser to synchronize with the simulated clock */
	private volatile boolean 			isRunning = true;   
					 Philosopher 		objPhilosopher;		/** the instances of the phiolosopher **/
					 PhilosopherState 	state;
	   
	/** constructor **/
	PhilosopierThread(Philosopher pho,Phaser p)
	{
		objPhilosopher	=pho;
		Guard 			= p;
		
		Guard.register();
	}
	   
	@Override
	public void run()
	{
		objPhilosopher.thinkIng();
		Guard.arriveAndAwaitAdvance(); // wait until every other thread is ready to start.
		
		while(isRunning)  // run the thread until it asked to be shut down.
		{
			state = objPhilosopher.getStatus(); // the current status of the philosopher and take action based on it.
			switch(state){
			
			case THINKING:
				if(objPhilosopher.timeLeft==0)
					objPhilosopher.PickSpoon();
				else objPhilosopher.timeLeft--;
				break;
			case WAITING_FOR_FORK:
				objPhilosopher.PickSpoon();	
				break;
			case WAITING_ON_SYSTEM:
				if(objPhilosopher.timeLeft==0)
					objPhilosopher.PickSpoon();
				else objPhilosopher.timeLeft--; 
				break;
			case EATING:
				if(objPhilosopher.timeLeft==0)
					objPhilosopher.reset();
				else objPhilosopher.timeLeft--;
			}
	    	  
			Guard.arriveAndAwaitAdvance();
			/**
			 * waiting for master thread to finish checking 
			 * deadlocks and checking necessary action.
			 */
			Guard.arriveAndAwaitAdvance();
		}
		Guard.arriveAndDeregister();
		      
	}

	/** finish the thread **/
	public void kill() 
	{ 
		isRunning= false;
	}

}
