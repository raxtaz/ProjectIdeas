public class CollisionDemo
{
	String[] table;

	public CollisionDemo(int size)
	{
		table = new String[size];
	}

	public int hash(int key)
	{
		return key % table.length;
	}

	public void put(int key, String value)
	{
		int index = hash(key);

		System.out.println("Key " + key + " goes to index " + index);
		table[index] = value;
	}

	public void display()
	{
		System.out.println();
		for(int i = 0; i < table.length; i++)
		{
			System.out.println(i + "->" + table[i]);
		}
	}

	static void main(String[] args)
	{
		CollisionDemo ht = new CollisionDemo(10);

		ht.put(11, "Apple");
		ht.put(21, "Banana");

		ht.display();
	}
}
