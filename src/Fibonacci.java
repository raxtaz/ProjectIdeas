public class Fibonacci
{
	static void main(String[] args)
	{
		int n = 14, num1 = 0, num2 = 1;

		// Use print() to keep the output on a single line
		System.out.print("Fibonacci Series: " + num1 + ", " + num2);

		for(int i = 2; i < n; i++)
		{
			int num3 = num1 + num2;
			System.out.print(", " + num3); // Changed println to print

			// Shift values for the next iteration
			num1 = num2;
			num2 = num3;
		}

		// Add a final newline to keep the terminal prompt clean
		System.out.println();
	}
}