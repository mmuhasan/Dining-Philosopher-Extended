package assognment3;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/** a readable state names of a philosopher **/
public enum PhilosopherState {
	THINKING,WAITING_ON_SYSTEM,WAITING_FOR_FORK,EATING,LEFTSPOON,RIGHTSPOON
}

/**
 * A spoon class that represent each of the spoon. 
 * This class use a monitor that allows sequential
 * access of the spoon by multiple philosopher. 
 * @author mmuhasan
 *
 */
class Spoon {
	int idSpoon; 
	boolean hold; /** is this spoon currnetly hold by a philosopher **/
	Lock lock;   /** a lock that use as monitor **/
	
	public Spoon(int id) {
		idSpoon = id;
		hold=false;
		lock = new ReentrantLock();
	}

	/** pick up the spoon **/
	public boolean take() 
	{
		lock.lock();
		try
		{
			if(!hold)
			{
				return hold=true;
			}
			return false;
		}
		finally
		{
			lock.unlock();
		}
	}

	/** release the spoon **/
	public void leave() {
		lock.lock();
		try
		{
			hold=false;
		}
		finally
		{
			lock.unlock();
		}
		
	}
}