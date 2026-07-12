import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class LinkedListL2
{

	static void main(String[] args)
	{

		List<String> students = new ArrayList<>();
		Scanner scanner = new Scanner(System.in);

		// 1. Accept user input dynamically
		System.out.println("Enter Student names (type 'stop' to finish):");

		while(true)
		{
			String input = scanner.nextLine().trim();

			if(input.equalsIgnoreCase("stop"))
			{
				break;
			}

			students.add(input);
		}

		// 2. Sort the list alphabetically
		Collections.sort(students);
		System.out.println("\nSorted List: " + students);

		// 3. Search name (case-sensitive)
		String searchName = "ramRAAJ";

		if(students.contains(searchName))
		{
			System.out.println(searchName + " is in the class.");
		}
		else
		{
			System.out.println(searchName + " is not in the class.");
		}

		// 4. Reverse the list
		Collections.reverse(students);
		System.out.println("Reversed List: " + students);

		scanner.close();
	}
}