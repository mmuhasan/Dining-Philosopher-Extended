package assognment3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public class Simulator {
	
		   ExecutorService 	pool 			= Executors.newFixedThreadPool(25);
	static boolean[] 		TableFull 		= {true,true,true,true,true};
	static int 				position6 		= 0;
	static Spoon[][] 		objSpoons;
	static Philosopher[] 	objPhilosopher;
	static Phaser 			simulationGuard;
	private static int 		lastMove;
		   

	public Simulator() 
	{
		simulationGuard = new Phaser();
		
		simulationGuard.register();
		objPhilosopher = new Philosopher[25];
		
		objSpoons = new Spoon[6][5];
		for(int i =0;i<6;i++)
			for(int j=0;j<5;j++)
				objSpoons[i][j] = new Spoon(i*5+j);
		
		for(int i = 0; i < 25; i ++)
			objPhilosopher[i] = new Philosopher(i,simulationGuard,pool);
	}

	public static void main(String[] args) 
	{
		Simulator objSimulator = new Simulator();

		simulationGuard.arriveAndAwaitAdvance();
		int simulatorTime = 0;
		while(tableDeadlock(5)!=true)
		{			
			simulationGuard.arriveAndAwaitAdvance();
			for(int i = 0; i<5;i++)
				if(TableFull[i] && tableDeadlock(i)==true)
					changeTable(i);
			simulationGuard.arriveAndAwaitAdvance();
			
			simulatorTime++;

//			System.out.println(simulatorTime);
//			for(int i =0;i<25;i++)
//				if(objPhilosopher[i].getTable()==5)
//				{
//					objPhilosopher[i].debugPrint();
//				}
//			if(simulatorTime>500)
//				break;
		
		}
		
		/** print Final result **/
		System.out.println("Deadlock occur at "+simulatorTime+" second");
		System.out.printf("The last philosophers moved to sixth table was %c",65+lastMove);
		
	}
	
	private static void changeTable(int idTable) {
		int p = MyRandom.randomPholosopher();
		int[] ids =  {0,1,2,3,4};
		int[] ids1 = {0,0,0,0,0};
		int i;
		for(i = 0; i< 25; i++)
		{
			if(objPhilosopher[i].getTable()==idTable) 
					p--;
			if(p==0)
			{
				lastMove = i;
				for(int j = 0; j< 25; j++)
				{
					if(objPhilosopher[j].getTable()==5)
						ids[objPhilosopher[j].position]=-1; 
				}
				
				int k=0,q;
				for(int j = 0; j<5;j++)
				{
					if(ids[j]!=-1)
						ids1[k++] = ids[j]; 
				}
				
				if(k==1)
						q = ids1[0];
				else 	q = ids1[MyRandom.rangeRandom(0, k-1)];

				objPhilosopher[i].changeTable(q);
				stopThreads(idTable);
				break;
			}
		}
		TableFull[idTable] = false;		
	}

	private static void stopThreads(int idTable) {
		for(int i = 0; i< 25; i++)
		{
			if(objPhilosopher[i].getTable()==idTable)
				objPhilosopher[i].stopThread();
		}
	}

	public static boolean tableDeadlock(int idTable)
	{
		boolean deadLock = false;
		
		int count = 0;
		for(int i = 0; i< 25; i++)
		{
			if(objPhilosopher[i].getTable()==idTable 
					&& objPhilosopher[i].getStatus() == PhilosopherState.WAITING_FOR_FORK)
				count ++;
		}
		if(count == 5)
			deadLock = true;
		return deadLock;		
	}
}


