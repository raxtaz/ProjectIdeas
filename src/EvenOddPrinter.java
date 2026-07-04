public class EvenOddPrinter
{
	private static int counter = 1;
	private static final int MAX_NUM = 10;
	private static final Object lock = new Object();

	static void main(String[] args)
	{
		Thread oddThread = new Thread(() ->
		{
			while(counter <= MAX_NUM)
			{
				synchronized(lock)
				{
					if(counter % 2 != 0)
					{
						System.out.println(Thread.currentThread().getName() + ": " + counter);
						counter++;
						lock.notify(); // Wake up the even thread
					}
					else
					{
						try
						{
							lock.wait();
						}
						catch(InterruptedException e)
						{
						}
					}
				}
			}
		}, "OddThread");

		Thread evenThread = new Thread(() ->
		{
			while(counter <= MAX_NUM)
			{
				synchronized(lock)
				{
					if(counter % 2 == 0)
					{
						System.out.println(Thread.currentThread().getName() + ": " + counter);
						counter++;
						lock.notify(); // Wake up the odd thread
					}
					else
					{
						try
						{
							lock.wait();
						}
						catch(InterruptedException e)
						{
						}
					}
				}
			}
		}, "EvenThread");

		oddThread.start();
		evenThread.start();
	}
}