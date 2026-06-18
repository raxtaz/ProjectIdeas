public class MergeSortDemo
{

public static void mergeSort(int[] arr)
{

	if(arr.length < 2)
	{
		return;
	}

	int mid = arr.length / 2;

	int[] left = new int[mid];
	int[] right = new int[arr.length - mid];

	System.arraycopy(arr, 0, left, 0, mid);

	if(arr.length - mid >= 0)
	{
		System.arraycopy(arr, mid, right, 0, arr.length - mid);
	}

	mergeSort(left);
	mergeSort(right);

	merge(arr, left, right);
}

public static void merge(int[] arr,
                         int[] left,
                         int[] right)
{

	int i = 0;
	int j = 0;
	int k = 0;

	while(i < left.length && j < right.length)
	{

		if(left[i] <= right[j])
		{
			arr[k++] = left[i++];
		}
		else
		{
			arr[k++] = right[j++];
		}
	}

	while(i < left.length)
	{
		arr[k++] = left[i++];
	}

	while(j < right.length)
	{
		arr[k++] = right[j++];
	}
}

static void main(String[] args)
{

	int[] arr = {8, 3, 5, 4, 7, 6, 1, 2};

	mergeSort(arr);

	for(int num : arr)
	{
		System.out.print(num + " ");
	}
}
}