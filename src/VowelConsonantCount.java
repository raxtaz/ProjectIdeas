public class VowelConsonantCount
{
	static void main(String[] args)
	{
		String str = "Vowels gin ne ki ninja technique";
		int vowels = 0, consonants = 0;
		for(char c : str.toCharArray())
		{
			if("aeiouAEIOU".indexOf(c) != -1)
			{
				vowels++;
			}
			else if(Character.isLetter(c))
			{
				consonants++;
			}
		}
		System.out.println("Vowels: " + vowels + " Consonants: " + consonants);
	}
}