public class CountVowels
{
    public static void main(String[] args)
    {
        String s = "Education";
        int count = 0;
        s = s.toLowerCase();
        for(char c : s.toCharArray())
        {
            if("aeiou".indexOf(c) >= 0)
            {
                count++;
            }
        }
        System.out.println("Number of vowels: " + count);
    }
}
