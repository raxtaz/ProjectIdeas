public class LargestInArray
{
public static void main(String[] args)
{
	int[] arr = {1, 3, 11, 7, 9};
	int largest = arr[0];
	for(int num : arr)
	{
		if(num > largest)
		{
			largest = num;
		}
	}
	System.out.println(largest);
}
}