public class SimpleHashTable
{

	String[] table;

	public SimpleHashTable(int size)
	{
		table = new String[size];
	}

	// Hash Function
	public int hash(int key)
	{
		return key % table.length;
	}

	// Insert
	public void put(int key, String value)
	{
		int index = hash(key);
		table[index] = value;
	}

	// Search
	public String get(int key)
	{
		int index = hash(key);
		return table[index];
	}

	// Display Table
	public void display()
	{
		System.out.println("\nHash Table:");

		for(int i = 0; i < table.length; i++)
		{
			System.out.println(i + " -> " + table[i]);
		}
	}

	public static void main(String[] args)
	{

		SimpleHashTable ht = new SimpleHashTable(10);

		ht.put(11, "Apple"); //replaced by Kiwi due to delibrate collision
		ht.put(25, "Banana");
		ht.put(37, "Mango");

		ht.put(21, "Kiwi"); //created collision to be replaced

		ht.display();

		System.out.println("\nSearch Key 25 : " + ht.get(25));
	}
}