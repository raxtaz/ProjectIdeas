public class ReverseString
{
	static void main(String[] args)
	{
		String str = "Rishabh";

		// 1. Original Brute Force Approach
		StringBuilder reversed = new StringBuilder();

		for(int i = str.length() - 1; i >= 0; i--)
		{
			reversed.append(str.charAt(i));
		}

		System.out.println("Original: " + str);
		System.out.println("Reversed (Brute Force): " + reversed);

		// 2. The Algorithmic Optimized Approach (Two Pointers)
		// This is optimal for interviews. It swaps characters in-place
		// within an array, cutting the iterations in half (N/2).

		char[] charArray = str.toCharArray();
		int left = 0;
		int right = charArray.length - 1;

		while(left < right)
		{
			// Swap characters
			char temp = charArray[left];
			charArray[left] = charArray[right];
			charArray[right] = temp;

			// Move pointers towards the center
			left++;
			right--;
		}
		String reversedTwoPointer = new String(charArray);
		System.out.println("Reversed (Two-Pointer): " + reversedTwoPointer);

		// 3. The Production Optimized Approach (Built-in)
		// This is the best way to do it in real-world Java development.
		// It relies on highly optimized, underlying Java source code.

		String reversedBuiltIn = new StringBuilder(str).reverse().toString();
		System.out.println("Reversed (Built-in): " + reversedBuiltIn);
	}
}