package info;

import info.templates.Plane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

import java.util.ArrayList;
import java.util.List;


public class ThrustGraph {
    List<List<Double>> thrusts;
    List<List<Double>> ttws;
    List<Integer> yAxis;
    String planeName;
    String title1;
    String xAxisLabel;
    String yAxisLabel;
    String folder;
    List<Plane> planes;
    public double alt;
    List<Integer> fuels;
    int minSpeed;
    int maxSpeed = 0;

    public ThrustGraph(List<Integer> yAxis, String planeName,
                       String title, String xAxisLabel, String yAxisLabel, String folder, List<Plane> planes, double alt, List<Integer> fuels, int minSpeed, int maxSpeed) throws HeadlessException {

        if ((maxSpeed - minSpeed) > 150 && minSpeed >= 0) {
            this.minSpeed = minSpeed;
            this.maxSpeed = maxSpeed;
        }
        this.fuels = fuels;
        this.alt = alt;
        this.planes = planes;
        this.yAxis = yAxis;

        thrusts = new ArrayList<>();
        ttws = new ArrayList<>();
        long d = System.nanoTime();
        for (int i = 0; i < planes.size(); i++) {
            Plane p = planes.get(i);
            thrusts.add(getThrust(p));
            ttws.add(getTtw(p, thrusts.get(i), fuels.get(i)));
        }
        long b = System.nanoTime();
        System.out.println("Time: " + (b - d));
        this.planeName = planeName;
        this.title1 = title;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.folder = folder;
    }

    public File init() {
        XYDataset dataset = createDataset();
        JFreeChart chart = createNormalChart(dataset);

        var file = new File("Data/" + folder + "/" + planeName.replace("%", "") + "_" + title1.replace("%", "") + " " + Plane.gameVer + ".png");
        try {
            ChartUtils.saveChartAsPNG(file, chart, 2500, 900);
        } catch (IOException e) {
            System.out.println(planeName);
            throw new RuntimeException(e);
        }
        return file;
    }

    private JFreeChart createNormalChart(XYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                planeName + " " + title1 + "m",
                xAxisLabel + "",
                yAxisLabel + "",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();
        var renderer = new XYSplineRenderer();
      //  var renderer = new XYLineAndShapeRenderer();

        plot.setRenderer(renderer);

        List<Color> colors = Arrays.asList(Color.BLUE, Color.RED, new Color(0, 100, 0), Color.MAGENTA, Color.ORANGE, Color.CYAN, Color.decode("#4b0082"));

        for (int i = 0; i < planes.size(); i++) {
            Color color = colors.get(i % colors.size());
            renderer.setSeriesPaint(i, color);
            renderer.setSeriesPaint(i + planes.size(), color);
        }


        NumberAxis yAxis1Temp = new NumberAxis("Thrust to weight");
        double ttwMax = ttws.stream().mapToDouble(ttw -> ttw.stream().max(Double::compare).get()).max().getAsDouble();
        double ttwMin = ttws.stream().mapToDouble(ttw -> ttw.stream().min(Double::compare).get()).min().getAsDouble();
        yAxis1Temp.setUpperBound(ttwMax + 0.1);
        yAxis1Temp.setLowerBound(ttwMin - 0.1);
        plot.setRangeAxis(1, yAxis1Temp);
        plot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);

        XYDataset datasetTemperature = makeTTWChart();
        plot.setDataset(2, datasetTemperature);
        plot.mapDatasetToDomainAxis(2, 0);
        plot.mapDatasetToRangeAxis(2, 1);
        var ttwSplineRenderer = new XYSplineRenderer();
       // var ttwSplineRenderer = new XYLineAndShapeRenderer();

        for (int i = 0; i < planes.size(); i++) {
            Color color = colors.get(i % colors.size());
            ttwSplineRenderer.setSeriesPaint(i, color);
            ttwSplineRenderer.setSeriesStroke(i, new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{6.0f, 6.0f}, 0.0f));
        }

        plot.setRenderer(2, ttwSplineRenderer);



        plot.setBackgroundPaint(Color.white);

        var axis = plot.getRangeAxis();
        double thrustMin = thrusts.stream().mapToDouble(thrust -> thrust.stream().min(Double::compare).get()).min().getAsDouble();
        double thrustMax = thrusts.stream().mapToDouble(thrust -> thrust.stream().max(Double::compare).get()).max().getAsDouble();
        axis.setRange(thrustMin - 150, thrustMax + 150);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);
        if (maxSpeed != 0) {
            NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
            xAxis.setRange(minSpeed, maxSpeed);
            double maxThrust = Double.NEGATIVE_INFINITY;
            double minThrust = Double.POSITIVE_INFINITY;
            for (Plane plane : planes) {
                for (int speed = minSpeed; speed <= maxSpeed; speed += 10) {
                    double thrust = plane.getThrust(alt, speed, plane.thrusts);
                    maxThrust = Math.max(maxThrust, thrust);
                    minThrust = Math.min(minThrust, thrust);
                }
            }
            NumberAxis yAxis1 = (NumberAxis) plot.getRangeAxis();
            yAxis1.setRange(minThrust - 150, maxThrust + 150);


        }
        chart.getLegend().setFrame(BlockBorder.NONE);

        chart.setTitle(new TextTitle(planeName + " " + title1 + "m",
                        new Font("Serif", java.awt.Font.BOLD, 18)
                )
        );

        return chart;
    }

    public XYDataset makeTTWChart() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (int i = 0; i < planes.size(); i++) {
            Plane p = planes.get(i);
            XYSeries series = new XYSeries("Thrust to weight " + p.actualName);
            int numDataPoints = Math.min(p.speedList.size(), ttws.get(i).size()); // Get number of data points based on size of both lists
            for (int j = 0; j < numDataPoints; j++) {
                series.add(p.speedList.get(j), ttws.get(i).get(j));
            }
            dataset.addSeries(series);
        }
        return dataset;
    }


    List<Double> getThrust(Plane p) {
        List<Double> thrust = new ArrayList<>();

        for (int i = 0; i < p.speedList.size(); i++) {
            thrust.add(p.getThrust(alt, p.speedList.get(i), p.thrusts));
        }
        if (p.hasTwoDiffEngineTypes && p.dualEngine && p.thrusts2 != null) {
            for (int i = 0; i < p.speedList.size(); i++) {
                thrust.set(i, thrust.get(i) + p.getThrust(alt, p.speedList.get(i), p.thrusts2));
            }
        }
        return thrust;
    }

    List<Double> getTtw(Plane p, List<Double> thrust1, int fuel) {
        List<Double> ttw = new ArrayList<>();
        for (int i = 0; i < p.speedList.size(); i++) {
            ttw.add(thrust1.get(i) / (Double.parseDouble(p.emptyWeight) + (Double.parseDouble(p.fuelWeight) * ((fuel + 0.0) / 100))));
        }
        return ttw;
    }

    private XYDataset createDataset() {
        var dataset = new XYSeriesCollection();
        for (int i = 0; i < planes.size(); i++) {
            Plane p = planes.get(i);
            String seriesKey = "Thrust " + p.actualName;
            if (dataset.getSeriesIndex(seriesKey) == -1) {
                var series = new XYSeries(seriesKey);
                int numDataPoints = Math.min(thrusts.get(i).size(), p.speedList.size());
                if (p.velType.equals("TAS"))
                    for (int j = 0; j < numDataPoints; j++) {
                        series.add(p.speedList.get(j), thrusts.get(i).get(j));
                    }
                else
                    for (int j = 0; j < numDataPoints; j++) {
                        series.add(getTASSpeed(p.speedList.get(j)), thrusts.get(i).get(j));
                    }

                dataset.addSeries(series);
            }
        }
        return dataset;
    }

    public static Plane getPlaneByGameversion(int id, int gameId) {
        Plane p = PlanesManager.getPlaneById(id);

        return null;
    }

    public static double getDensityAtAlt(int alt) {
        final double seaLevelTemp = 288.15;
        final double seaLevelPressure = 101325;
        final double tempLapseRate = -0.0065;
        final double gasConstant = 8.31447;
        final double molarMass = 0.0289644;
        final double gravity = 9.80665;

        double temperature = seaLevelTemp + tempLapseRate * alt;
        double pressure = seaLevelPressure * Math.pow((temperature / seaLevelTemp), (gravity * molarMass) / (-gasConstant * tempLapseRate));
        return (pressure * molarMass) / (gasConstant * temperature);
    }

    public double getTASSpeed(double iasSpeed) {
        final double airDensitySL = 1.225;
        double airDensity = airDensitySL * getDensityAtAlt((int) (alt));
        return iasSpeed * Math.sqrt(airDensitySL / airDensity);
    }

    public static double getPressureAtAlt(int alt) {
        final double seaLevelTemp = 288.15;
        final double seaLevelPressure = 101325;
        final double tempLapseRate = -0.0065;
        final double gasConstant = 8.31447;
        final double molarMass = 0.0289644;
        final double gravity = 9.80665;

        double temperature = seaLevelTemp + tempLapseRate * alt;
        double pressure = seaLevelPressure * Math.pow((temperature / seaLevelTemp), (gravity * molarMass) / (-gasConstant * tempLapseRate));
        return pressure;
    }


//these two are dogshit and wrong

    public static double calculateMachFromTas(double tas_kph, double altitude_m) {
        // Constants for standard atmosphere
        double sea_level_temperature_K = 288.15;
        double lapse_rate_K_per_m = -0.0065;
        double gas_constant_J_per_kg_K = 287.058;
        double specific_heat_ratio = 1.4;

        // Calculate temperature at altitude
        double temperature_at_altitude_K = sea_level_temperature_K + lapse_rate_K_per_m * altitude_m;

        // Calculate speed of sound at altitude
        double speed_of_sound_m_per_s = Math.sqrt(specific_heat_ratio * gas_constant_J_per_kg_K * temperature_at_altitude_K);

        // Convert TAS from kph to m/s
        double tas_m_per_s = tas_kph * 1000 / 3600;

        // Calculate Mach number
        double mach_number = tas_m_per_s / speed_of_sound_m_per_s;

        return mach_number;
    }


}