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
import java.util.HashMap;
import com.mycompany.segmentation.Segmentation;

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
    static LinkedList<Integer> withRoeDeer = new LinkedList<>();
    static LinkedList<Integer> withoutRoeDeer = new LinkedList<>();
    static int counterWithRoe = 0; // pocet zpracovanych obrazku v sete s srnkou v ere
    static int counterWithoutRoe = 0; // pocet zpracovanych obrazku v sete bez srnky v ere
    static int learningSet = 132;
    static int testSet = 500;
//    static BufferedImage result;

    public static void main(String[] args) throws Exception {
        createFirstDataSet();
//        net = deserializationNN("Best weights");
//        String test = checkOneClass("Without");
//        test+="\r\n"+"With:"+"\r\n";
//        test+=checkOneClass("With");
//        write("testResult", test);
        
//        System.out.println("");
//        System.out.println("with:");
//        System.out.println("");
//        checkOneClass("With");
//        checkOnePicture();
//        exam2();
//        writeProgressInfo(101,test("normal"));					//vysledek ve fajlu "short progress info"
        net = new Neuronet();
        study();
    }
    
    static void newEpoch() {
//        withRoeDeer = new LinkedList<>();
//        withoutRoeDeer = new LinkedList<>();
        shuffleDataSets();
        counterWithRoe = 0;
        counterWithoutRoe = 0;
    }
    
    static void shuffleDataSets() {
        int elementNumber;
        int templ;
        for(int i=0; i<learningSet-1; i++) {
            elementNumber = (int)(Math.random()*(learningSet-i-1));
            templ = withRoeDeer.get(learningSet-i-1);
            withRoeDeer.set(learningSet-i-1, withRoeDeer.get(elementNumber));
            withRoeDeer.set(elementNumber, templ);
        }
        
        for(int i=0; i<learningSet-1; i++) {
            elementNumber = (int)(Math.random()*(learningSet-i-1));
            if(elementNumber <0 ){
                elementNumber = elementNumber;
            }
            templ = withoutRoeDeer.get(learningSet-i-1);
            withoutRoeDeer.set(learningSet-i-1, withoutRoeDeer.get(elementNumber));
            withoutRoeDeer.set(elementNumber, templ);
        }
    }
    
    static void tryRandom() {
        int a = 0; 
        int b = 20;
        for(int i=0; i< 200; i++) {
            System.out.println((int)(Math.random()*b));   
        }
    }
    
    static void printDataSets() {
        System.out.println("With roe: ");
        for(int i=0; i<learningSet; i++) {
            System.out.println(withRoeDeer.get(i));
        }
        System.out.println("");
        System.out.println("Without roe: ");
        for(int i=0; i<learningSet; i++) {
            System.out.println(withoutRoeDeer.get(i));
        }
    }
    
    static void createFirstDataSet() {
        int tryNumber = -1;
        boolean isLoad;
        BufferedImage img = null;
        for(int i=0; i<learningSet; i++) { // zapisuje data set pro class "with roe-deer"
            isLoad = false;
            
            do {
                try{
                    tryNumber++;
                    img = ImageIO.read(new File("Data set/Learning set/With roe-deer/"+tryNumber+".jpg"));
                    isLoad = true;
                }catch (Exception e){}
                
            }while(!isLoad);
            withRoeDeer.addLast(tryNumber);
        }
        
        tryNumber = -1;
        for(int i=0; i<learningSet; i++) { // zapisuje data set pro class "without roe-deer"
            isLoad = false;
            do {
                tryNumber++;
                try{
                    img = ImageIO.read(new File("Data set/Learning set/Without roe-deer/"+tryNumber+".jpg"));
                    isLoad = true;
                }catch (Exception e){}
                
            }while(!isLoad);
            withoutRoeDeer.addLast(tryNumber);
        }
    }

    public NNAutoencoders(int i) {}
    
    static Picture getPicture(boolean ifColor, String mode) throws Exception {  //mode:normal - pro bezny studium(na learning sete), test-pro zkouseni na test sete
        Random rand = new Random();
        BufferedImage img = null;						//vychozi obrazek
        int choose = -1;							//0 - bude vybran obrazek s srnkou , 1 -bez 
        String path; 								//pro smerovani k mnozestvum obrazku
        String imgWay = null;                                                          //pro vysledny obrazek 
        if ("normal".equals(mode)) {
            path = "Learning set";
        }
        else {
            path = "Test set";
        }
        if ((counterWithRoe < learningSet)&&(counterWithoutRoe < learningSet)){
            choose = rand.nextInt(2);  
        }
        else { if((counterWithoutRoe == learningSet)&&(counterWithRoe < learningSet)) {
                choose = 0;
               }
               else if((counterWithRoe ==learningSet) && (counterWithoutRoe <learningSet)) {
                        choose = 1;
                    }
                    else {
                        throw new Exception("Chyba ve vyberu obrazku");
                    }
        }
        boolean isLaod;
        if (choose == 0) {
            img = ImageIO.read(new File("Data set/"+path+"/With roe-deer/"+withRoeDeer.get(counterWithRoe)+".jpg"));
            imgWay = "Data set/"+path+"/With roe-deer/"+withRoeDeer.get(counterWithRoe)+".jpg";
            counterWithRoe++;
        }
        else {
            img = ImageIO.read(new File("Data set/"+path+"/Without roe-deer/"+withoutRoeDeer.get(counterWithoutRoe)+".jpg"));
            imgWay = "Data set/"+path+"/Without roe-deer/"+withoutRoeDeer.get(counterWithoutRoe)+".jpg";
            counterWithoutRoe++;
        }
        Answer = choose;
        return ImgToRightPicture(imgWay, ifColor); 							
    }

    //Rika jestli obrazek uz byl zpracovan v dane ere
    static boolean isAlreadyBeen(int pictureNumber, int isRoeSet, boolean isLaod) {
        if(!isLaod) { //jestli jeste nemame spravne cislo obrazku vratime se zpet do cikla
            return true;
        }
        if(isRoeSet == 0) {
            if(!withRoeDeer.isEmpty()) {
                if(withRoeDeer.contains(pictureNumber)) {
                    return true;
                }
                else {
                    withRoeDeer.addLast(pictureNumber);
                    return false;
                }
            }
            else {
                withRoeDeer.addLast(pictureNumber);
                return false;
            }
        }
        else {
            if(!withoutRoeDeer.isEmpty()) {
                if(withoutRoeDeer.contains(pictureNumber)) {
                    return true;
                }
                else {
                    withoutRoeDeer.addLast(pictureNumber);
                    return false;
                }
            }
            else {
                withoutRoeDeer.addLast(pictureNumber);
                return false;
            }
        }
    }
    
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

    static int calculateResult(Picture picture) throws Exception {
        Convolution templ = net.convolutions.head;                              
        for (int i = 0; i < net.convolutions.size; i++) {
            templ.clearInputMass();                                                     //mazani zbytecnych dat
            templ.clearOutput();
            templ = templ.next;
        }
        Picture[] firstConvolution = new Picture[net.convolutions.size / Neuronet.counvolutionGroups];		//РјР°СЃСЃРёРІ РјР°СЃСЃРёРІРѕРІ?? [][,]		//prvni vrstva, convolution 11x11
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
        int index = 0;                  //cislo neuronu ktery vyhral
        Max = net.l2.outputs[0];
        for (int i = 0; i < net.l2.outputs.length; i++) {
            if (Max < net.l2.outputs[i]) {
                Max = net.l2.outputs[i];
                index = i;
            }
        }

        templ = net.convolutions.head;                              
        for (int i = 0; i < net.convolutions.size; i++) {
            templ.countAverageInput();                                          //spocitani prumerneho vstupu
            templ.countAverageOutput();
            templ = templ.next;
        }
        return index;
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
//        sendReport("Start learning", 0);
        long start = System.nanoTime();
        long checkpoint, runTime;
        double finalTest;
        String struct = writeSrtuct();  //popisuje strukturu seti
        RealTimeChart.firstShowChart();
        RealTimeChart.displayValueOnChart(100, false);
//        do {
//            doNNMoreClever();
//            finalTest = test("test");
////            checkpoint = System.nanoTime();
////            runTime = checkpoint - start;
////            if(runTime > 60) 
////                runTime /= 60;
//            System.out.println("Final test:" + finalTest);
////            serializace("middleResult");
//            struct = writeSrtuct();
//            sendReport(struct, finalTest);
//        } while(finalTest < 100);
//        System.out.println("Final test " + finalTest);
        doNNMoreClever();
        writeSrtuct();
        try {
            serializace("normal");
        } catch (Exception e){
            System.out.println("Serialization error in 'normal' way");
        }
    }
    
    static void sendReport(String struct, double finalTest) {
        EmailClient em;
        if("Start learning".equals(struct)) {
             em = new EmailClient("ustinov_nikita_01@mail.ru", "report", "Start learning");
        }
        else {
            String report = progressInfo + "\r\n" + finalTest  + "\r\n" + struct;
            em = new EmailClient("ustinov_nikita_01@mail.ru", "report", report);
        }
    }
    
    static void doNNMoreClever() throws Exception {
        double[] err = new double[Neuronet.tretiVrstva];
        int iteration = 1;
        double lokError = 0;
        int lokResult = 0;
        double errorMin = 100;
        int testValue = 0;
        int bestTestValue = 0;
        int gradNull = 0;
        double lastResult = -1;
        int changeIteration = 0;
        boolean shakeFlag = false;
        double lastError = -1;
        int epochaWithoutNewNeuron = 0;
        boolean isEndOfLearning = false;
        boolean testFlag = false;

        while (!isEndOfLearning) {
            lokResult = calculateResult(getPicture(true, "normal"));
            Neuron templ3 = net.l2.head;
            if((Double.isNaN(templ3.weights[1])||(Double.isNaN(net.l0.head.weights[1]))||(Double.isNaN(net.l1.head.weights[1])))) { //kontrola siti
                System.out.println("NAN NAN NAN iteration "+ iteration);   //kontrola neuronu
            }
            for (int i = 0; i < Neuronet.tretiVrstva; i++) {
                if (Answer == i) {
                    err[i] = Max - templ3.output;				//zapisuje signal chyby vystupni vrstvy
                } else {
                    err[i] = 0 - templ3.output;
                }
                templ3 = templ3.next;
            }
            Neuron templ2;
            templ3 = net.l2.head;
            for (int i = 0; i < Neuronet.tretiVrstva; i++) {
                templ2 = net.l1.head;
                templ3.grad = 0.388 * (1.716 - templ3.output) * (1.716 + templ3.output) * err[i];//1.7159   //pocita gradient pro vystupni vyrstvu
                for (int j = 0; j < Neuronet.druhaVrstva; j++) {
                    templ3.weights[j] += net.speedLFCN * templ2.output * templ3.grad;     //pocita vahy pro vystupni vrstvu
                    templ2 = templ2.next;
                }
                templ3 = templ3.next;
            }

            double grad = 0;
            Neuron templ1;
            templ2 = net.l1.head;
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
                    templ2.weights[j] += net.speedLFCN * templ1.output * templ2.grad;
                    templ1 = templ1.next;
                }
                templ2 = templ2.next;
            }

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
                    templ1.weights[j] += net.speedLFCN * templ1.input[j] * grad;
                }
                templ1 = templ1.next;
            }
            
            
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
                double delta;
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
            
            if (iteration % (learningSet*2) == 0) {
                if ( (iteration / (learningSet*2) == 900) || (iteration / (learningSet*2)== 1800)){
                    net.speedL1CL *= 0.75;
                    net.speedL2CL *= 0.75;
                    net.speedL3CL *= 0.75;
                    net.speedLFCN *= 0.75;
                }
                if(!testFlag) {
                    lokError = lokError/(learningSet*2)*100;
                    if(lastResult != lokError) {
                        lastResult = lokError;
                        changeIteration = iteration;
                    }
                    RealTimeChart.displayValueOnChart(lokError, false);
                    if (lokError < errorMin) {
                        writeNewTestResult(lokError);                    
                        errorMin = lokError;
                        System.out.println("");
                        System.out.println("epoch: "+iteration / (learningSet*2)+"  >>>>>   Minimalni chyba: " +(int)errorMin+"%");
                        sendReport("Min error", errorMin);
                        serializace("Best weights");
                        if(errorMin < 20)
                            testFlag = true;
                    }
                    else {
                        System.out.println("Error in "+iteration / (learningSet*2)+" epoch: "+(int)lokError+"%");
                    }
                }else {
                    testValue = test("normal");
                    newEpoch();
                    if(lastResult != testValue) {
                        lastResult = testValue;
                        changeIteration = iteration;
                    }
                    RealTimeChart.displayValueOnChart(testValue, true);
                    if (testValue > bestTestValue) {
                        bestTestValue = testValue;
                        System.out.println("");
                        System.out.println("Better result >>>>>>>>>>>  "+bestTestValue);
                        System.out.println("");
                        serializace("Best weights");
                        sendReport("New best value", testValue);
                    }
                    
                    if(testValue > 96)
                        isEndOfLearning = true;
                    System.out.println(iteration / (learningSet*2)+" epoch: "+testValue);
                }
               

                if ((iteration - changeIteration)/(learningSet*2) > 20) {
                     net.speedL1CL *= 5;
                     net.speedL2CL *= 5;
                     net.speedL3CL *= 5;
                     net.speedLFCN *= 5;
                     shakeFlag = true;
                     System.out.println("Shake flag true");
                 } 
                
//                writeProgressInfo(iteration, testValue);
                
//                if (epochaWithoutNewNeuron>=50) {
//                    System.out.println("epochaWithoutNewNeuron "+ epochaWithoutNewNeuron);
//                    System.out.println("Error difference " + (lastError-lokError/iteration*100));
//                    if (Math.abs(lastError-lokError/iteration*100) < 1) {	
//                        epochaWithoutNewNeuron=0;
//                        addNeuron();
//                        writeSrtuct();
//                    }
//                }
//                else 
//                    epochaWithoutNewNeuron++;
                
                if (iteration % 1000000 == 0) {
                    _convolutionsInfo = null;
                }
                if (iteration % 1000000 == 0) {
                    progressInfo = null;
                }
                gradNull = 0;
                newEpoch();
                lokError = 0;
            }
            lastError = lokError / iteration * 100;
            iteration++;
        }
        serializace("weights");
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
    
    static void writeNewTestResult(double ErrorMin) throws Exception {
        String testResult = "With:";
        testResult += checkOneClass("With");
        testResult+="\r\n";
        testResult+="Without:";
        testResult+="\r\n";
        testResult += checkOneClass("Without");
        write("Analyz/error"+ErrorMin, testResult);
        serializace("Analyz/Weights/error"+ErrorMin);
    }

    static void writeProgressInfo(int iteration, double testValue) {
        progressInfo += "epoch = " + iteration / (learningSet*2) + " test value = " + testValue + "\r\n";
        System.out.println("epoch = " + iteration / (learningSet*2) + " test value = " + testValue);
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

    static int test(String mode) throws Exception {  //normal - pro bezny studium(na learning sete), test-pro zkouseni na test sete
        newEpoch();
        int vysledek=0;
        boolean isDifferentOutput = false;		//rika jestli NN vydava ruzne hodnoty
//        int firstOutput = calculateResult(getPicture(true, mode));
        int vysOperace = -2;
        int limit = (int)(learningSet * 2); //pocet testovych iteraci pro (learning set)
        if(mode=="test") {
            limit = testSet;
        }
        for (int i=0; i<limit; i++) {
            vysOperace = calculateResult(getPicture(true, mode));
            if (Answer == vysOperace) 
                vysledek += 1;
            else
                vysledek += 0;
//            if (firstOutput != vysOperace) 
//                isDifferentOutput = true;
        }
//        if(!isDifferentOutput){
//            stillOneValue = vysOperace;
//        }
//        else {
//            stillOneValue = -1;
//        }
        double k = 1;
//        if ("test".equals(mode))
//            k = k/limit*100;
//        else {
         k= k/limit*100;
//        }
        return (int)(vysledek*k);
    }
    
    static void exam() throws Exception {
            LinkedList<Integer> pointList = new LinkedList<>();
//            deserializationNN();
            BufferedImage inputImage = ImageIO.read(new File("Data set/Exam set/segmentation on video-frame-202 with DoG 43.jpg"));
            BufferedImage result = inputImage;
            BufferedImage templ;
            boolean isHere = false; //rekne nam, jestli na malem obrazku je srnka
            int size = Neuronet.inputPictureFormat;  //velikost receptivniho pole
            for(int i=0; i<inputImage.getHeight()-size; i+=17) { //y demenze (i<inputImage.Height-size)
                for(int j=0; j<inputImage.getWidth()-size;  j+=17) { // x demenze (j<inputImage.Width-size)
                    try {
                        templ = cutImage(j,i, size, inputImage);
                        if(isNeedsToProcessing(templ))
                            isHere = findRoeDeer(templ);
                        else 
                            isHere = false;
                    }catch(Exception e){}
                    if (isHere) {
                        pointList.addLast(j);   //pridani souradnice X
                        pointList.addLast(i);   //pridani souradnice Y
                    }
                }
                System.out.println("i: "+i);
            }
            int counter = 0;
            int x,y;
            if(pointList.isEmpty()){
                throw new Exception("Nenalezen zadny jelen");
            }
            int templPoint = pointList.getFirst();
            while(counter<pointList.size()) {
                x = pointList.get(counter);
                y = pointList.get(counter+1);
//                saveToDatasetAsRecognized(x, y, size, result, counter);
                result = drawRectungle(x, y, size, result);
                counter += 2;
            }
            ImageIO.write(result,"jpg", new File("result 1.jpg"));
        }
    
    static void exam2() throws Exception {
        int counterAreasToProcess = 0;
        LinkedList<Integer> pointList = new LinkedList<>();
        Segmentation segment = new Segmentation();
        BufferedImage inputImage = ImageIO.read(new File("Data set/Exam set/segmentation on video-frame-5(with roe-deer).jpg"));
        BufferedImage result = inputImage;
        inputImage = segment.segmentPicture(inputImage);
        ImageIO.write(inputImage,"jpg", new File("segmented picture.jpg"));
        BufferedImage templ;
        boolean isHere = false; //rekne nam, jestli na malem obrazku je srnka
        int size = Neuronet.inputPictureFormat;  //velikost receptivniho pole
        for(int i=0; i<inputImage.getHeight()-size; i+=17) { //y demenze (i<inputImage.Height-size)
            for(int j=0; j<inputImage.getWidth()-size;  j+=17) { // x demenze (j<inputImage.Width-size)
                try {
                    templ = cutImage(j,i, size, inputImage);
                    if(isNeedsToProcessing(templ)){
                        counterAreasToProcess++;
                        templ = cutImage(j, j, size, result);
                        isHere = findRoeDeer(templ);
                    }
                    else 
                        isHere = false;
                }catch(Exception e){}
                if (isHere) {
                    pointList.addLast(j);   //pridani souradnice X
                    pointList.addLast(i);   //pridani souradnice Y
                }
            }
            System.out.println("i: "+i);
        }
        int counter = 0;
        int x,y;
        if(pointList.isEmpty()){
            throw new Exception("Nenalezen zadny jelen");
        }
        int templPoint = pointList.getFirst();
        savePositivePicture(pointList,result, size);
        while(counter<pointList.size()) {
            x = pointList.get(counter);
            y = pointList.get(counter+1);
//            saveToDatasetAsRecognized(x, y, size, result, counter);
            result = drawRectungle(x, y, size, result);
            counter += 2;
        }
        ImageIO.write(result,"jpg", new File("result 1.jpg"));
        System.out.println("Process zone: "+counterAreasToProcess);
    }
    
    static void savePositivePicture(LinkedList<Integer> pointList, BufferedImage image, int size) throws Exception {
        int x,y,counter = 0;
        while(counter<pointList.size()) {
            x = pointList.get(counter);
            y = pointList.get(counter+1);
            saveToDatasetAsRecognized(x, y, size, image, counter);
            counter += 2;
        }
    }
    
    static void checkOnePicture() throws IOException, Exception {
        String path = "Data set/Learning set/Without roe-deer/134.jpg";
        BufferedImage inputImage = ImageIO.read(new File(path));
        boolean tracker;
        tracker = findRoeDeer(inputImage);
        tracker = tracker;
    }
    
    static String checkOneClass(String classWay) throws IOException, Exception {
        int tryNumber = -1;
        boolean nextPicture = false;
        String output = null;
        for(int i=0; i<learningSet; i++) {
            while(!nextPicture) {
                tryNumber++;
                try{
                    String path = "Data set/Learning set/"+classWay+" roe-deer/"+tryNumber+".jpg";
                    BufferedImage inputImage = ImageIO.read(new File(path));
                    nextPicture = true;
                    if(classWay == "With") {
                        if(findRoeDeer(inputImage)){
//                            System.out.println(tryNumber+" ok");
                            output+=(tryNumber+" ok");
                            output +="\r\n";
                        }
                        else {
//                            System.out.println(tryNumber+" wrong");
                            output+=(tryNumber+" wrong");
                            output +="\r\n";
                        }
                    } else {
                        if(findRoeDeer(inputImage)){
//                            System.out.println(tryNumber+" wrong");
                            output+=(tryNumber+" wrong");
                            output +="\r\n";
                        }
                        else {
//                            System.out.println(tryNumber+" ok");
                            output+=(tryNumber+" ok");
                            output +="\r\n";
                        }
                    }
                }
                catch (Exception e) {}
            }
            nextPicture = false;
        }
        return output;
    }
    
    static void saveToDatasetAsRecognized(int x, int y, int size, BufferedImage result, int counter) throws Exception {
        BufferedImage templ = cutImage(x, y, size, result);
        ImageIO.write(templ,"jpg", new File("Test positive/"+counter+".jpg"));
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
    
    static boolean isNeedsToProcessing(BufferedImage input) {
        boolean isWhite = true;
//        HashMap<Integer,Integer> histogram = new HashMap<>();
        Color white = new Color(255, 255, 255);
        int rgbWhite = white.getRGB();
        int rgb,r,g,b, h=0;
        float [] hsb;
        for(int i=0; i<input.getHeight(); i++) {
            for(int j=0; j<input.getWidth(); j++) {
                if(input.getRGB(j, i) != rgbWhite){
                    isWhite = false;
                    break;
                }
            }
            if(!isWhite)
                break;
        }
        if(isWhite)
            return false;
        int counterRedPixels = 0;
//        int value;
        for(int i=0; i<input.getHeight(); i++) {
            for(int j=0; j<input.getWidth(); j++) {
                rgb = input.getRGB(j, i);
                r = (rgb >> 16) & 0xFF;
                g = (rgb >> 8) & 0xFF;
                b = (rgb >> 0) & 0xFF;
                hsb = Color.RGBtoHSB(r, g, b, null);
                hsb[0] *= 100;
                h = Math.round(hsb[0]);//hue
//                if(!histogram.containsKey(h)){
//                    histogram.put(h, 1);
//                }
//                else {
//                   value = histogram.get(h);
//                   histogram.put(h, value+1);
//                }
                if(h==5){
                    h=h;
                }
                if((h > 83)||(h < 9)) {
                   counterRedPixels++; 
                }
            }
        }
//        System.out.println("h - v");
//        for(int i=0; i<histogram.size(); i++) {
//            if(histogram.containsKey(i))
//                System.out.println(i+" - "+histogram.get(i));
//        }
        if(counterRedPixels>21) 
            return true;
        else 
            return false;
    }

    static BufferedImage cutImage(int x, int y, int size, BufferedImage inputPicture) throws Exception {
        BufferedImage outputPicture;
        outputPicture = new BufferedImage(size, size, 5);
//        int x1 = x; //pro pocitani souradnic puvodnich pixelu
//        int y1 = y;
        try {
            for (int i = y; i < y+size; i++) {
                for (int j = x; j < x+size; j++) {
//                    int extract = inputPicture.getRGB(x1, y1);
//                    Color pixel = new Color(inputPicture.getRGB(x1, y1));
//                    outputPicture.SetPixel(j, i, pixel);
                    outputPicture.setRGB(j-x, i-y, inputPicture.getRGB(j, i));
//                    x1++;
                }
//                x1 = x;
//                y1++;
            }
            return outputPicture;
        }catch (Exception e) {
            throw new Exception("Nepovedlo vzit maly obrazek");
        }
//        return new BufferedImage(size, size, 6);
    }
        
    static boolean findRoeDeer(BufferedImage input) throws Exception {
        Picture inputPicture = ImgToRightPicture(input, true);
        int result = calculateResult(inputPicture);
        if(result == 0) 
            return true;
        else 
            return false;
    }
    
    static void serializace(String wayOfSaving) throws Exception {
//        String fileName = null;
//        if (wayOfSaving == "normal") {
//            fileName = "weights";
//        } else {
//            fileName = "BestWeights";;
//        }
        FileOutputStream fos = new FileOutputStream(wayOfSaving+".out");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(net);
        oos.close();
    }

    static Neuronet deserializationNN(String way) throws Exception {//deserializuje NN z dat-faila
        net = deseralizace(way);
        Neuronet.inputLength = net.inputLengthOwn;
        Neuronet.prvniVrstva = net.prvniVrstvaOwn;
        Neuronet.druhaVrstva = net.druhaVrstvaOwn;
        Neuronet.tretiVrstva = net.tretiVrstvaOwn;
        return net;
    }
    
    static Neuronet deseralizace(String way) throws Exception {
        try {
            way += ".out";
            Neuronet net = null;
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(way));
            net = (Neuronet) in.readObject();
            return net;
        } catch (Exception e) {
            throw new Exception("Nepovedlo deserializovat NN");
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

    static void write(String fileName, String text) {
        File file = new File(fileName+".txt");
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
    
    static void clearScreen() {  
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
   }  
}
