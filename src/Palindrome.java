public class Palindrome
{
	static void main(String[] args)
	{
		String str = "maDam";
		str = str.toLowerCase();
		String reversed = new StringBuilder(str).reverse().toString();
		System.out.println(str.equals(reversed));
	}
}