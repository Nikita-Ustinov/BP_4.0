package nn.autoencoders;

import email.client.EmailClient;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import javax.imageio.*;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class NNAutoencoders implements Serializable {

    static Boolean firstEnter = true;
    static String progressInfo;
    static String _info;
    static String _convolutionsInfo;
    static int Answer;
    static boolean[] UzByli = new boolean[]{false, false, false, false, false, false, false, false, false, false};
    static Neuronet net;
    static double Max;
    static int counterNeuronNew = 0;
    public static int counterAddNeuron = 0;
    public int iterationWithoutNewNeuron = 0;
    boolean newNeuron = false;
    static int stillOneValue = -1;
    static int learningSet = 4;
    static int peopleInSet = 2;
    static int[][] combi = new int[4][3]; // = {{0,1,2}, {0,1,3}, {2,3,0}, {2,3,1}};        //4-pocet kombinaci, 3-pocet polozek v jedne kombinaci
    static HashMap<Integer, String> imgNumber = new HashMap<>();        //tady lezi adresa kazdeho obrazka z data seta

    public static void main(String[] args) throws Exception {
        createCombinations();
        pritnCombinations();
//        createFirstDataSet();
//        sendReport(_info, Max);
//        net = deseralizace("BestWeights");
//        net = new Neuronet();
//        deserializationNN();
//        int testResult = test();
//        System.out.println("test result: "+testResult);
//        net.serialization();
//        net.deserialization();
//        study();
    }
    
    static void pritnCombinations() {
        for(int i=0; i<combi.length; i++) {
            for(int j=0; j<3; j++) {
                System.out.print(combi[i][j]+" ");
            }
            System.out.println("");
        }
    }
    
    static void createCombinations() {
//        boolean isEnd = false;
        int group1Counter = 0;
        int group2Counter = 1;
        int groupNumber = peopleInSet;
        int groupSize = learningSet/peopleInSet;
        int numberOfCombinations = combi.length;
        HashMap<Integer, Integer[]> groups = new HashMap<>();
        int counter=0;
        int inFirstGroupCounter1 = 0;    //pro prvni hodnotu v combi
        int inFirstGroupCounter2 = 1;    //pro druhou hodnotu v combi
        int inSecondGroupCounter = 0;    //pro treti hodnotu v combi
        boolean isEndFirstGroup;
        boolean isJumpMode = false;
        if(groupSize == 2) 
            isEndFirstGroup  = true;
        else 
            isEndFirstGroup = false;
        
        for(Integer i=0; i<groupNumber; i++) { //naplneni groups
            Integer[] oneGroup = new Integer[groupSize];
            for(int j=0;j<groupSize; j++) {
                oneGroup[j] = counter;
                counter++;
            }
            groups.put(i, oneGroup);
        }

        for(int i=0; i<numberOfCombinations; i++) {
            Integer[] group1 = groups.get(group1Counter);
            Integer[] group2 = groups.get(group2Counter);
            if(i==7){
                i=i;
            }
            
            combi[i][0] = group1[inFirstGroupCounter1];
            combi[i][1] = group1[inFirstGroupCounter2];
            combi[i][2] = group2[inSecondGroupCounter];
            
            if(inSecondGroupCounter+1<groupSize) {
                inSecondGroupCounter++;
            }
            else {
                inSecondGroupCounter=0;
                if((group1Counter > group2Counter)&(group2Counter+2 != groupNumber)&(groupNumber-1 - group1Counter>0))
                    isJumpMode = true;
                if((group2Counter+1<groupNumber)&(group2Counter+1 != group1Counter)||(isJumpMode)) {
                    if(isJumpMode){
                        group2Counter += 2;
                        isJumpMode = false;
                    }
                    else 
                        group2Counter += 1;
                }
                else {
                    if(isEndFirstGroup) {
                        group1Counter++;
                        group2Counter = 0;
                        inFirstGroupCounter1 = 0;
                        inFirstGroupCounter2 = 1;
                        isEndFirstGroup = false;
                    }
                    else {
                        if(inFirstGroupCounter2+1 == groupSize){
                            inFirstGroupCounter1++;
                            inFirstGroupCounter2 = inFirstGroupCounter1+1;
                        }
                        else {
                            inFirstGroupCounter2++;
                        }
                    }
                    if((inFirstGroupCounter1+1 == inFirstGroupCounter2)&(inFirstGroupCounter2+1 == groupSize)) {
                        isEndFirstGroup = true;
                    }
                }
            }
        }
    
    
    }
    
    static void shuffleDataSets() {
        int elementNumber;
        int[] templ;
        for(int i=0; i<combi.length-1; i++) {
            elementNumber = (int)(Math.random()*(combi.length-i-1));
            templ = combi[combi.length-i-1];
            combi[learningSet-i-1] = combi[elementNumber];
            combi[elementNumber] = templ;
        }
    }
    
    static void createFirstDataSet() {
       String way = "Data set/";
       int counter = 0;
       for(int i=0; i<peopleInSet; i++) {
           for(int j=0; j<learningSet/peopleInSet; j++) {
               imgNumber.put(counter, way+i+"/"+j+".jpg");
               counter++;
           }
       }
    }

    public NNAutoencoders(int i) {}
    
    static Picture ImgToRightPicture(BufferedImage img, boolean isColor) throws Exception {
        if (img == null) {
            throw new Exception("ImgToRightPicture img - null");
        }
        int color;
        Picture vysledek;
        if(isColor) {
            vysledek = new Picture(Neuronet.inputPictureFormat,Neuronet.inputPictureFormat,3);
            for (int i=0;i<Neuronet.inputPictureFormat; i++) {
                for (int j=0; j<Neuronet.inputPictureFormat; j++) {
                    color = img.getRGB(j, i);
                    for(int k=0; k<3; k++) {
                        if (k==0) {
                            vysledek.map3D[i][j][k] = (color >>> 16) & 0xFF; //red 
                            vysledek.map3D[i][j][k] /= 256; 
                        }
                        if (k==1) {
                            vysledek.map3D[i][j][k] = (color >>> 8) & 0xFF;  //green
                             vysledek.map3D[i][j][k] /= 256; 
                        }
                        if (k==2) {
                            vysledek.map3D[i][j][k] = (color >>> 0) & 0xFF;  //blue
                            vysledek.map3D[i][j][k] /= 256; 
                        }
                    }
                }
            }
        }
        else {
            vysledek = new Picture(Neuronet.inputPictureFormat,Neuronet.inputPictureFormat);
            for (int i = 0; i < Neuronet.inputPictureFormat; i++) {
                for (int j = 0; j < Neuronet.inputPictureFormat; j++) {
                    color = img.getRGB(j, i); //bm.GetPixel(j,i);
                    int red = (color >>> 16) & 0xFF;
                    int green = (color >>> 8) & 0xFF;
                    int blue = (color >>> 0) & 0xFF;
                    double delta = (0.2126f * red + 0.7152f * green + 0.0722f * blue)/255;
                    vysledek.map2D[i][j] = delta;
                }
            }
        }
        return vysledek;
        
    }
    
    static Picture ImgToRightPicture(String file, boolean isColor) throws Exception  {
        if(file == null) 
            throw new Exception("ImgToRightPicture - file == null");
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(file));
        } catch (IOException e) {
            System.out.println("Nelze precist file");
        }
        Picture vysledek;
        if(isColor) {
            vysledek = new Picture(Neuronet.inputPictureFormat,Neuronet.inputPictureFormat,3);
            file += ".jpg";
            int color;
            for (int i=0;i<Neuronet.inputPictureFormat; i++) {
                for (int j=0; j<Neuronet.inputPictureFormat; j++) {
                    color = img.getRGB(j, i);
                    for(int k=0; k<3; k++) {
                        if (k==0) {
                            vysledek.map3D[i][j][k] = (color >>> 16) & 0xFF; //red 
                            vysledek.map3D[i][j][k] /= 256; 
                        }
                        if (k==1) {
                            vysledek.map3D[i][j][k] = (color >>> 8) & 0xFF;  //green
                             vysledek.map3D[i][j][k] /= 256; 
                        }
                        if (k==2) {
                            vysledek.map3D[i][j][k] = (color >>> 0) & 0xFF;  //blue
                            vysledek.map3D[i][j][k] /= 256; 
                        }
                    }
                }
            }
        }
        else {
            int color;
            vysledek = new Picture(Neuronet.inputPictureFormat,Neuronet.inputPictureFormat);
            for (int i = 0; i < Neuronet.inputPictureFormat; i++) {
                for (int j = 0; j < Neuronet.inputPictureFormat; j++) {
                    color = img.getRGB(j, i); //bm.GetPixel(j,i);
                    int red = (color >>> 16)& 0xFF;
                    int green = (color >>> 8)& 0xFF;
                    int blue = (color >>> 0)& 0xFF;
                    double delta = (0.2126f * red + 0.7152f * green + 0.0722f * blue)/255;
                    vysledek.map2D[i][j] = delta;
                }
            }
        }
         return vysledek;
    }

    static void calculateResult(Picture picture) throws Exception {
        Convolution templ = net.convolutions.head;                              
        for (int i = 0; i < net.convolutions.size; i++) {
            templ.clearInputMass();                                                     //mazani zbytecnych dat
            templ.clearOutput();
            templ = templ.next;
        }
        Picture[] firstConvolution = new Picture[net.convolutions.size / Neuronet.counvolutionGroups];		//Ð Ñ˜Ð Â°Ð¡ÐƒÐ¡ÐƒÐ Ñ‘Ð Ð† Ð Ñ˜Ð Â°Ð¡ÐƒÐ¡ÐƒÐ Ñ‘Ð Ð†Ð Ñ•Ð Ð†?? [][,]		//prvni vrstva, convolution 11x11
        for (int i = 0; i < net.convolutions.size / Neuronet.counvolutionGroups; i++) {
            firstConvolution[i] = new Picture(applyConvolution(i, picture));            //prvni konvoluce
            firstConvolution[i].map2D = function(firstConvolution[i].map2D, "Tanh");	// prvni funkce aktivace (Tanh)
            firstConvolution[i].map2D = pooling(2, firstConvolution[i].map2D);		//prvni pooling
        }
        Picture[] secondConvolution = new Picture[(int)Math.pow(net.convolutions.size/Neuronet.counvolutionGroups, 2)];
        int cisloFiltra = net.convolutions.size / Neuronet.counvolutionGroups ;
        int cisloPolozky = 0;
        for (int j = 0; j < net.convolutions.size / Neuronet.counvolutionGroups; j++) {
            for (int i = 0; i < net.convolutions.size / Neuronet.counvolutionGroups; i++) {
                secondConvolution[cisloPolozky] = new Picture(applyConvolution(cisloFiltra, firstConvolution[j]));
                secondConvolution[cisloPolozky].map2D = function(secondConvolution[cisloPolozky].map2D, "Tanh");
                secondConvolution[cisloPolozky].map2D = pooling(2, secondConvolution[cisloPolozky].map2D);
                cisloFiltra++;
                cisloPolozky++;
            }
            cisloFiltra = net.convolutions.size / Neuronet.counvolutionGroups;
        }
        Picture[] thirdConvolution = new Picture[(int)Math.pow(net.convolutions.size / Neuronet.counvolutionGroups, 3)] ;
        cisloFiltra = net.convolutions.size / Neuronet.counvolutionGroups * 2;
        cisloPolozky = 0;
        for (int j = 0; j < Math.pow(net.convolutions.size / Neuronet.counvolutionGroups, 2); j++) {
            for (int i = 0; i < net.convolutions.size / Neuronet.counvolutionGroups; i++) {
                thirdConvolution[cisloPolozky] = new Picture(applyConvolution(cisloFiltra, secondConvolution[j]));
                thirdConvolution[cisloPolozky].map2D = function(thirdConvolution[cisloPolozky].map2D, "Tanh");
                thirdConvolution[cisloPolozky].map2D = pooling(2, thirdConvolution[cisloPolozky].map2D);
                cisloFiltra++;
                cisloPolozky++;
            }
            cisloFiltra = net.convolutions.size / Neuronet.counvolutionGroups * 2;
        }
        
        Picture[] fourthConvolution = new Picture[(int)Math.pow(net.convolutions.size / Neuronet.counvolutionGroups, 4)];
        cisloFiltra = net.convolutions.size / Neuronet.counvolutionGroups * 3;
        cisloPolozky = 0;
        for (int j = 0; j < thirdConvolution.length; j++) {
            for (int i = 0; i < net.convolutions.size / Neuronet.counvolutionGroups; i++) {
                fourthConvolution[cisloPolozky] = new Picture(applyConvolution(cisloFiltra, thirdConvolution[j]));
                fourthConvolution[cisloPolozky].map2D = function(fourthConvolution[cisloPolozky].map2D, "Tanh");
//                fourthConvolution[cisloPolozky].map2D = pooling(2, fourthConvolution[cisloPolozky].map2D);
                cisloFiltra++;
                cisloPolozky++;
            }
            cisloFiltra = net.convolutions.size / Neuronet.counvolutionGroups * 3;
        }
        
        double[] inputFullyConnectionNet = doOneArray(fourthConvolution);
        net.l0.writeInput(inputFullyConnectionNet);				//zapisuje vstupni vektor v "fully connected" neuronovou sit
        net.l0.countOutputs();
        Neuron templ1 = net.l1.head;
        for (int i = 0; i < Neuronet.druhaVrstva; i++) {			//zapisuje do druhe vrstvy FC neuronove siti vstupni signaly
            for (int j = 0; j < Neuronet.prvniVrstva; j++) {
                templ1.input[j] = net.l0.outputs[j];
            }
            templ1 = templ1.next;
        }
        net.l1.countOutputs();
        templ1 = net.l2.head;
        for (int i = 0; i < Neuronet.tretiVrstva; i++) {			// zapisuje do treti vrstvy FC neuronove siti vstupni signaly
            for (int j = 0; j < Neuronet.druhaVrstva; j++) {
                templ1.input[j] = net.l1.outputs[j];
            }
            templ1 = templ1.next;
        }
        net.l2.countOutputs();
//        int index = 0;                  //cislo neuronu ktery vyhral
//        Max = net.l2.outputs[0];
//        for (int i = 0; i < net.l2.outputs.length; i++) {
//            if (Max < net.l2.outputs[i]) {
//                Max = net.l2.outputs[i];
//                index = i;
//            }
//        }

        templ = net.convolutions.head;                              
        for (int i = 0; i < net.convolutions.size; i++) {
            templ.countAverageInput();                                          //spocitani prumerneho vstupu
            templ.countAverageOutput();
            templ = templ.next;
        }
//        return index;
    }

    static double[][] function(double[][] picture, String nazevFunkce) {
        double[][] result = picture;
        int jEnd = 1;
        try{
            jEnd = picture[1].length;
        } catch(Exception e) {}
        
        for (int i = 0; i < picture.length; i++) {
            for (int j = 0; j < jEnd; j++) {
                if (nazevFunkce == "Tanh") {
                    result[i][j] = 1.7159 * Math.tanh(0.66 * picture[i][j]);
                } else if (nazevFunkce == "ReLu") {
                    if (result[i][j] > 0) {
                        result[i][j] = result[i][j];
                    } else {
                        result[i][j] = 0;
                    }
                }

            }
        }
        return result;
    }
 
    static void study() throws Exception {
        sendReport("Start learning", 0);
        long start = System.nanoTime();
        long checkpoint, runTime;
        double finalTest;
        String struct = writeSrtuct();  //popisuje strukturu seti
        doNNMoreClever();
        writeSrtuct();
        try {
            serializace("normal");
        } catch (Exception e){
            System.out.println("Serialization error in 'normal' way");
        }
    }
    
    static void sendReport(String struct, double finalTest) {
//        EmailClient em;
//        if("Start learning".equals(struct)) {
//             em = new EmailClient("ustinov.nikita.01@gmail.com", "report", "Start learning");
//        }
//        else {
//            String report = progressInfo + "\r\n" + finalTest  + "\r\n" + struct;
//            em = new EmailClient("ustinov_nikita_01@mail.ru", "report", report);
//        }
    }
    
    static void doNNMoreClever() throws Exception {
        RealTimeChart.firstShowChart();
        double[] err = new double[Neuronet.tretiVrstva];
        double[] output0 = new double[Neuronet.tretiVrstva];
        double[] output1 = new double[Neuronet.tretiVrstva];
        double[] output2 = new double[Neuronet.tretiVrstva];
        int iteration = 1;
        int gradNull = 0;
        double lokError = 0;
        int lokResult = 0;
        int lastResult = -1;
        double errorMin = 100;
        int testValue = 0;
        int bestTestValue = 0;
//        double lastResult = -1;
        int changeIteration = 0;
        boolean shakeFlag = false;
        int epochaWithoutNewNeuron = 0;
        while(testValue<98) {
            for(int w=0; w<combi.length; w++) {
                for(int i=0; i<combi[0].length; i++) {
                    calculateResult(ImgToRightPicture(imgNumber.get(combi[w][i]), true));
                    switch (i) {
                        case 0: {
//                            output0 = net.l2.outputs;
                            System.arraycopy(net.l2.outputs, 0, output0, 0, output0.length);
                            break;
                        }
                        case 1: {
//                            output1 = net.l2.outputs;
                            System.arraycopy(net.l2.outputs, 0, output1, 0, output0.length);
                            break;
                        }
                        case 2: {
//                            output2 = net.l2.outputs;
                            System.arraycopy(net.l2.outputs, 0, output2, 0, output0.length);
                            break;
                        }
                    }
                }
                Neuron templ3 = net.l2.head;
                if((Double.isNaN(templ3.weights[1])||(Double.isNaN(net.l0.head.weights[1]))||(Double.isNaN(net.l1.head.weights[1])))) { //kontrola siti
                    System.out.println("NAN NAN NAN iteration "+ iteration);   //kontrola neuronu
                }
                double a,b;
                int countWrongWay = 0;
                for(int i=0;i<err.length; i++) {
                    a = Math.abs(output0[i] - output1[i]);
                    b = Math.abs(output0[i] - output2[i]);
                    if(a>=b) {
                        err[i] =1;
                        countWrongWay++;
                    }
                    else {
                        err[i] = -1;
                    }
                }
//                System.out.println("Hyba v "+countWrongWay+" polozkach");
                 Neuron templ2;
            templ3 = net.l2.head;
            double delta = 0;
            int deltaCounter = 0;
            for (int i = 0; i < Neuronet.tretiVrstva; i++) {
                templ2 = net.l1.head;
                templ3.grad = 0.388 * (1.716 - templ3.output) * (1.716 + templ3.output) * err[i];//1.7159   //pocita gradient pro vystupni vyrstvu
                for (int j = 0; j < Neuronet.druhaVrstva; j++) {
                    delta = net.speedLFCN * templ2.output * templ3.grad;
                    templ3.weights[j] += net.speedLFCN * templ2.output * templ3.grad;     //pocita vahy pro vystupni vrstvu
                    templ2 = templ2.next;
                    deltaCounter++;
                }
                templ3 = templ3.next;
            }
            delta = delta/deltaCounter;
//                System.out.println("Average change weights on l2: "+delta);

            double grad = 0;
            Neuron templ1;
            templ2 = net.l1.head;
            delta = 0;
            deltaCounter=0;
            for (int i = 0; i < Neuronet.druhaVrstva; i++) {
                grad = 0;
                templ3 = net.l2.head;
                for (int u = 0; u < Neuronet.tretiVrstva; u++) {		//sumarizuje gradient predhozi vrstvy (delta pravidlo pro druhou vrstvu)
                    grad += templ3.grad * templ3.weights[i];
                    templ3 = templ3.next;
                }
                templ2.grad = grad * 0.388 * (1.716 - templ2.output) * (1.716 + templ2.output);//1.7159
                templ1 = net.l0.head;
                for (int j = 0; j < Neuronet.prvniVrstva; j++) {
                    delta = net.speedLFCN * templ1.output * templ2.grad;
                    templ2.weights[j] += net.speedLFCN * templ1.output * templ2.grad;
                    templ1 = templ1.next;
                    deltaCounter++;
                }
                templ2 = templ2.next;
            }
            delta = delta/deltaCounter;
//            System.out.println("Average change weights on l1: "+delta);
            delta = 0;
            deltaCounter = 0;
            templ1 = net.l0.head;
            for (int i = 0; i < Neuronet.prvniVrstva; i++) {
                grad = 0;
                templ2 = net.l1.head;
                for (int u = 0; u < Neuronet.druhaVrstva; u++) {		//sumarizuje gradient predhozi vrstvy (delta pravidlo pro prvni vrstvu)
                    grad += templ2.grad * templ2.weights[i];
                    templ2 = templ2.next;
                }
                templ1.grad = grad * 0.388 * (1.716 - templ1.output) * (1.716 + templ1.output);//1.7159
                for (int j = 0; j < Neuronet.inputLength; j++) {
                    delta = net.speedLFCN * templ1.input[j] * grad;
                    templ1.weights[j] += net.speedLFCN * templ1.input[j] * grad;
                    deltaCounter++;
                }
                templ1 = templ1.next;
            }
            
            delta = delta/deltaCounter;
//            System.out.println("Average change weights on l0: "+delta);
            
            //pro filtry 15 az 19
            int minusCounter = 1;
            Convolution templ = net.convolutions.head;
            while (templ.cisloFiltra != net.convolutions.size / Neuronet.counvolutionGroups * (Neuronet.counvolutionGroups-minusCounter) ) {
                templ = templ.next;
            }
            for (int i = 0; i < net.convolutions.size / Neuronet.counvolutionGroups; i++) {
                grad = 0;
                templ1 = net.l0.head;                                           //vrstva se ktere scita gradienty
                for (int j = 0; j < net.l0.length; j++) {
                    grad += templ1.grad;					//sumarizuje gradient predhozi vrstvy
                    templ1 = templ1.next;
                }
                templ.grad = grad * 0.388 * (1.7159 - templ.averageOutput) * (1.7159 + templ.averageOutput) / net.l0.length;
                if (grad == 0) {
                    gradNull++;
                }
                delta = 0;
                for (int k = 0; k < templ.weights2D.length; k++) {
                    for (int q = 0; q < templ.weights2D[0].length; q++) {
                        delta = net.speedL1CL * templ.grad * templ.avInput[k][q];
                        templ.weights2D[k][q] += delta;
                    }
                    
                }
                templ = templ.next;
            }
            minusCounter++;
            
            //pro filtry 10 az 14
            templ = net.convolutions.head;
            while (templ.cisloFiltra != net.convolutions.size / Neuronet.counvolutionGroups * (Neuronet.counvolutionGroups-minusCounter) ) {
                templ = templ.next;
            }
            for (int i = 0; i < net.convolutions.size / Neuronet.counvolutionGroups; i++) {
                grad = 0;
                Convolution templLast = net.convolutions.head;          //vrstva se ktere scita gradienty
                while (templLast.cisloFiltra != net.convolutions.size / Neuronet.counvolutionGroups * (Neuronet.counvolutionGroups-minusCounter+1) ) {
                    templLast = templLast.next;
                }
                for (int j = 0; j < net.convolutions.size / Neuronet.counvolutionGroups; j++) {
                    grad += templLast.grad;				//sumarizuje gradient predhozi vrstvy
                    templLast = templLast.next;
                }
                templ.grad = grad * 0.388 * (1.7159 - templ.averageOutput) * (1.7159 + templ.averageOutput) / Neuronet.prvniVrstva;
                if (grad == 0) {
                    gradNull++;
                }
                for (int k = 0; k < templ.weights2D.length; k++) {
                    for (int q = 0; q < templ.weights2D[0].length; q++) {
                        templ.weights2D[k][q] += net.speedL2CL * templ.grad * templ.avInput[k][q];
                    }
                }
                templ = templ.next;
            }
            minusCounter++;
            //pro filtry 5 az 9
            templ = net.convolutions.head;
            while (templ.cisloFiltra != net.convolutions.size / Neuronet.counvolutionGroups * (Neuronet.counvolutionGroups-minusCounter) ) {
                templ = templ.next;
            }
            for (int i = 0; i < net.convolutions.size / Neuronet.counvolutionGroups; i++) {
                grad = 0;
                Convolution templLast = net.convolutions.head;          //vrstva se ktere scita gradienty
               while (templLast.cisloFiltra != net.convolutions.size / Neuronet.counvolutionGroups * (Neuronet.counvolutionGroups-minusCounter+1) ) {
                    templLast = templLast.next;
                }
                for (int j = 0; j < net.convolutions.size / Neuronet.counvolutionGroups; j++) {
                    grad += templLast.grad;				//sumarizuje gradient predhozi vrstvy
                    templLast = templLast.next;
                }
                templ.grad = grad * 0.388 * (1.7159 - templ.averageOutput) * (1.7159 + templ.averageOutput) / Neuronet.prvniVrstva;
                if (grad == 0) {
                    gradNull++;
                }
                for (int k = 0; k < templ.weights2D.length; k++) {
                    for (int q = 0; q < templ.weights2D[0].length; q++) {
                        templ.weights2D[k][q] += net.speedL2CL * templ.grad * templ.avInput[k][q];
                    }
                }
                templ = templ.next;
            }

            //pro filtry 0 az 4
            templ = net.convolutions.head;
            for (int i = 0; i < net.convolutions.size / Neuronet.counvolutionGroups; i++) {
                grad = 0;
                Convolution templLast = net.convolutions.head;		//vrstva se ktere scita gradienty
                while (templLast.cisloFiltra != net.convolutions.size / Neuronet.counvolutionGroups ) {
                    templLast = templLast.next;
                }
                for (int j = 0; j < net.convolutions.size / Neuronet.counvolutionGroups; j++) {
                    grad += templLast.grad;					//sumarizuje gradient predhozi vrstvy
                    templLast = templLast.next;
                }
                templ.grad = grad * 0.388 * (1.7159 - templ.averageOutput) * (1.7159 + templ.averageOutput) / Neuronet.druhaVrstva;
                if (grad == 0) {
                    gradNull++;
                }
                for (int k = 0; k < templ.weights3D.length; k++) {
                    for (int q = 0; q < templ.weights3D[0].length; q++) {
                        for(int z=0; z < templ.weights3D[0][0].length; z++) {
                            templ.weights3D[k][q][z] += net.speedL3CL * templ.grad * templ.avInput[k][q];
                        }
                    }
                }
                templ = templ.next;
            }

                if (Answer != lokResult) {
                    lokError++;
                }

                if (shakeFlag) {
                    net.speedL1CL /= 5;
                    net.speedL2CL /= 5;
                    net.speedL3CL /= 5;
                    net.speedLFCN /= 5;
                    shakeFlag = false;
                    System.out.println("Shake flag false");
                }

                if ((iteration % combi.length) == 0) {
                    testValue = test();
                    RealTimeChart.displayValueOnChart(testValue);
                    shuffleDataSets();
                    if(gradNull>0) 
                        System.out.println("Grad 0: "+gradNull);
                    if (testValue > bestTestValue) {
                        bestTestValue = testValue;
                        System.out.println("");
                        System.out.println("Better result >>>>>>>>>>>  "+bestTestValue);
                        System.out.println("");
                        net.serialization();
                        sendReport("New best value", testValue);
                       RealTimeChart.SaveChart();
                    }
                    writeProgressInfo(iteration, testValue);

                    if (epochaWithoutNewNeuron>=50) {
                        System.out.println("epochaWithoutNewNeuron "+ epochaWithoutNewNeuron);
                        System.out.println("Error difference " + (lastResult-lokError/iteration*100));
                        if (Math.abs(lastResult-testValue) < 5) {	
                            epochaWithoutNewNeuron=0;
                            addNeuron();
                            writeSrtuct();
                        }
                    }
                    else 
                        epochaWithoutNewNeuron++;

                    if (iteration % 1000000 == 0) {
                        _convolutionsInfo = null;
                    }
                    if (iteration % 1000000 == 0) {
                        progressInfo = null;
                    }
                    lokError = 0;
                }
                lastResult = testValue;
                iteration++;
               
            }
            gradNull = 0;
        
        }
        sendReport("End of learning", testValue);
       
    }
    
    static Boolean addNeuron(){
        counterNeuronNew++;
        System.out.println("---------------------------New neuron added!!---------------------------------");
        if (counterAddNeuron==1){
            counterAddNeuron=0;
            net.l1.addNeuron();
            net.druhaVrstva++;
            net.l1.length=net.druhaVrstva;
            net.l1.outputs=new double[net.druhaVrstva];
            
            Neuron templ=net.l2.head;
            for(int i=0; i<net.tretiVrstva; i++) {
                templ.addOneWeightAndInput(Neuronet.druhaVrstva);
                templ = templ.next;
            }
            String struct = writeSrtuct();
            EmailClient em = new EmailClient("ustinov_nikita_01@mail.ru", "new neuron", "Add neuron at l1. new struct " +struct);
            return false;
        }
        else {
            counterAddNeuron++;
            net.l0.addNeuron();
            net.prvniVrstva++;
            net.l0.length=net.prvniVrstva;
            net.l0.outputs=new double[net.prvniVrstva];

            Neuron templ=net.l1.head;
            for(int i=0; i<net.druhaVrstva; i++) {
                templ.addOneWeightAndInput(Neuronet.prvniVrstva);
                templ = templ.next;
            }
            String struct = writeSrtuct();
            EmailClient em = new EmailClient("ustinov_nikita_01@mail.ru", "new neuron", "Add neuron at l0. new struct " +struct);
            return true;
        }
    }

    static double[][] pooling(int size, double[][] picture) {             	// size treba 2x2 => size=2
        double templ = (double)picture.length / size;                           //jenom pro to, aby Math.ceil spravne zaokrouhlil
        int massSize1 = (int)Math.ceil(templ);
        templ = (double)(picture[0].length) / size;                             //jenom pro to, aby Math.ceil spravne zaokrouhlil
        int massSize2 = (int)Math.ceil(templ);
        if (massSize2 == 0) {
            massSize2 = 1;
        }
        double[][] result = new double[massSize1][massSize2];
        int x0, x1, y0, y1;
        y0 = 0;
        y1 = size;
        for (int i = 0; i < result.length; i++) {
            x0 = 0;
            x1 = size;
            for (int j = 0; j < result[0].length; j++) {
                result[i][j] = max(picture, x0, x1, y0, y1);
                x0 += size;
                x1 += size;
            }
            y0 += size;
            y1 += size;
        }
        return result;
    }

    static double max(double[][] picture, int x0, int x1, int y0, int y1) {
        double result = picture[y0][x0];
        for (int i = y0; i < y1; i++) {
            for (int j = x0; j < x1; j++) {
                try{
                    if (picture[i][j] > result) {
                        result = picture[i][j];
                    }
                }
                catch(Exception e){}
            }
        }
        return result;
    }

    static double[][] applyConvolution(int cisloFiltra, Picture picture) {
        int countTempl = 0;
        Convolution templ = net.convolutions.head;
        while (cisloFiltra != 0) {
            templ = templ.next;
            cisloFiltra--;
        }
        int x;
        int y;
        if (picture.map3D == null) {
                x = picture.map2D[0].length - templ.weights2D[0].length + 1;		// rozmer vysledne matici - x a y
                y = picture.map2D.length - templ.weights2D.length + 1;
        }
        else {
                x = picture.map3D[0].length - templ.weights3D[0].length + 1;		// rozmer vysledne matici - x a y
                y = picture.map3D.length - templ.weights3D.length + 1;
        }
        double[][] result = new double[y][x];
        int x0, y0;
        for(int i=0; i<y; i++) {
                x0 = 0;
                y0 = 0;
                for(int j=0; j<x; j++) {
                        result[i][j] = sum(picture ,templ, x0, y0);
                        x0++;
                }
                y0++;
        }
        return result;
    }

    static double sum(Picture picture, Convolution templ, int x0, int y0) {
        double result = 0;
        int y = 0;			// kountery pro konvoluce
        int x = 0;
        if(picture.map3D == null) {
            for(int i=y0; i<y0+templ.weights2D.length; i++) {
                for(int j=x0; j<x0+templ.weights2D[0].length; j++) {
                    if(j==picture.map2D[0].length) {
                        result += picture.map2D[i][j-1]*templ.weights2D[y][x];
                        templ.addInput(picture.map2D[i][j - 1], y, x); 
                    }
                    else {
                        result += picture.map2D[i][j]*templ.weights2D[y][x];
                        templ.addInput(picture.map2D[i][j], y, x);
                    }
                    x++;
                }
                x=0;
                y++;
            }
            templ.addOutput(result);
            return result;
        }
        else {
            for(int i=y0; i<y0+templ.size2; i++) {
                for(int j=x0; j<x0+templ.size1; j++) {
                    for(int k=0; k<3; k++) {
                        if(j==picture.map3D[0].length) {
                            result += picture.map3D[i][j-1][k]*templ.weights3D[y][x][k];
                            templ.addInput(picture.map3D[i][j - 1][ k], y, x, k); 
                        }
                        else {
                            result += picture.map3D[i][j][k]*templ.weights3D[y][x][k];
                            templ.addInput(picture.map3D[i][j][k], y, x, k);
                        }
                    }
                    x++;
                }
                x=0;
                y++;
            }
            templ.addOutput(result);
            return result;
        }
    }

    static double[] doOneArray(Picture[] inputArray) {
        int length = inputArray.length * inputArray[0].map2D.length;
        int kEnd = 1;
//        if(inputArray[0].map2D[1] != null)
        try {    
            length *= inputArray[0].map2D[1].length;
        } catch(Exception e) {}
        double[] result = new double[length];
        int counter = 0;
        for (int i = 0; i < inputArray.length; i++) {
            for (int j = 0; j < inputArray[i].map2D.length; j++) {
                for (int k = 0; k < kEnd; k++) {
                    result[counter] = inputArray[i].map2D[j][k];
                    counter++;
                }
            }
        }
        return result;
    }

    static String writeSrtuct() {
        String text = "NN struct:" + "\r\n";
        text+= "vstupni vrstva: "+ net.inputLength+ "\r\n";
        text+= "prvni vrstva: "+ net.prvniVrstva+ "\r\n";
        text+= "druha vrtstva: "+ net.druhaVrstva+ "\r\n";
        text+= "treti vrstva: "+ net.tretiVrstva;
        
        write("NN struct.txt", text);
        return text;
    }
    
    public static void writeInfo(double[][] picture, String typeOfTransformation)  {                //zobrazuje zmeny primo v konvolucich
        _info += typeOfTransformation + " -> " + "\r\n" + "x- " + picture[0].length + "\r\n" + "y -" + picture.length + "\r\n";
        try {
            File.createTempFile("Date.txt", _info); //WriteAllText("Date.txt", _info);
        } catch (IOException e) {
        }
        writeConvolution(picture, typeOfTransformation);
    }

    static void writeProgressInfo(int iteration, double testValue) {
        progressInfo += "epoch = " + iteration / (learningSet*2) + " test value = " + testValue + "\r\n";
        System.out.println("epoch = " + iteration / combi.length + " test value = " + testValue);
        write("Short progress info.txt", progressInfo);
    }

    static void writeAllConvolution(int iteration) {
        Convolution templ = net.convolutions.head;
        for (int i = 0; i < net.convolutions.size; i++) {
            writeConvolution(templ, i, iteration);
            templ = templ.next;
        }
    }
    
    static void writeConvolution(Convolution convolution, int counvolutionNumber, int iteration ) {
        if(firstEnter) {
            firstEnter = false;
            _convolutionsInfo = null;
        }
        _convolutionsInfo += "iteration:"+iteration+" в„–:"+counvolutionNumber;
         _convolutionsInfo += "\r\n";
        if(convolution.weights3D == null) {
            for (int i = 0; i < convolution.weights2D.length; i++) {
                for (int j = 0; j < convolution.weights2D[0].length; j++) {
                    _convolutionsInfo += convolution.weights2D[i][j] + "  ";
                }
                _convolutionsInfo += "\r\n";
            }
             _convolutionsInfo += "\r\n";
        }
        
        else {
             for (int i = 0; i < convolution.weights3D.length; i++) {
                for (int j = 0; j < convolution.weights3D[0].length; j++) {
                    _convolutionsInfo += "[";
                    for (int k=0; k<convolution.weights3D[0][0].length; k++) {
                        _convolutionsInfo += convolution.weights3D[i][j][k] + ",";
                    }
                    _convolutionsInfo += "] ";
                }
                _convolutionsInfo += "\r\n";
            }
            _convolutionsInfo += "\r\n"+"\r\n";
        }
        write("Convolutions.txt", _convolutionsInfo);
    }

    static void writeConvolution(double[][] picture, String typeOfTransformation) {
        _convolutionsInfo += typeOfTransformation + "\r\n";
        for (int i = 0; i < picture.length; i++) {
            for (int j = 0; j < picture[0].length; j++) {
                _convolutionsInfo += picture[i][j] + "  ";
            }
            _convolutionsInfo += "\r\n";
        }
        _convolutionsInfo += "\r\n" + "\r\n";
        write("Convolutions.txt", _convolutionsInfo);

    }

    static int test() throws Exception {  
        double[] output0 = new double[Neuronet.tretiVrstva];
        double[] output1 = new double[Neuronet.tretiVrstva];
        double[] output2 = new double[Neuronet.tretiVrstva];
        double a,b;
        int counterRight = 0;
        int counteWrong = 0;
        for(int w=0; w<combi.length; w++) {
            for(int i=0; i<combi[0].length; i++) {
                calculateResult(ImgToRightPicture(imgNumber.get(combi[w][i]), true));
                switch (i) {
                    case 0: {
                        System.arraycopy(net.l2.outputs, 0, output0, 0, output0.length);
                        break;
                    }
                    case 1: {
                        System.arraycopy(net.l2.outputs, 0, output1, 0, output0.length);
                        break;
                    }
                    case 2: {
                        System.arraycopy(net.l2.outputs, 0, output2, 0, output0.length);
                        break;
                    }
                }
            }
            for(int i=0;i<Neuronet.tretiVrstva; i++) {
                a = Math.abs(output0[i] - output1[i]);
                b = Math.abs(output0[i] - output2[i]);
                if(a<b) {
                    counterRight++;
                }
                else {
                    counteWrong++;
                }

            }
        
        }
        counteWrong = counteWrong/combi.length;
//        System.out.println("Right: "+counterRight+" / wrong: "+counteWrong);
        return (int)(100-((Neuronet.tretiVrstva/100)*counteWrong));
    }
    
    static BufferedImage drawRectungle(int x, int y, int size, BufferedImage result) {
        Color blue = new Color(0,0,255);
        int rgbBlue = blue.getRGB();
        for(int i=y; i<y+size; i++) {
            for(int j=x; j<x+size; j++) {
                if((i-y==0)||(i-y==size-1)||(j-x==0)||(j-x==size-1))
                    result.setRGB(j, i, rgbBlue);
            }
        }
        return result;
    } 
        
    static void serializace(String wayOfSaving) throws Exception {
        String fileName = null;
        if (wayOfSaving == "normal") {
            fileName = "weights";
        } else {
            fileName = "BestWeights";;
        }
        FileOutputStream fos = new FileOutputStream(fileName+".out");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(net);
        oos.close();
    }

    static void deserializationNN() throws Exception {//deserializuje NN z dat-faila
        net = deseralizace("weights");
        Neuronet.inputLength = net.inputLengthOwn;
        Neuronet.prvniVrstva = 51;
        Neuronet.druhaVrstva = net.druhaVrstvaOwn;
        Neuronet.tretiVrstva = net.tretiVrstvaOwn;
    }
    
    static Neuronet deseralizace(String way) throws Exception {
        try {
            way += ".out";
            Neuronet net = null;
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(way));
            net = (Neuronet) in.readObject();
            return net;
        } catch (Exception e) {
            return new Neuronet();
        }
    }

    static double[][] addX(double[][] picture) {					//pridani sloupce '0' k polu
        double[][] result = new double[picture.length][picture[0].length + 1];
        for (int i = 0; i < picture.length; i++) {
            for (int j = 0; j < picture[0].length + 1; j++) {
                if (j == picture[0].length) {
                    result[i][j] = -100;
                } else {
                    result[i][j] = picture[i][j];
                }
            }
        }
        return result;
    }

    static double[][] addY(double[][] picture) {					//pridani radka '0' k polu
        double[][] result = new double[picture.length + 1][picture[0].length];
        for (int i = 0; i < picture.length + 1; i++) {
            for (int j = 0; j < picture[0].length; j++) {
                if (i == picture.length) {
                    result[i][j] = -100;
                } else {
                    result[i][j] = picture[i][j];
                }
            }
        }
        return result;
    }

    public static void write(String fileName, String text) {
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());

            try {
                out.print(text);
            } finally {
                out.close();
            }
        } catch (IOException e) {}
    }
    
}
