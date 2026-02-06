public class Factorial
{
    public static void main(String[] args)
    {
        int n = 5; // Change this value to compute factorial for different numbers
        long fact = 1;
        if(n < 0)
        {
            System.out.println("Factorial is not defined for negative numbers.");
        }
        else
        {
            for(int i = 1; i <= n; i++)
            {
                fact *= i;
            }
            System.out.println("Factorial of " + n + " is: " + fact);
        }
    }
}