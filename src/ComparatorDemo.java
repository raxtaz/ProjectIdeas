import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

record Employee(int id, String name, double salary)
{


	@Override
	public String toString()
	{
		return String.format("%d %s %.2f", id, name, salary);
	}
}

public class ComparatorDemo
{
	static void main(String[] args)
	{
		List<Employee> employees = new ArrayList<>(List.of(
			new Employee(101, "Raghav", 50000),
			new Employee(102, "Shivank", 225000),
			new Employee(103, "Anusha", 100000),
			new Employee(104, "Sunaina", 35000)
		));

		// Sort by id descending
		employees.sort(Comparator.comparingInt(Employee:: id).reversed());

		employees.forEach(System.out::println);
	}
}