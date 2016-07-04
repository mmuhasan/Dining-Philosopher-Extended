package assognment3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Phaser;

public class Philosopher 
{
	int 				timeLeft;
	int 				idPhilosopher;
	int 				idTable;
	int 				position;
	Phaser 				guard;
	PhilosopherState 	state;
	PhilosopierThread 	t;
	int					spoonHold;
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
	
	public void thinkIng()
	{
		state 		= PhilosopherState.THINKING;
		timeLeft	= MyRandom.thinkIngTime();
	}
	
	public int getTable() {
		return idTable;
	}
	public void changeTable(int position6) {
		reset();
		idTable = 5;
		position = position6;
	}
	public PhilosopherState getStatus() {
		return state;
	}

	public void stopThread() {
		t.kill();
	}
	
	private boolean takeLeftSpoon()
	{
		int iPosition = position-1;
		if(position==0)
			iPosition = 4;
		return Simulator.objSpoons[idTable][iPosition].take();
	}
	
	private boolean takeRightSpoon(){

		return Simulator.objSpoons[idTable][position].take();
	}

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

	public void reset() 
	{
		int iPosition = position-1;
		if(position==0)
			iPosition = 4;
		
		Simulator.objSpoons[idTable][iPosition].leave();
		Simulator.objSpoons[idTable][position].leave();
		spoonHold = 0;
		
		thinkIng();
	}

	public void debugPrint() 
	{
		System.out.println("id: "+idPhilosopher+" Table: "+idTable+" ("+position+") state: "+state+ "("+timeLeft+")"+ " spoon: "+spoonHold);
	}
}

class PhilosopierThread extends Thread {
					 Phaser 			Guard;
	private volatile boolean 			isRunning = true;
					 Philosopher 		objPhilosopher;
					 PhilosopherState 	state;
	   
	PhilosopierThread(Philosopher pho,Phaser p)
	{
		objPhilosopher	=pho;
		Guard 			= p;
		
		Guard.register();
	}
	   
	public void run()
	{
		objPhilosopher.thinkIng();
		Guard.arriveAndAwaitAdvance();
		while(isRunning) 
		{
			state = objPhilosopher.getStatus();
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

	public void kill() 
	{ 
		isRunning= false;
	}

}
