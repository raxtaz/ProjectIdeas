import java.util.ArrayList;

public class ArrayListL2
{
static void main(String[] args)
{
	ArrayList<Integer> dynamicArray = new ArrayList<>();
	dynamicArray.add(1);
	dynamicArray.add(2);
	dynamicArray.add(3);

	dynamicArray.add(4);
	System.out.println("After append: " + dynamicArray);

	dynamicArray.add(1, 99);
	System.out.println("After inserting 99 at index #1: " + dynamicArray);

	dynamicArray.remove(3);
	dynamicArray.remove(Integer.valueOf(2));
	System.out.println("After removing 2:  " + dynamicArray);
}
}
