public class Palindrome
{
    public static void main(String[] args)
    {
        StringBuilder sb = new StringBuilder("madam");
        String str = sb.toString();
        String rev = new StringBuilder(str).reverse().toString();
        if (str.compareTo(rev) == 0)
        {
            System.out.println("String is palindrome");
        }
        else
        {
            System.out.println("String is not palindrome");
        }
    }
}