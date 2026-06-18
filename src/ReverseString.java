public class ReverseString
{
static void main(String[] args)
{
	String str = "Ram";
	StringBuilder reversed = new StringBuilder();

	// Brute force approach: iterate from end to beginning
	for(int i = str.length() - 1; i >= 0; i--)
	{
		reversed.append(str.charAt(i));
	}

	System.out.println("Original: " + str);
	System.out.println("Reversed (Brute Force): " + reversed);
}
}