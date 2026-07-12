public class CountCharacter
{
	static void main(String[] args)
	{
		String str = "Artificial Intelligence Company";
		char ch = 'a';
		int count = 0;

		for(char c : str.toCharArray())
		{
			// Convert both to lowercase before comparing
			if(Character.toLowerCase(c) == Character.toLowerCase(ch))
			{
				count++;
			}
		}

		System.out.println("The character '" + ch + "' (case-insensitive) occurs: " + count + " time(s).");
	}
}