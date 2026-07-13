import java.util.SortedMap;
import java.util.TreeMap;

public class SortedMapPractice
{
	static void main(String[] args)
	{
		//1. Intitalisation
		SortedMap<Integer, String> students = new TreeMap<>();

		//2. Adding Elements
		students.put(105, "Evelyn");
		students.put(109, "Harry");
		students.put(108, "John");
		students.put(107, "Sally");
		students.put(106, "Julie");

		//3. Printing Elements
		System.out.println("---Entire Sorted Map---");
		System.out.println(students);
		System.out.println();

		//4. Accessing Extreme Elements
		System.out.println("---First & Last Elemets---");
		System.out.println("Lowest Roll No.: " + students.firstKey());
		System.out.println("Highest Roll No.: " + students.lastKey());
		System.out.println();

		//5. Using headMap(toKey)
		System.out.println("--- headMap (Roll no. less than 104)---");
		SortedMap<Integer, String> head = students.headMap(104);
		System.out.println(head);
		System.out.println();

		//6. Using tailMap (fromKey)
		System.out.println("---tailMap (Roll no. 103 & above)---");
		SortedMap<Integer, String> tail = students.tailMap(103);
		System.out.println(tail);
		System.out.println();

		//7 .Using subMap (fromKey, toKey)
		System.out.println("---subMap (from Roll no. 102 to 105 (outbound range))---");
		SortedMap<Integer, String> sub = students.subMap(102, 105);
		System.out.println(sub);
		System.out.println();

		//8. Iterating through the SortedMap
		System.out.println("---Iterating through the Map---");
		for(SortedMap.Entry<Integer, String> entry : students.entrySet())
		{
			System.out.println("Roll No.: " + entry.getKey() + " | Name: " + entry.getValue());
		}
	}
}
