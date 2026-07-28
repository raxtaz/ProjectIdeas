public class Factorial
{
	static long computeFactorial(int n)
	{
		if(n < 0) throw new IllegalArgumentException("Factorial is not defined for negative numbers.");
		if(n == 0 || n == 1) return 1;

		long fact = 1;
		for(int i = 2; i <= n; i++)
		{
			fact *= i;
		}
		return fact;
	}

	public static void main(String[] args)
	{
		int n = args.length > 0 ? Integer.parseInt(args[0]) : 5;

		try
		{
			long result = computeFactorial(n);
			System.out.println(String.format("Factorial of %d is: %d", n, result));
		}
		catch(IllegalArgumentException e)
		{
			System.out.println(e.getMessage());
		}
	}
}