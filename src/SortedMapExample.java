import java.util.SortedMap;
import java.util.TreeMap;

public class SortedMapExample
{
	static void main(String[] args)
	{
		SortedMap<Integer, String> students = new TreeMap<>();

		students.put(104, "Joffin");
		students.put(101, "Monica");
		students.put(103, "Janish");
		students.put(102, "Dravid");

		System.out.println("Sorted Map:");
		System.out.println(students);
	}
}
