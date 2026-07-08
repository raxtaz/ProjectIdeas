import java.util.Map;
import java.util.WeakHashMap;

public class SimpleWeakHashMap
{
	static void main(String[] args)
	{
		//Create map
		Map<Object, String> map = new WeakHashMap<>();

		//Create an object as key
		Object key = new Object();

		//Put in map
		map.put(key, "Hidden Treasure");
		System.out.println("1. Map before GC: " + map);

		//Destroy only reference to key
		key = null;

		//Clean JVM memory
		System.gc();

		//Check map again
		System.out.println("2. Map after GC: " + map);
	}
}
