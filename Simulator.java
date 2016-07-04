package assognment3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

/**
 * The simulator class that execute the simulator
 * @author mmuhasan
 *
 */
public class Simulator {
	
		   ExecutorService 	pool 			= Executors.newFixedThreadPool(25); // thread executor pool to run philosopher threads.
	static boolean[] 		TableFull 		= {true,true,true,true,true};    // status of the tables.
	static int 				position6 		= 0;	
	static Spoon[][] 		objSpoons;	// spoon objects
	static Philosopher[] 	objPhilosopher; // philosophier objects
	static Phaser 			simulationGuard; // phaser
	private static int 		lastMove;	
		   
	// Initiating the simulator
	public Simulator() 
	{
		simulationGuard = new Phaser();
		
		simulationGuard.register();
		objPhilosopher = new Philosopher[25];
		
		/** creates 5 spoons for each of the six tables **/
		objSpoons = new Spoon[6][5];
		for(int i =0;i<6;i++)
			for(int j=0;j<5;j++)
				objSpoons[i][j] = new Spoon(i*5+j);
		
		/** create 25 philosophers and put them in the first 5 tables.
		 * One thread per philosopher will start running during this 
		 * initialization process, however, they will wait untill all the 
		 * threads starts. It will be done by the phaser. 
		 */
		for(int i = 0; i < 25; i ++)
			objPhilosopher[i] = new Philosopher(i,simulationGuard,pool);
	}

	public static void main(String[] args) 
	{
		Simulator objSimulator = new Simulator();

		simulationGuard.arriveAndAwaitAdvance(); // wait for all other threads to be initialized.
		
		int simulatorTime = 0; // the simulated clock, represent seconds.
		while(tableDeadlock(5)!=true) // the simulation stops when table 5 has a dead lock
		{			
			simulationGuard.arriveAndAwaitAdvance();// wait all other thread to finish what they 
													// suppose to do for any given simulated second
			/**
			 * check if any table reach to deadlock. if so, pick one philosopher from that table and 
			 * put them in table 6. Then it stops all other philosopher thread of that table since they
			 * are not require by the simulator for rest of the process
			 **/
			for(int i = 0; i<5;i++)
				if(TableFull[i] && tableDeadlock(i)==true)
					changeTable(i);
			simulationGuard.arriveAndAwaitAdvance(); /** let other threads to proceed. **/
			
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
	
	/** 
	 * 
	 * @param idTable : the table id
	 * Pick one philosophier from the table and 
	 * place him/her to sixth table.
	 * 
	 */
	private static void changeTable(int idTable) {
		int p = MyRandom.randomPholosopher(); // take a random philosopher to move to table six
		int[] ids =  {0,1,2,3,4};
		int[] ids1 = {0,0,0,0,0};
		int i;
		for(i = 0; i< 25; i++)
		{
			if(objPhilosopher[i].getTable()==idTable) 
					p--;
			if(p==0)
			{
				lastMove = i; // the philosopher to move to table six

				// find the empth positions of the table six
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
				
				/** find a random empty position at table six **/
				if(k==1)
						q = ids1[0];
				else 	q = ids1[MyRandom.rangeRandom(0, k-1)];

				/** move the philosophier to table six **/
				objPhilosopher[i].changeTable(q);
				
				/** Stop the other threads of that table **/
				stopThreads(idTable);
				break;
			}
		}
		TableFull[idTable] = false;		
	}

	/** find the philosophers of a table and stop the threads **/
	private static void stopThreads(int idTable) {
		for(int i = 0; i< 25; i++)
		{
			if(objPhilosopher[i].getTable()==idTable)
				objPhilosopher[i].stopThread();
		}
	}

	/** find if a table reach deadlock */
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
