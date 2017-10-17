package nn.autoencoders;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import static nn.autoencoders.NNAutoencoders.net;


//[Serializable]
public class List implements Serializable
{
    public int length;
    public Neuron head;
    public double [] outputs;
    int vrstva;


    public List(int vrstva)
    {
        this.vrstva=vrstva;
        if (vrstva==0)
                length=Neuronet.prvniVrstva;
        if(vrstva==1)
                length=Neuronet.druhaVrstva;
        if(vrstva==2)
                length=Neuronet.tretiVrstva;
        Neuron templ;
        for (int i=0; i< length; i++){
            Neuron node=new Neuron(vrstva);
            if (i==0)
                    head=node;
            else {
                 templ=head;
                 while(templ.next!=null){
                        templ = templ.next;
                 }
                 do{
                   node= new Neuron(vrstva);
                 } while(templ.weights[0]==node.weights[0]);
                 templ.next=node;
            }
        }
        outputs=new double[length];
    }
    
    public List(int input, int output) {
        Neuron templ;
        for (int i=0; i< output; i++){
            Neuron node=new Neuron(input);
            if (i==0)
                    head=node;
            else {
                 templ=head;
                 while(templ.next!=null){
                        templ = templ.next;
                 }
                 do{
                   node= new Neuron(input);
                 } while(templ.weights[0]==node.weights[0]);
                 templ.next=node;
            }
        }
        outputs=new double[output];
    }
    
//    public List(List original) {    //dela z puvodniho Linked List(neuronu) opačný 
//        int output = original.head.input.length;
//        int input = original.length;
//        Neuron templ;
//        for (int i=0; i< output; i++){
//            Neuron node=new Neuron(input);
//            if (i==0)
//                    head=node;
//            else {
//                 templ=head;
//                 while(templ.next!=null){
//                        templ = templ.next;
//                 }
//                 do{
//                   node= new Neuron(input);
//                 } while(templ.weights[0]==node.weights[0]);
//                 templ.next=node;
//            }
//        }
//        outputs=new double[output];
//    }
    
    List subList(int counter) { //pro vytvareni sublistu
        List list = null;
        if(vrstva==0) {
            if(counter == 0) {
                list = new List(Neuronet.inputLength, this.length/2);
                Neuron templ1 = list.head;
                Neuron templ2 = this.head;
                for(int i =0; i<list.length; i++) {
                    templ1 = templ2;
                    templ1 = templ1.next;
                    templ2 = templ2.next;
                }
            }
            else if(counter == 1) {
                list = new List(Neuronet.inputLength, this.length/2);
                Neuron templ1 = list.head;
                Neuron templ2 = this.head;
                for(int i=0; i<length/2; i++) {
                    templ2 = templ2.next;
                }
                for(int i =0; i<list.length; i++) {
                    templ1 = templ2;
                    templ1 = templ1.next;
                    templ2 = templ2.next;
                }
            }
        }
        return list;
    }
    
    void unite(List input1, List input2) throws Exception {
        if((input1==null)||(input2 == null)) {
            throw new Exception("neda se spojit nulove seznamy!");
        }
        Neuron templ = head;
        Neuron inputTempl = input1.head;
        for(int i=0; i<length/2; i++) {
            templ = inputTempl;
            templ = templ.next;
            inputTempl = inputTempl.next;
        }
        
        
        inputTempl = input2.head;
        for(int i=0; i<length/2; i++) {
            templ = inputTempl;
            templ = templ.next;
            inputTempl = inputTempl.next;
        }
    }
    

    public void writeInput(double[] input){
        Neuron templ=head;
        for (int i=0; i<Neuronet.prvniVrstva; i++){
            templ.input = new double[Neuronet.inputLength];
            for (int j=0; j<input.length; j++){
                templ.input[j]= input[j];
            }
            templ=templ.next;
            if(templ == null) {
                templ =templ;
            }
        }
    }


    public void countOutputs() throws Exception{
        Neuron templ=head;
        int counter=0;
        while(templ!=null){
                outputs[counter]=templ.countOut();
                templ= templ.next;
                counter++;
        }
    }

    public void addNeuron(){
        Neuron templ;
        templ=head;
        while(templ.next!=null){
            templ= templ.next;
        }
        Neuron node= new Neuron(vrstva);
        templ.next=node;
    }
    
    void serialization() throws IOException {
        Neuron templ = head;
        String fileName = "serialization files/"+vrstva+"/";
        FileOutputStream fos;
        ObjectOutputStream oos = null;
        for(int i=0; i<length; i++) {
            templ.serialization(i);
//            fos = new FileOutputStream(fileName+i+".out");
//            oos = new ObjectOutputStream(fos);
//            templ.serializationStandart(i);
            templ = templ.next;
//        oos.writeObject(templ);
            System.out.println("serialized "+i+" neuron");
        }
        oos.close();
    }
    
    List deserialization() throws IOException {
        Neuron templ = head;
        for(int i=0; i<length; i++) {
            templ.deserialization(i);
        }
        return this;
    }
    
//    void serialization() throws FileNotFoundException, IOException { 
//       if(vrstva!=0) {
//            FileOutputStream fos = new FileOutputStream("serialization files/"+vrstva+".out"); 
//            ObjectOutputStream oos = new ObjectOutputStream(fos); 
//            oos.writeObject(this); 
//            oos.close(); 
////        }catch(Exception e) {
//        }else {
//            List list_01 = new List;
//            FileOutputStream fos = new FileOutputStream("serialization files/"+vrstva+"_1.out"); 
//            ObjectOutputStream oos = new ObjectOutputStream(fos); 
//            oos.writeObject(list_01); 
//            oos.close(); 
//            
//            List list_02 = new List(List(1, ""));
//            fos = new FileOutputStream("serialization files/"+vrstva+"_2.out"); 
//            oos = new ObjectOutputStream(fos); 
//            oos.writeObject(list_02); 
//            oos.close(); 
//        }
//    }
    
//    public void plusOneRundomizeWeight() {
//        Neuron templ = head;
//        for(int i=0; i<outputs.length; i++) {
//            double[] newWeights = new double[templ.weights.length+1];
//            newWeights = templ.weights;
//            newWeights[templ.weights.length] = 
//        }
//    }
}

