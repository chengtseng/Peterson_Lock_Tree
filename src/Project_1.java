import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;
import java.util.Scanner;
/*read a configuration .txt file to set up the testing  environment*/
public class Project_1 
{
	 private static int THREADS;	
	 private static int EACHTHREAD;	 
	 private static int scale;	
	 private static Thread[] thread;
	 private static Random random = new Random();
	 private PetersonTree instance;	 
	 private static Account MyAccount = new Account();
	 private static String inputFileName;	
	 private int sum = 0;	 
	 
	 public static void main(String[] args) 
	 {
		 Project_1 assignment = new  Project_1();
		 
		 try 
	     {
			assignment.concurrent();
	     }
	      
		 catch (Exception e) {} 
		  
		 finally
		 {
			MyAccount.setBalance(0);				  
		 }		 
	  }
	 
	 public void parse( String file)
	 {
		 try(BufferedReader bufReader = new BufferedReader( new FileReader(inputFileName));)
		 {			
			String str = bufReader.readLine();			
			int row = 0;
			
			while(str != null)
			{	
				String [] info = str.trim().split("\\s+");
										
				if(info.length != 2 )
				{
					throw new FormatErrorException();
				}else
				{					
					if (row == 0 && Integer.parseInt(info[1])%2 == 0)
					{
						THREADS = Integer.parseInt(info[1]);						
					}					
					else if (row == 1 )
					{
						EACHTHREAD = Integer.parseInt(info[1]);						
					}
					else if (row == 2 )
					{
						scale = Integer.parseInt(info[1]);						
					}					
				}
				str = bufReader.readLine();
				row++;
			}				
		 }
		 catch(Exception e)
		 {
			 
		 }		 
	 }
	 public void concurrent() throws Exception 
	 {
	     System.out.println("Please enter your .txt configuration file with number of threads and money to deposit:"); 
	     Scanner input = new Scanner(System.in);
	     this.inputFileName = input.nextLine();
	     input.close();
		 this.parse(inputFileName);
		 thread = new Thread[2*THREADS];
		 instance = new PetersonTree(THREADS);
		 System.out.println( "Start:............. ");
		 long start = System.currentTimeMillis();		
	     
	      for (int i = THREADS ; i < thread.length; i++) 
	      {
	    	  thread[i] = new MyThread();
	    	  thread[i].setName(String.valueOf(i));	    	  
	      }
	      for (int i = THREADS ; i < thread.length; i++) 
	      {	    	  
	    	  thread[i].start();
	      }
	      for (int i = THREADS ; i < thread.length; i++) 
	      {
	    	  thread[i].join();
	      }	      
	      if (MyAccount.getBalance() != EACHTHREAD*THREADS) 
	      {
	    	  System.out.println("Wrong result! " + MyAccount.getBalance() + " " + EACHTHREAD*THREADS);
	      }
	      else if(MyAccount.getBalance() == EACHTHREAD*THREADS)
	      {
	    	  System.out.println( "Sucess, the account balance is ........................"+MyAccount.getBalance());
	    	  long end = System.currentTimeMillis();
	    	  long total = end - start;
	    	  double totalInSecond = total / 1000.0;
	    	  double throughput = (EACHTHREAD*THREADS)/totalInSecond;
	    	  System.out.println("total time: "+total+" milliseconds"+"		throughput is "+ throughput+ " request/ per sec");	    	  
	      }	      
	  }
	 
	 class MyThread extends Thread 
	 {
		 public void run()
		 {
		      for (int i = 0; i < EACHTHREAD ; i++) 
		      {		    	 
		    	  instance.PTreeLock();
			    try 
			    {		      
			    	MyAccount.deposit();			     
			    } 
			    finally 
			    {			    	
			      instance. PTreeUnlock();
			      try 
			      {
			    	int sleepTime = (int) (-scale * Math.log(random.nextDouble()));					
			    	Thread.sleep(sleepTime);				
			      } 
			      catch (InterruptedException e) 
			      {					
					e.printStackTrace();
			      }
			    }
		      }
		  }  	    
		   
	 }
	 private class FormatErrorException extends Exception
	 {
			
	 }
}

class Account 
{
	private int balance = 0;
	
	Account(){}
	
	public void deposit()
	{
		balance++;		
	}
	
	public int getBalance()
	{
		return balance;		
	}
	
	public void setBalance(int a)
	{
		this.balance = a;		
	}
}