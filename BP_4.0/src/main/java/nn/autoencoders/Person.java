
package nn.autoencoders;

import java.io.Serializable;
import java.util.HashMap;

public class Person implements Serializable{
    static HashMap<Integer, String> names = new HashMap<Integer, String>();
    String Name;
    int Number;
    double[] Features;

    
    Person() {
        names.put(0, "Ben Kingsli");        names.put(1, "Barianna Hildebrand");        names.put(2, "Garry Oldman");        names.put(3, "Gordon Levit"); 
        names.put(4, "Jake Jillenhol");        names.put(5, "Jason Bejtman");        names.put(6, "Jenifer Lourenc");        names.put(7, "Jef Bridjes");       
        names.put(8, "Jina Karnao");        names.put(9, "Jovani Ribzi");        names.put(10, "Juliana Mur");       
//        names.put(11, "");
//        names.put(12, "");        names.put(13, "");        names.put(14, "");        names.put(15, "");
//        names.put(16, "");        names.put(17, "");        names.put(18, "");
//        names.put(19, "");        names.put(20, "");
//        
        
    }
    
    Person(int number, double[] features) {
        Number = number;
        Features = new double[Neuronet.tretiVrstva];
        System.arraycopy(features, 0, Features, 0, Neuronet.tretiVrstva);
        Name = names.get(number);
    }
    
    
}
