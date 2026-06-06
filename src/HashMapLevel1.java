import java.util.HashMap;

public class HashMapLevel1
{
public static void main(String[] args)
{
	//1. Create a Hashmap (key:value pair)
	HashMap<String, Integer> fruitInventory = new HashMap<>();

	//2. Put elements into map using put
	fruitInventory.put("Apple", 50);
	fruitInventory.put("Banana", 120);
	fruitInventory.put("Orange", 75);

	//3. Retrieve a value using key
	int appleCount = fruitInventory.get("Apple");
	System.out.println("Quantity of Apples: " + appleCount);

	//4. Check if a key exist using containKey
	if(fruitInventory.containsKey("Mango"))
	{
		System.out.println("Mango count: " + fruitInventory.get("Mango"));
	}
	else
	{
		System.out.println("Mango is not in inventory.");
	}

	//Print whole map
	System.out.println("Full Inventory: " + fruitInventory);
}
}