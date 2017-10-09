package com.mycompany.chart;

import java.io.IOException;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;


public class Main {

    public static void main(String[] args) throws IOException {
        doExample();
    }
    
    static void doExample() throws IOException {
        double[] xData = new double[] {-3, -2, -1, 0, 1, 2, 3};
        double[] yData = new double[] {9, 4, 1, 0, 1, 4, 9 };

        // Create Chart
        XYChart chart = QuickChart.getChart("Sample Chart", "X", "Y", "y(x)", xData, yData);
        
        // Show it
        new SwingWrapper(chart).displayChart();

        // Save it
        BitmapEncoder.saveBitmap(chart, "./Sample_Chart", BitmapFormat.PNG);

        // or save it in high-res
        BitmapEncoder.saveBitmapWithDPI(chart, "./Sample_Chart_300_DPI", BitmapFormat.PNG, 300);
    }

}

