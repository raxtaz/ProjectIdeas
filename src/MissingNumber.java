import java.util.Arrays;

public class MissingNumber
{
public static void main(String[] args)
{
	int[] arr = {1, 2, 4, 5, 8, 10};

	System.out.print("Missing numbers: ");

	// Iterate through the array (assuming it is sorted)
	for(int i = 0; i < arr.length - 1; i++)
	{
		// Check if the gap between current number and next number is greater than 1
		int current = arr[i];
		int next = arr[i + 1];

		// Print all missing numbers in the gap
		while(current + 1 < next)
		{
			current++;
			System.out.print(current + " ");
		}
	}
	System.out.println("\nFull Array: " + Arrays.toString(arr));
}
}