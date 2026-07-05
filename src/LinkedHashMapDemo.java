import java.util.LinkedHashMap;
import java.util.Map;

public class LinkedHashMapDemo
{
	static void main(String[] args)
	{
		LinkedHashMap<String, Integer> linkedHashMap = new LinkedHashMap<>(11, 0.8f, true); //double linked list
		linkedHashMap.put("Orange", 10);
		linkedHashMap.put("Apple", 20);
		linkedHashMap.put("Guava", 13);
		for(Map.Entry<String, Integer> entry : linkedHashMap.entrySet())
		{
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
	}
}
