package nn.autoencoders;

import java.io.Serializable;
import java.util.Random;
public class Neuron implements Serializable
{
    public double [] weights, input;
    public double output, grad, sum ;
    public Neuron next;
    private static int range = 1;   // 1, jestli interval [-1, 1]
    int vrstva;

    public Neuron(int vrstva)
    {	
        if (vrstva==0){
                weights = new double[Neuronet.inputLength];
                input = new double[Neuronet.inputLength];
        } else 
        if (vrstva==1){
                weights = new double[Neuronet.prvniVrstva];
                input=new double[Neuronet.prvniVrstva];
        } else 
        if (vrstva==2){
                weights = new double[Neuronet.druhaVrstva];
                input=new double[Neuronet.druhaVrstva];
        } else {
            weights = new double[vrstva];
            input=new double[vrstva];
        } 
        this.vrstva = vrstva;
        doRandomWeights();
    }		


    public void doRandomWeights()							
    {  
            Random rand = new Random();
            for (int i=0;i<weights.length; i++){
                    do{
//                    weights[i] = -0.6+rand.nextDouble(); //-6 -> 5
                       weights[i] = -range + Math.random()*range*2;
                    } while (weights[i]==0);
            }
    }
    
    
    boolean dead() {
        boolean result = true;
        for (int i=0; i<weights.length; i++) {
            if(weights[i]!=0) 
                result = false;
        }
        return result;
    }
    
    
    public double countOut() throws Exception
    { 
        if(dead()) 
        throw new Exception("Neuron is dead, vrstva "+vrstva);
        
        sum=0;
        output=0;
        for (int i=0; i<input.length; i++)
        {   
            if(Double.isNaN(sum)) {
                int ads = 2;
            }
            sum+=weights[i]*input[i];
        }
        output=1.7159*Math.tanh(0.66*sum); 
        return output;
    }

    public void addOneWeightAndInput(int neuronNumber) {
        double[] templ = weights;
        weights = new double[neuronNumber];
        for (int i = 0; i<templ.length; i++) {
                weights[i] = templ[i];
        }
        Random rand = new Random();
        weights[weights.length-1] = -range + Math.random()*range*2;
        input = new double[neuronNumber];
    }

}


