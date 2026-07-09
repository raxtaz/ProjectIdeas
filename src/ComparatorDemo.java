import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class Employee
{
	int id;
	String name;
	double salary;

	Employee(int id, String name, double salary)
	{
		this.id = id;
		this.name = name;
		this.salary = salary;
	}

	@Override
	public String toString()
	{
		return id + " " + name + " " + salary;
	}
}

public class ComparatorDemo
{
	static void main(String[] args)
	{
		List<Employee> employees = new ArrayList<>();

		employees.add(new Employee(101, "Raghav", 50000));
		employees.add(new Employee(102, "Shivank", 225000));
		employees.add(new Employee(103, "Anusha", 100000));
		employees.add(new Employee(104, "Sunaina", 35000));

		employees.sort(Comparator.comparingDouble((Employee emp) -> emp.salary).reversed());

		System.out.println(employees);
	}
}