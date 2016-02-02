import java.util.ArrayList;
public class PetersonTree 
{
	private int threads;																								//number of threads
	private PetersonLock [] petersonTree;																				//lock tree
	private Thread [] threadsArray;																						//array storing threads		
	private int lockInstancePerThread;																					//number of lock each threads acquire	
	
	private static ThreadLocal<ArrayList<Integer>> myThreadLocal =new ThreadLocal<ArrayList<Integer>>()					//each thread's lock acquiring pathway
	{
		@Override 
		public ArrayList<Integer> initialValue() 
		{
		     return new ArrayList<Integer>();
		}
	};
	
	/*initialize the lock tree*/
	PetersonTree( int amountOfThreads)
	{		
		this.threads =  amountOfThreads;																		//ie. 8
		this.petersonTree = new PetersonLock[threads];															//petersonTree, length 8 for locks(index 0 is not used)
		int pLength = petersonTree.length;
		this.lockInstancePerThread = (int) Math.ceil(((Math.log(threads))/Math.log(2)));						//3-each thread acquire 3 locks
		for(int i = 1; i < pLength ; i++)
		{
			petersonTree [i] = new PetersonLock();																//install locks					
		}	
	}
	
	/*Thread's lock acquiring method*/
	public void PTreeLock()
	{
		ArrayList<Integer> lockPath = myThreadLocal.get();														//get the thread local lock path
		int i = Integer.valueOf(Thread.currentThread().getName());												//initial i is the thread's name		
		int acquireLock = i/2;																					//the target lock i am acquiring
		
		while(acquireLock >= 1 )																				//keep acquire lock until acquire lock is 1(the root)	
		{			
			petersonTree[acquireLock].lock(i);																	//acquire
			lockPath.add(acquireLock);																			//add the lock to lock path 				
			i = acquireLock % 2;																				//new i determine the next lock acquirong 
			acquireLock /= 2;																					//my new target		
		}		
	}
	
	public void PTreeUnlock()
	{
		ArrayList<Integer> lockPath = myThreadLocal.get();														//get the thread lockPath
		
		int i = Integer.valueOf(Thread.currentThread().getName());												//the name(id) of the thread
		int LockPathIn = lockInstancePerThread -1;																//the last index of the lockPath, which stored number 1
	
		while(LockPathIn>=0)																					//if not yet release the first lock acquired
		{
			int unlockedLock = lockPath.get(LockPathIn);														//final lock released, all locked released, 1 request is finished
			if(LockPathIn == 0)
			{
				petersonTree[unlockedLock].unLock( i % 2 );														 
				break;
			}
			else																								//keep finding locks need to be released.
			{
				petersonTree[unlockedLock].unLock( (lockPath.get(LockPathIn-1))%2 ); 
				LockPathIn--;				
			}			
		}
	}
}
