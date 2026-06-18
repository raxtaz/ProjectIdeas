import java.util.Scanner;

public class SumOfDigits
{
static void main(String[] args)
{
	Scanner scanner = new Scanner(System.in);

	System.out.print("Enter an integer: ");

	// 1. We use nextLong() to grab a massive integer directly
	long num = scanner.nextLong();
	long sum = 0;

	// This handles negative numbers by turning them positive
	num = Math.abs(num);

	// 2. Use a 'while' loop to keep going until the number hits 0
	while(num > 0)
	{
		long lastDigit = num % 10; // Pluck off the last digit (e.g., 456 -> 6)
		sum += lastDigit;          // Add that digit to our running total
		num /= 10;                 // Chop off the last digit (e.g., 456 -> 45)
	}

	System.out.println("The sum of the digits is: " + sum);

	// It's good practice to close your scanner when done!
	scanner.close();
}
}