import java.util.HashMap;
import java.util.Map;

public class HashMapLevel2
{
public static void main(String[] args)
{
	HashMap<String, String> capitalCity = new HashMap<>();
	capitalCity.put("India", "New Delhi");
	capitalCity.put("Britain", "London");
	capitalCity.put("America", "Washington D.C.");
	capitalCity.put("Uruguay", "Montevideo");
	capitalCity.put("Nigeria", "Abuja");
	capitalCity.put("Laos", "Vientiane");

	//1. Iterate using entrySet() both key and value
	System.out.println("--- Using entrySet ---");
	for(Map.Entry<String, String> entry : capitalCity.entrySet())
	{
		System.out.println("Country: " + entry.getKey() + " | Capital: " + entry.getValue());
	}

	//2. Iterate using keySet if only key needed
	System.out.println("\n--- Using keySet ---");
	for(String country : capitalCity.keySet())
	{
		System.out.println("Country: " + country);
	}

	//3. Removing element
	capitalCity.remove("Britain");
	System.out.println("\nAfter removal of Britain: " + capitalCity);
}
}