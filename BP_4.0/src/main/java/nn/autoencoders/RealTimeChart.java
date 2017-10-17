package nn.autoencoders;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.bytedeco.javacpp.opencv_core;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.style.XYStyler;

public class RealTimeChart {
    
      static List<Integer> dataListY= new LinkedList<>();
      static List<Integer> dataListX= new LinkedList<>();
      static int counter = 0;
      static double[][] initdata = new double[5][5];
      static final XYChart chartError  = QuickChart.getChart("NN Lerning", "Epochs", "error", "error", initdata[0], initdata[1]);
      static final XYChart chartTest  = QuickChart.getChart("NN Lerning", "Epochs", "Test value", "testValue", initdata[0], initdata[1]);
      static boolean firstInput = true;
      static final SwingWrapper<XYChart> sw = new SwingWrapper<XYChart>(chartError);
      static final SwingWrapper<XYChart> swTest = new SwingWrapper<XYChart>(chartTest);
      
      public static void firstShowChart() throws Exception {
        sw.displayChart();
        chartError.getStyler().setYAxisMax(100.0);
//        chartError.getStyler().setYAxisMax(0.0);
        chartTest.getStyler().setYAxisMax(100.0);
//        chartTest.getStyler().setYAxisMax(0.0);
      }
      
       public static void displayValueOnChart(double k, boolean isTestMode) throws IOException {
        if(!isTestMode) {
            dataListY.add((int)k);
            dataListX.add(counter);
            counter++;
            if(dataListY.size()> 300){
                dataListX.remove(0);
                dataListY.remove(0);
            }
            javax.swing.SwingUtilities.invokeLater(new Runnable() {

               @Override
               public void run() {
                   if(dataListX.size()!=dataListY.size()){
                       counter= counter;
                   }
                   chartError.updateXYSeries("error", dataListX, dataListY, null);
                   sw.repaintChart();
               }
             });
            
          }
        else {
            if(firstInput) {
                BitmapEncoder.saveBitmap(chartError, "ErrorChart", BitmapFormat.PNG);
                dataListY= new LinkedList<>();
                dataListX= new LinkedList<>();
                swTest.displayChart();
                firstInput = false;
            }
            dataListY.add((int)k);
            dataListX.add(counter);
            counter++;
            if(dataListY.size()> 300){
                dataListX.remove(0);
                dataListY.remove(0);
            }
            javax.swing.SwingUtilities.invokeLater(new Runnable() {

               @Override
               public void run() {
                   if(dataListX.size()!=dataListY.size()){
                       counter= counter;
                   }
                   chartTest.updateXYSeries("testValue", dataListX, dataListY, null);
                   swTest.repaintChart();
               }
             });
        }
        
        
       }
       
    }