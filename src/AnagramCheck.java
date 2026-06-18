import java.util.Arrays;

public class AnagramCheck
{
static void main(String[] args)
{
	String str1 = "TomMarvoloRiddle", str2 = "IamLordVoldemort";
	char[] arr1 = str1.toCharArray();
	char[] arr2 = str2.toCharArray();
	Arrays.sort(arr1);
	Arrays.sort(arr2);
	System.out.println(Arrays.equals(arr1, arr2));
}
}
