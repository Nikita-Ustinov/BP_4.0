package nn.autoencoders;

import java.io.Serializable;


public final class Neuronet implements Serializable
{	
    public List l0, l1, l2;
    public static int inputLength = 2048;	//pro fully connection neuronet
    public static int prvniVrstva =  900;		
    public static int druhaVrstva = 100;		
    public static int tretiVrstva = 2;
    public static double speedLFCN = 0.0007;				//rychlost uceni pro "fully connection neuronet"
    public static double speedL1CL = 0.0007;				//rychlost uceni pro prvni "convolution layer" 10 az 14
    public static double speedL2CL = 0.0007;				//rychlost uceni pro druhy "convolution layer"  5 az 9
    public static double speedL3CL = 0.0007;				//rychlost uceni pro treti "convolution layer"  0 az 4
    public static int inputPictureFormat = 40; //px                         //jestli napriklad obrazek 20x20 davame sem 20
    public int inputLengthOwn;
    public int prvniVrstvaOwn;
    public int druhaVrstvaOwn;
    public int tretiVrstvaOwn;
    public ListOfConvolutions convolutions ;
    public static int cisloFiltra = 0;
    public static int numberOfFulters = 24;

    public Neuronet()
    {
        addFilter(11,11,3);				//pridani konvoluci 11x11x3 
        addFilter(11,11,3);				//pridani konvoluci 11x11x3   
        addFilter(11,11,3);				//pridani konvoluci 11x11x3   
        addFilter(11,11,3);				//pridani konvoluci 11x11x3   
        addFilter(11,11,3);                             //pridani konvoluci 11x11x3 
        addFilter(11,11,3);				//pridani konvoluci 11x11x3   
        addFilter(11,11,3);                             //pridani konvoluci 11x11x3 
        addFilter(11,11,3);                             //pridani konvoluci 11x11x3 

        addFilter(6,6);					//pridani konvoluci 6x6  
        addFilter(6,6);					//pridani konvoluci 6x6   
        addFilter(6,6);					//pridani konvoluci 6x6   
        addFilter(6,6);					//pridani konvoluci 6x6   
        addFilter(6,6);					//pridani konvoluci 6x6   
        addFilter(6,6);					//pridani konvoluci 6x6   
        addFilter(6,6);					//pridani konvoluci 6x6   
        addFilter(6,6);					//pridani konvoluci 6x6   
        

        addFilter(3,3);					//pridani konvoluci 3x3  
        addFilter(3,3);					//pridani konvoluci 3x3   
        addFilter(3,3);					//pridani konvoluci 3x3  
        addFilter(3,3);					//pridani konvoluci 3x3  
        addFilter(3,3);					//pridani konvoluci 3x3  
        addFilter(3,3);					//pridani konvoluci 3x3 
        addFilter(3,3);					//pridani konvoluci 3x3
        addFilter(3,3);					//pridani konvoluci 3x3

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
}


