package com.mycompany.chart;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

public class SimpleRealTime {
    
      static List<Integer> dataListY= new LinkedList<>();
      static List<Integer> dataListX= new LinkedList<>();
      static int counter = 0;
      
      public static void main(String[] args) throws Exception {
    
        double phase = 0;
        double[][] initdata = getSineData(phase);
    
        // Create Chart
        final XYChart chart = QuickChart.getChart("Simple XChart Real-time Demo", "Radians", "Sine", "sine", initdata[0], initdata[1]);
//        final XYChaty chart2 = QuickChart.getChart(null, null, null, null, xData, yData)
        // Show it
        final SwingWrapper<XYChart> sw = new SwingWrapper<XYChart>(chart);
        sw.displayChart();
        while (true) {
    
//          phase += 2 * Math.PI * 2 / 20.0;
        Random rand = new Random();
        double k = rand.nextDouble()*100;
        dataListY.add((int)k);
        dataListX.add(counter);
        counter++;
        if(dataListY.size()> 50){
            dataListX.remove(0);
            dataListY.remove(0);
//            dataListX = new LinkedList(dataListX.subList((dataListX.size()-19), dataListX.size()-1));
//            dataListY = new LinkedList(dataListY.subList((dataListY.size()-19), dataListY.size()-1));
            
//            dataListY =  dataListY.subList((dataListY.size()- 20), dataListY.size()-1);
//            dataListX =  dataListX.subList((dataListX.size()- 20), dataListX.size()-1);
            
        }
        
        Thread.sleep(200);
//        final double[][] data = getRandomData((int) k);
//          final double[][] data = getSineData(phase);
    
          javax.swing.SwingUtilities.invokeLater(new Runnable() {
    
            @Override
            public void run() {
    
//              chart.updateXYSeries("sine", data[0], data[1], null);
                if(dataListX.size()!=dataListY.size()){
                    counter= counter;
                }
                chart.updateXYSeries("sine", dataListX, dataListY, null);
//                sw.displayChartMatrix();
//                sw.repaintChart(1);
              sw.repaintChart();
            }
          });
        }
    
      }
    
      static double[][] getRandomData(int k) {
        double[] xData = new double[100];
        double[] yData = new double[100];
        for (int i = 0; i < xData.length; i++) {
//          double radians =  (2 * Math.PI / xData.length * i);
//          xData[i] = dataList.size() - i;
          yData[i] = k;
        }
        return new double[][] { xData, yData };
      }
      
      private static double[][] getSineData(double phase) {
    
        double[] xData = new double[100];
        double[] yData = new double[100];
        for (int i = 0; i < xData.length; i++) {
          double radians = phase + (2 * Math.PI / xData.length * i);
          xData[i] = radians;
          yData[i] = Math.sin(radians);
        }
        return new double[][] { xData, yData };
      }
    }