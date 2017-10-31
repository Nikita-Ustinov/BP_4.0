
package nn.autoencoders;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

public class Database implements Serializable
{
    int length;
    LinkedList<Person> persons = new LinkedList<Person>();
    
    Database() {
        Person p = new Person();
    }
    
    public void addPeople(int number, double[] features) {
        Person p = new Person(number, features);
        persons.push(p);
        length++;
    }
    
    public void printDatabase() {
        for(int i=0; i<persons.size(); i++) {
            System.out.print(persons.get(i).Name+"  features[0..5]: ");
            for(int j=0; j<10; j++) {
                System.out.print(persons.get(i).Features[j]+",");
                System.out.println("");
            }
        }
    }
    
    public String determinePerson(double[] features) {
        double[] distance = new double[length];
        double min = 9999;
        int minNumber = -1;
        for(int i=0; i<persons.size(); i++) {
            distance[i] = difference(features, persons.get(i).Features);
            if(min>distance[i]){
                min = distance[i];
                minNumber = i;
            }
        }
        distance = sort(distance);
        String name = persons.get(minNumber).Name;
        return name;
    }
    
    double[] sort(double[] input) {
        double templ;
        for(int i=0; i<input.length; i++) {
            for(int j=i; j<input.length; j++) {
                if(input[i]>input[j]){
                    templ = input[j];
                    input[j] = input[i];
                    input[i] = templ;
                }
            }
        }
        return input;
    }
    
    static void print(double[] d) {
        for(int i=0; i<d.length; i++) {
              System.out.println(d[i]);
        }
    }
    
    
    double difference(double[] mainFeatures, double[] features) {
        double difference = 0;
        for(int i=0; i<features.length; i++) {
            if(features[i] > mainFeatures[i]){
                difference = features[i] - mainFeatures[i];
            }
            else {
                difference = mainFeatures[i] - features[i];
            }
        }
        return difference;
    }
    
    public void serializace() throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream("database/d1.out"); 
        ObjectOutputStream oos = new ObjectOutputStream(fos); 
        oos.writeObject(this); 
        oos.close(); 
    }
    
    public Database deserializace() throws Exception {
        Database d = null;
        try { 
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("database/d1.out")); 
            d = (Database) in.readObject();
        }catch(Exception e) {
            throw new Exception("Nepovedlo se desearilizovat database");
        }
        return d;
    }
    
    
}
