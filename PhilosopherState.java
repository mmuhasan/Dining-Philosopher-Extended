package assognment3;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public enum PhilosopherState {
	THINKING,WAITING_ON_SYSTEM,WAITING_FOR_FORK,EATING,LEFTSPOON,RIGHTSPOON
}

class Spoon {
	int idSpoon;
	boolean hold;
	Lock lock;
	
	public Spoon(int id) {
		idSpoon = id;
		hold=false;
		lock = new ReentrantLock();
	}

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