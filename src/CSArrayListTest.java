import java.util.Collection;
import java.util.Iterator;
import java.util.AbstractList;
import java.util.Arrays;

public class CSArrayListTest {
    public static void main(String[] args) {
        Collection<String> testCollection = new CSArrayList<>();
        testCollection.add("A");
        testCollection.add("B");

        Iterator<String> iterator = testCollection.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
        System.out.println(testCollection.size());
        System.out.println(testCollection.contains("B"));
        System.out.println(((CSArrayList<String>) testCollection).indexOf("B"));



    }

}
