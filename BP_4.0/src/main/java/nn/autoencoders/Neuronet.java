package nn.autoencoders;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public final class Neuronet implements Serializable
{	
    public List l0, l1, l2;
    public static int inputLength = 2401;   //625;	//pro fully connection neuronet
    public static int prvniVrstva = 1600;		
    public static int druhaVrstva = 100;		
    public static int tretiVrstva = 100;
    public static double speedLFCN = 0.001;				//rychlost uceni pro "fully connection neuronet"
    public static double speedL1CL = 0.001;				//rychlost uceni pro prvni "convolution layer" 10 az 14
    public static double speedL2CL = 0.001;				//rychlost uceni pro druhy "convolution layer"  5 az 9
    public static double speedL3CL = 0.001;				//rychlost uceni pro treti "convolution layer"  0 az 4
    public static int inputPictureFormat = 40; //px                         //jestli napriklad obrazek 20x20 davame sem 20
    public int inputLengthOwn;
    public int prvniVrstvaOwn;
    public int druhaVrstvaOwn;
    public int tretiVrstvaOwn;
    public ListOfConvolutions convolutions ;
    public static int cisloFiltra = 0;
    public static int numberOfFulters = 28;
    public static int counvolutionGroups = 4;

    public Neuronet()
    {
        addFilter(9,9,3);				//pridani konvoluci 11x11x3 
        addFilter(9,9,3);				//pridani konvoluci 11x11x3 
        addFilter(9,9,3);				//pridani konvoluci 11x11x3 
        addFilter(9,9,3);				//pridani konvoluci 11x11x3 
        addFilter(9,9,3);				//pridani konvoluci 11x11x3 
        addFilter(9,9,3);				//pridani konvoluci 11x11x3 
        addFilter(9,9,3);				//pridani konvoluci 11x11x3 

        addFilter(5,5);					//pridani konvoluci 6x6  
        addFilter(5,5);					//pridani konvoluci 6x6   
        addFilter(5,5);					//pridani konvoluci 6x6   
        addFilter(5,5);					//pridani konvoluci 6x6   
        addFilter(5,5);					//pridani konvoluci 6x6  
        addFilter(5,5);					//pridani konvoluci 6x6   
        addFilter(5,5);					//pridani konvoluci 6x6 
        
        addFilter(3,3);					//pridani konvoluci 3x3  
        addFilter(3,3);					//pridani konvoluci 3x3  
        addFilter(3,3);					//pridani konvoluci 3x3  
        addFilter(3,3);					//pridani konvoluci 3x3  
        addFilter(3,3);					//pridani konvoluci 3x3 
        addFilter(3,3);					//pridani konvoluci 3x3  
        addFilter(3,3);					//pridani konvoluci 3x3 
        
        addFilter(2,2);					//pridani konvoluci 3x3  
        addFilter(2,2);					//pridani konvoluci 3x3  
        addFilter(2,2);					//pridani konvoluci 3x3  
        addFilter(2,2);					//pridani konvoluci 3x3  
        addFilter(2,2);					//pridani konvoluci 3x3  
        addFilter(2,2);					//pridani konvoluci 3x3  
        addFilter(2,2);					//pridani konvoluci 3x3 
        				
        l0= new List(0);				//create first fully connected layer  - prvni vrstva
        l1= new List(1);				//create second fully connected layer - druha vrstva
        l2= new List(2);				//create output fully connected layer - vystupni vrstva

        inputLengthOwn = inputLength;
        prvniVrstvaOwn = prvniVrstva;
        druhaVrstvaOwn = druhaVrstva;
        tretiVrstvaOwn = tretiVrstva;
    }
    

    void addFilter(int size1, int size2, int size3) {
            cisloFiltra=0;
            if(convolutions == null) {
                Convolution newConvolution = new Convolution(size1, size2, size3, cisloFiltra);
                convolutions = new ListOfConvolutions(newConvolution);
//                convolutions.addConvolution(new Convolution(size1, size2, size3, cisloFiltra));
            } 
            else {
                cisloFiltra++;
                Convolution templ = convolutions.head;
                while(templ.next != null) {
                    templ = templ.next;
                    cisloFiltra++;
                }
                convolutions.addConvolution(new Convolution(size1, size2, size3, cisloFiltra));
            }

    }
    
    void addFilter(int size1, int size2) {
        cisloFiltra = 0;
        int countTempl = 0;
        if(convolutions==null) {
            Convolution newConvolution = new Convolution(size1, size2, cisloFiltra);
            convolutions = new ListOfConvolutions(newConvolution);
        } 
        else {
            cisloFiltra++;
            Convolution templ = convolutions.head;
            while(templ.next != null) {
                templ = templ.next;
                cisloFiltra++;
            }
            Convolution newConvolution = new Convolution(size1, size2, cisloFiltra);
            convolutions.addConvolution(newConvolution);
        }

    }
    
//    void serialization() throws IOException {
//        l0.serialization();
//        l1.serialization();
//        l2.serialization();
//        
//        FileOutputStream  fos = new FileOutputStream("serialization files/convolutions"+".out"); 
//        ObjectOutputStream oos = new ObjectOutputStream(fos); 
//        oos.writeObject(convolutions); 
//        oos.close(); 
//
//        String text = "NN struct:" + "\r\n"; 
//        text+= "vstupni vrstva: "+ inputLength+ "\r\n"; 
//        text+= "prvni vrstva: "+ prvniVrstva+ "\r\n"; 
//        text+= "druha vrtstva: "+ druhaVrstva+ "\r\n"; 
//        text+= "treti vrstva: "+ tretiVrstva; 
//        NNAutoencoders.write("Struct.txt", text); 
//    }
//    void deserialization() throws IOException, ClassNotFoundException {
//        l0.deserialization();
//        l1.deserialization();
//        l2.deserialization();
//        
//        ObjectInputStream in = new ObjectInputStream(new FileInputStream("serialization files/convolutions.out")); 
//        in = new ObjectInputStream(new FileInputStream("serialization files/convolutions.out")); 
//        convolutions = (ListOfConvolutions) in.readObject(); 
//    }
    
    void serialization() throws IOException { 
        List l0_1 = l0.subList(0);
        List l0_2 = l0.subList(1);
        
        FileOutputStream fos = new FileOutputStream("serialization files/l0_1"+".out"); 
        ObjectOutputStream oos = new ObjectOutputStream(fos); 
        oos.writeObject(l0_1); 
        oos.close(); 
                
        fos = new FileOutputStream("serialization files/l0_2"+".out"); 
        oos = new ObjectOutputStream(fos); 
        oos.writeObject(l0_2); 
        oos.close(); 

        fos = new FileOutputStream("serialization files/l1"+".out"); 
        oos = new ObjectOutputStream(fos); 
        oos.writeObject(l1); 
        oos.close(); 

        fos = new FileOutputStream("serialization files/l2"+".out"); 
        oos = new ObjectOutputStream(fos); 
        oos.writeObject(l2); 
        oos.close(); 

        fos = new FileOutputStream("serialization files/convolutions"+".out"); 
        oos = new ObjectOutputStream(fos); 
        oos.writeObject(convolutions); 
        oos.close(); 

        String text = "NN struct:" + "\r\n"; 
        text+= "vstupni vrstva: "+ inputLength+ "\r\n"; 
        text+= "prvni vrstva: "+ prvniVrstva+ "\r\n"; 
        text+= "druha vrtstva: "+ druhaVrstva+ "\r\n"; 
        text+= "treti vrstva: "+ tretiVrstva; 
        NNAutoencoders.write("Struct.txt", text); 
        System.out.println("Successful serialization ");  
    } 

    void deserialization() throws Exception { 
        List l0_1 = null;
        List l0_2 = null;
        
        try { 
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("serialization files/l0_1.out")); 
        l0_1 = (List) in.readObject(); 
        
        in = new ObjectInputStream(new FileInputStream("serialization files/l0_2.out")); 
        l0_2 = (List) in.readObject(); 

        in = new ObjectInputStream(new FileInputStream("serialization files/l1.out")); 
        l1 = (List) in.readObject(); 

        in = new ObjectInputStream(new FileInputStream("serialization files/l2.out")); 
        l2 = (List) in.readObject(); 

        in = new ObjectInputStream(new FileInputStream("serialization files/convolutions.out")); 
        convolutions = (ListOfConvolutions) in.readObject(); 

        } catch (Exception e) {
            throw new Exception("Nepovedlo desearializovat net");
        }
        
        l0.unite(l0_1, l0_2);
        
    }
}


