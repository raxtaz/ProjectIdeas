import java.util.Scanner;

public class SimpleCalculator
{
	static void main(String[] args)
	{
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter first number: ");
		double num1 = scanner.nextDouble();
		System.out.println("Enter second number: ");
		double num2 = scanner.nextDouble();
		System.out.println("Enter operation (+,-,*,/): ");
		char operation = scanner.next().charAt(0);
		double result = switch(operation)
		{
			case '+' -> num1 + num2;
			case '-' -> num1 - num2;
			case '*' -> num1 * num2;
			case '/' -> num1 / num2;
			default -> throw new IllegalArgumentException("Invalid operation");
		};

		System.out.println("The result is: " + result);
	}
}
