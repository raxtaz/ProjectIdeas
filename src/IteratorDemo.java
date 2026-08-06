import java.util.ArrayList;
import java.util.Iterator;

public class IteratorDemo
{
	public void main(String[] args)
	{
		ArrayList<String> fruits = new ArrayList<>();
		fruits.add("Apple");
		fruits.add("Banana");
		fruits.add("Orange");

		System.out.println("\n1. Using the Iterable property (For-Each Loop)");
		for(String fruit : fruits)
		{
			System.out.println(fruit);
		}

		System.out.println("\n2. Using Iterator object manually.");

		Iterator<String> tool = fruits.iterator();

		while(tool.hasNext())
		{
			String fruit = tool.next();
			System.out.println(fruit);
		}
	}
}