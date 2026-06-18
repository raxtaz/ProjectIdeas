import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HashMapLevel3
{
static void main(String[] args)
{
	HashMap<String, Integer> scores = new HashMap<>();
	scores.put("Alice", 90);

	//1. getOrDefault: Provide a fallback value
	int bobScore = scores.getOrDefault("Bob", 0);
	System.out.println("Bob's Score (Default): " + bobScore);

	//2. putIfAbsent: Only adds if the key isn't already there
	scores.putIfAbsent("Alice", 100); // Won't change Alice coz already exists
	scores.putIfAbsent("Charlie", 85); // Will add Charlie

	//3. computeIfAbsent
	HashMap<String, List<String>> structure = new HashMap<>();
	//Automatically creates the ArrayList if "DevTeam" doesn't exist
	structure.computeIfAbsent("DevTeam", k -> new ArrayList<>()).add("Alex");
	structure.computeIfAbsent("DevTeam", k -> new ArrayList<>()).add("Blake");

	System.out.println("Teams: " + structure);
}
}