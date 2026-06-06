import java.util.ArrayList;

public class ArrayListDemo {
    static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(20);
        list.add(95);
        System.out.println(list.get(2));
        System.out.println(list.get(0));

        for(Integer i : list) {
            System.out.println(i);
        }
        System.out.println(list.contains(95));
    }
}