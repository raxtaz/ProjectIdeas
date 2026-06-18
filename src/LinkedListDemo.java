import java.util.LinkedList;

public class LinkedListDemo
{
static void main(String[] args)
{
	// #1. Create a linked list of strings
	LinkedList<String> cars = new LinkedList<>();

	// #2. Add elements to the end of the list
	cars.add("Volvo");
	cars.add("Ford");
	cars.add("BMW");
	cars.add("Mercedes");
	cars.add("GWagon");
	System.out.println("Initial List: " + cars);

	// #3. Add elements to specific nodes
	cars.addFirst("Tesla");     //Adds in beginning
	cars.addLast("Mazda");      //Adds in end
	cars.add(2, "Toyota");  //Inserts at index #2
	System.out.println("After Additions: " + cars);

	// #4. Remove elements
	cars.remove("BMW");     // Removes by value
	cars.remove(3);     // Removes by index
	cars.removeFirst();     // Removes by first item
	System.out.println("After Removals: " + cars);

	// #5. Iterate through list
	System.out.println("\nIterating over the list:");
	for(String car : cars)
	{
		System.out.println("- " + car);
	}
}
}