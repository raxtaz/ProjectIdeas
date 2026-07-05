import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleHashMapExample
{
	static void main(String[] args)
	{
		LinkedHashMap<String, Integer> fruitInventory = new LinkedHashMap<>();

		fruitInventory.put("Apple", 55);
		fruitInventory.put("Banana", 40);
		fruitInventory.put("Pear", 30);
		fruitInventory.put("Orange", 20);
		fruitInventory.put("Coconut", 25);

		System.out.println("\nFull Inventory: " + fruitInventory);
		System.out.println(fruitInventory.get("Pear") + " Pear in inventory.");
		System.out.println("\n----- Looping through the inventory -----");
		for(Map.Entry<String, Integer> pooriList : fruitInventory.entrySet())
		{
			System.out.println("Fruit name: " + pooriList.getKey() + " & Quantity: " + pooriList.getValue());
		}
		fruitInventory.remove("Pear");
		System.out.println("\nFull Inventory after removing Pear: " + fruitInventory);
	}
}
