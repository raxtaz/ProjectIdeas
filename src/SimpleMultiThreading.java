// 1. Create a class that extends Thread
class MyTask extends Thread
{

	// 2. Put the task inside the run() method
	@Override
	public void run()
	{
		for(int i = 1; i <= 3; i++)
		{
			System.out.println(Thread.currentThread().getName() + " is running step " + i);
		}
	}
}

public class SimpleMultiThreading
{
	static void main(String[] args)
	{
		System.out.println("Main program started!");

		// 3. Create two separate workers (threads)
		MyTask worker1 = new MyTask();
		MyTask worker2 = new MyTask();

		// Give them names so we can tell them apart in the output
		worker1.setName("Worker A");
		worker2.setName("Worker B");

		// 4. Start the threads!
		worker1.start();
		worker2.start();

		System.out.println("Main program finished!");
	}
}