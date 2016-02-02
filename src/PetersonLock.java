public class PetersonLock 
{	
	int lockNum;
	int twoThreads = 2;										//number of threads a lock dealing				
	volatile boolean [] flag = new boolean[2];				//the boolean flag
	volatile int victim;									// the victim

	public PetersonLock()
	{
		for(int i = 0; i < flag.length; ++i)
		{		
			flag[i]=false;		
		}
	}

	public void lock( int id ) 
	{
		int i = id % 2;
		int j = 1 - i;
		flag[i] = true;
		victim = i;
		
		while(flag[j]==true && victim == i){};		
	}
	
	public void unLock( int id ) 
	{
		flag[id % 2] =false;		
	}
}
