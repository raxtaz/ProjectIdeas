public class InsertionSortDemo
{
public static void insertionSort(int[] arr)
{
	for(int i = 1; i < arr.length; i++)
	{
		int key = arr[i];
		int j = i - 1;

		while(j >= 0 && arr[j] > key)
		{
			arr[j + 1] = arr[j];
			j--;
		}
		arr[j + 1] = key;
	}
}

static void main(String[] args)
{
	int[] arr = {3, 4, 5, 6, 7, 8, 9, 10};
	insertionSort(arr);

	for(int j : arr)
	{
		System.out.println(j);
	}
}
}
