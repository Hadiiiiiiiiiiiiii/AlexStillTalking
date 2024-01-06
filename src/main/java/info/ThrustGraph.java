package info;

import info.templates.Plane;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;


public class ThrustGraph {
    List<List<Double>> thrusts;
    List<List<Double>> ttws;
    List<Integer> yAxis;
    String planeNames;
    String title1;
    String xAxisLabel;
    String yAxisLabel;
    String folder;
    List<Plane> planes;
    public double alt;
    List<Float> fuels;
    int minSpeed;
    int maxSpeed = 0;
    double aoa = 0;
    ArrayList<Double> accel = new ArrayList<>();
    InteractionHook hook;
    boolean levelFlight;
    ArrayList<ArrayList<Float>> tempDrag = new ArrayList<>();
    ArrayList<Float> allDrag = new ArrayList<>();
    double ttwMax;
    double ttwMin;
    double thrustMin;
    double thrustMax;
    double buffer;

    public ThrustGraph(List<Integer> yAxis, String planeName, String title, String xAxisLabel, String yAxisLabel, String folder, List<Plane> planes, double alt, List<Float> fuels, int minSpeed, int maxSpeed) throws HeadlessException {
        this(yAxis, planeName, title, xAxisLabel, yAxisLabel, folder, planes, alt, fuels, minSpeed, maxSpeed, 0.0, null, false);

    }

    public ThrustGraph(List<Integer> yAxis, String planeName, String title, String xAxisLabel, String yAxisLabel, String folder, List<Plane> planes, double alt, List<Float> fuels, int minSpeed, int maxSpeed, double aoa, InteractionHook hook, boolean levelFlight) throws HeadlessException {
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.fuels = fuels;
        this.alt = alt;
        this.planes = planes;
        this.yAxis = yAxis;
        this.hook = hook;
        this.levelFlight = levelFlight;

        thrusts = new ArrayList<>();
        ttws = new ArrayList<>();
        for (int i = 0; i < planes.size(); i++) {
            Plane p = planes.get(i);
            thrusts.add(getThrust(p));
            ttws.add(getTtw(p, thrusts.get(i), fuels.get(i)));
        }
        this.planeNames = planeName;
        this.title1 = title;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.folder = folder;
        buffer = 0;
    }


    public File init() {
        setupThrust();
        XYDataset dataset = createDataset();
        JFreeChart chart = createThrustChart(dataset);

        var file = new File("Data/" + folder + "/" + planeNames.replace("%", "") + "_" + title1.replace("%", "") + " " + Plane.gameVer + ".png");
        try {
            ChartUtils.saveChartAsPNG(file, chart, 2500, 900);
        } catch (IOException e) {
            System.out.println(planeNames);
            throw new RuntimeException(e);
        }
        long b = System.nanoTime();
        return file;
    }

    public File init2() {

        XYDataset dataset = createDragDataset();
        JFreeChart chart = createDragChart(dataset);

        var file = new File("Data/" + folder + "/" + planeNames.replace("%", "") + "_" + title1.replace("%", "") + " " + Plane.gameVer + ".png");
        try {
            ChartUtils.saveChartAsPNG(file, chart, 2500, 900);
        } catch (IOException e) {
            System.out.println(planeNames);
            throw new RuntimeException(e);
        }
        return file;
    }

    public File init3() {
        setupThrust();
        XYDataset dataset = createThrustAndDragDataset();
        JFreeChart chart = createDragThrustChart(dataset);

        var file = new File("Data/" + folder + "/" + planeNames.replace("%", "") + "_" + title1.replace("%", "") + " " + Plane.gameVer + ".png");
        try {
            ChartUtils.saveChartAsPNG(file, chart, 2500, 900);
        } catch (IOException e) {
            System.out.println(planeNames);
            throw new RuntimeException(e);
        }
        return file;
    }

    private JFreeChart createThrustChart(XYDataset dataset) {

        JFreeChart chart = ChartFactory.createXYLineChart(
                planeNames + " " + title1 + "m",
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
        yAxis1Temp.setUpperBound(ttwMax + 0.1);
        yAxis1Temp.setLowerBound(ttwMin - 0.1);
        plot.setRangeAxis(1, yAxis1Temp);
        plot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);

        XYDataset datasetTemperature = createTTWDataSet();

        XYDataset ripSpeedDataset = null;
        var maxrip = getRipSpeeds(planes).stream().max(Double::compare).get();
        if (maxrip < maxSpeed) {
            ripSpeedDataset = createRipSpeedDataset();
        } else if (maxSpeed == 0) {
            if (maxrip < planes.stream().flatMap(plane -> plane.speedList.stream()).max(Double::compare).get()) {
                ripSpeedDataset = createRipSpeedDataset();
            }
        }
        if (ripSpeedDataset != null) {
            plot.setDataset(3, ripSpeedDataset);
        }
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


        chart.setTitle(new TextTitle(new String(planeNames + " " + title1 + "m "),
                new Font("Serif", java.awt.Font.BOLD, 18)
        ));


        return chart;
    }

    void setupThrust() {
        thrustMin = thrusts.stream().mapToDouble(thrust -> thrust.stream().min(Double::compare).get()).min().getAsDouble();
        thrustMax = thrusts.stream().mapToDouble(thrust -> thrust.stream().max(Double::compare).get()).max().getAsDouble();

        for (int i = 0; i < planes.size(); i++) {
            double rip = Double.parseDouble(planes.get(i).ripSpeedKph);
            double thrust = planes.get(i).getThrust(alt, rip, planes.get(i).thrusts);
            double buff = 0.02 * thrust;
            if (buffer < buff)
                buffer = buff;
        }
        ttwMax = ttws.stream().mapToDouble(ttw -> ttw.stream().max(Double::compare).get()).max().getAsDouble();
        ttwMin = ttws.stream().mapToDouble(ttw -> ttw.stream().min(Double::compare).get()).min().getAsDouble();
    }

    private JFreeChart createDragThrustChart(XYDataset dataset) {

        JFreeChart chart = ChartFactory.createXYLineChart(
                title1,
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
        plot.setRenderer(renderer);

        List<Color> colors = Arrays.asList(Color.BLUE, Color.RED, new Color(0, 100, 0), Color.MAGENTA, Color.ORANGE, Color.CYAN, Color.decode("#4b0082"));
        XYDataset dragDataset = createAccelerationDataset();

        NumberAxis accelaxis = new NumberAxis("Acceleration in m/s");
        // System.out.println(accel);
        double accelMax = accel.stream().max(Double::compare).get();
        double accelMin = accel.stream().min(Double::compare).get();
        accelaxis.setUpperBound(accelMax + 0.3);
        accelaxis.setLowerBound(accelMin - 0.3);
        plot.setRangeAxis(1, accelaxis);
        plot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);

        plot.setDataset(2, dragDataset);
        plot.mapDatasetToDomainAxis(2, 0);
        plot.mapDatasetToRangeAxis(2, 1);
        var accelRenderer = new XYSplineRenderer();
        // var ttwSplineRenderer = new XYLineAndShapeRenderer();

        for (int i = 0; i < planes.size(); i++) {
            Color color = colors.get(i % colors.size());
            accelRenderer.setSeriesPaint(i, color);
            accelRenderer.setSeriesStroke(i, new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{6.0f, 6.0f}, 0.0f));
        }

        plot.setRenderer(2, accelRenderer);
        for (int i = 0; i < planes.size(); i++) {
            Color color = colors.get(i % colors.size());
            renderer.setSeriesPaint(i, color);
            renderer.setSeriesPaint(i + planes.size(), color);
        }


        var dragRenderer = new XYSplineRenderer();
        for (int i = 0; i < planes.size(); i++) {
            Color color = colors.get(i % colors.size());
            dragRenderer.setSeriesPaint(i, color);
            dragRenderer.setSeriesStroke(i, new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{6.0f, 6.0f}, 0.0f));
        }
        plot.setRenderer(1, dragRenderer);
        XYDataset ripSpeedDataset = null;
        var maxrip = getRipSpeeds(planes).stream().max(Double::compare).get();
        if (maxrip < maxSpeed) {
            ripSpeedDataset = createRipSpeedDataset();
        } else if (maxSpeed == 0) {
            if (maxrip < planes.stream().flatMap(plane -> plane.speedList.stream()).max(Double::compare).get()) {
                ripSpeedDataset = createRipSpeedDataset();
            }
        }
        if (ripSpeedDataset != null) {
            plot.setDataset(3, ripSpeedDataset);
        }
        plot.mapDatasetToDomainAxis(3, 0);
        plot.mapDatasetToRangeAxis(3, 0);

        var ripSpeedRenderer = new XYSplineRenderer();

        for (int i = 0; i < planes.size(); i++) {
            Color color = colors.get(i % colors.size());
            ripSpeedRenderer.setSeriesPaint(i, color);
        }

        plot.setRenderer(3, ripSpeedRenderer);

        plot.setBackgroundPaint(Color.white);

        var axis = plot.getRangeAxis();

        double dragMin = allDrag.stream().min(Double::compare).get();
        double dragMax = allDrag.stream().max(Double::compare).get();
        double thrustMin = thrusts.stream().mapToDouble(thrust -> thrust.stream().min(Double::compare).get()).min().getAsDouble();
        double thrustMax = thrusts.stream().mapToDouble(thrust -> thrust.stream().max(Double::compare).get()).max().getAsDouble();
        double maxx = Double.max(thrustMax, dragMax);
        double min = Double.min(thrustMin, dragMin);

        axis.setRange(min - 100, maxx + 100);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);
        if (maxSpeed != 0) {
            NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
            xAxis.setRange(minSpeed, maxSpeed);
        }
        chart.getLegend().setFrame(BlockBorder.NONE);

        String flightStatus;
        if (levelFlight) {
            flightStatus = "maintaining level flight";
        } else {
            flightStatus = "with " + aoa + "° aoa";
        }

        chart.setTitle(new TextTitle(new String(planeNames + " " + title1 + "m " + flightStatus),
                new Font("Serif", java.awt.Font.BOLD, 18)
        ));


        return chart;
    }

    private ArrayList<Double> getRipSpeeds(List<Plane> planes) {
        ArrayList<Double> ripSpeeds = new ArrayList<>();
        for (Plane p : planes) {
            double rip = Double.parseDouble(p.ripSpeedKph);
            double tasSpeed = Math.min(calculateTasFromMach(Double.parseDouble(p.ripSpeedMach), alt), getTASSpeed((int) rip));
            ripSpeeds.add(tasSpeed);
        }
        return ripSpeeds;
    }

    private JFreeChart createDragChart(XYDataset dataset) {
        String chartTitle = planeNames + " " + title1 + "m";
        if (levelFlight) {
            chartTitle += " level flight";
        } else {
            chartTitle += " with " + aoa + "° aoa";
        }

        JFreeChart chart = ChartFactory.createXYLineChart(
                chartTitle,
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
        plot.setRenderer(renderer);

        List<Color> colors = Arrays.asList(Color.BLUE, Color.RED, new Color(0, 100, 0), Color.MAGENTA, Color.ORANGE, Color.CYAN, Color.decode("#4b0082"));

        for (int i = 0; i < planes.size(); i++) {
            Color color = colors.get(i % colors.size());
            renderer.setSeriesPaint(i, color);
            renderer.setSeriesPaint(i + planes.size(), color);
        }

        plot.setBackgroundPaint(Color.white);

        var axis = plot.getRangeAxis();
        double dragMin = allDrag.stream().min(Double::compare).get();
        double dragMax = allDrag.stream().max(Double::compare).get();
        axis.setRange(dragMin, dragMax + 150);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);
        if (maxSpeed != 0) {
            NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
            xAxis.setRange(minSpeed, maxSpeed);
        }
        chart.getLegend().setFrame(BlockBorder.NONE);
        String flightStatus;
        if (levelFlight) {
            flightStatus = "maintaining level flight";
        } else {
            flightStatus = "with " + aoa + "° aoa";
        }
        chart.setTitle(new TextTitle(planeNames + " " + title1 + "m " + flightStatus,
                        new Font("Serif", java.awt.Font.BOLD, 18)
                )
        );

        return chart;
    }

    public List<List<Float>> getDragByPlanes(List<Plane> planesList) {
        List<List<Float>> drags = new ArrayList<>();
        Map<String, List<Float>> dragsMap = new HashMap<>();
        JSONArray speeds = new JSONArray();
        double step = (maxSpeed - minSpeed) / 100.0;
        for (int i = 0; i <= 100; i++) {
            double point = minSpeed + (i * step);
            speeds.put(point);
        }
        JSONArray planesJson = new JSONArray();
        for (Plane plane : planesList) {
            JSONObject planeJson = new JSONObject();
            planeJson.put("fmpath", System.getProperty("user.dir").replace("\\", "/") + "/Data/FM/" + plane.actualName + ".blkx");
            planeJson.put("height", alt);
            planeJson.put("aoa", aoa);
            planeJson.put("isProp", plane.isProp);
            planeJson.put("levelFlight", levelFlight);
            planeJson.put("weight", (Double.parseDouble(plane.emptyWeight) + (Double.parseDouble(plane.fuelWeight) * ((fuels.get(planes.indexOf(plane)) * 0.1)))));
            planeJson.put("name", plane.actualName); // Add the "name" attribute
            planesJson.put(planeJson);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("speeds", speeds);
        jsonObject.put("planes", planesJson);
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String path = "Data/Drag/";

            String jsonInput = jsonObject.toString();
            ProcessBuilder pb;
            if (os.contains("linux")) {
                pb = new ProcessBuilder("wine", path + "drag.exe", jsonInput);
            } else {
                jsonInput = jsonObject.toString().replace("\"", "\\\"");
                pb = new ProcessBuilder(path + "drag.exe", jsonInput);
            }

            Process p = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            p.waitFor();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            StringBuilder errorOutput = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line);
            }
        /*if (errorOutput.length() > 0) {
            System.out.println("Errors: " + errorOutput);
            hook.sendMessage("An error occurred when calculating the drag! mention hadi fr").queue();
        }*/
            int startIndex = output.toString().indexOf("{");
            if (startIndex != -1) {
                String jsonString = output.toString().substring(startIndex);
                JSONObject jsonObjectOut = new JSONObject(jsonString);
                for (String key : jsonObjectOut.keySet()) {
                    JSONObject planeObject = jsonObjectOut.getJSONObject(key);
                    JSONArray dragJson = planeObject.getJSONArray("drag");
                    List<Float> drag = new ArrayList<>();
                    for (Object o : dragJson) {
                        drag.add(((Number) o).floatValue());
                    }
                    dragsMap.put(key, drag); // Store the drag values in the map with the plane's actual name as the key
                }
            } else {
                hook.sendMessage("An error occurred when calculating the drag! tell hadi fr").queue();
                return null;
            }

            // Iterate over the planesList and add the drag values to drags in the same order
            for (Plane plane : planesList) {
                if (dragsMap.containsKey(plane.actualName)) {
                    drags.add(dragsMap.get(plane.actualName));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return drags;
    }

    List<Double> getThrust(Plane p) {
        List<Double> thrust = new ArrayList<>();
        for (int i = 0; i < p.speedList.size(); i++) {
            thrust.add(p.getThrust(alt, p.speedList.get(i), p.thrusts));
        }
        if (p.hasTwoDiffEngineTypes && p.dualEngine && p.thrusts2 != null && !p.actualName.contains("yak_141")) {

            for (int i = 0; i < p.speedList.size(); i++) {
                thrust.set(i, thrust.get(i) + p.getThrust(alt, p.speedList.get(i), p.thrusts2));
            }
        }
        return thrust;
    }

    List<Double> getTtw(Plane p, List<Double> thrust1, Float fuel) {
        List<Double> ttw = new ArrayList<>();
        for (int i = 0; i < p.speedList.size(); i++) {
            ttw.add(thrust1.get(i) / (Double.parseDouble(p.emptyWeight) + (Double.parseDouble(p.fuelWeight) * ((fuel * 0.1)))));
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

    private XYDataset createRipSpeedDataset() {
        var dataset = new XYSeriesCollection();
        for (int i = 0; i < planes.size(); i++) {
            Plane p = planes.get(i);
            var ripSpeedIas = new XYSeries("rip speed " + p.actualName);
            double rip = Double.parseDouble(p.ripSpeedKph);
            double tasSpeed = Math.min(calculateTasFromMach(Double.parseDouble(p.ripSpeedMach), alt), getTASSpeed((int) rip));
            tasSpeed = Math.min(tasSpeed, p.speedList.stream().max(Double::compare).get());
            double thrust = p.getThrust(alt, tasSpeed, p.thrusts);
            ripSpeedIas.add(tasSpeed, thrust - buffer);
            ripSpeedIas.add(tasSpeed, thrust + buffer);
            dataset.addSeries(ripSpeedIas);
        }
        return dataset;
    }

    public XYDataset createTTWDataSet() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (int i = 0; i < planes.size(); i++) {
            Plane p = planes.get(i);
            XYSeries series = new XYSeries("Thrust to weight " + p.actualName);
            int numDataPoints = Math.min(p.speedList.size(), ttws.get(i).size());
            for (int j = 0; j < numDataPoints; j++) {
                series.add(p.speedList.get(j), ttws.get(i).get(j));
            }
            dataset.addSeries(series);
        }
        return dataset;
    }

    private XYDataset createDragDataset() {
        var dataset = new XYSeriesCollection();
        double step = (maxSpeed - minSpeed) / 100.0;
        List<List<Float>> dragsList = getDragByPlanes(planes);
        for (int i = 0; i < planes.size(); i++) {
            Plane p = planes.get(i);
            String seriesKey = "Drag " + p.actualName;
            if (dataset.getSeriesIndex(seriesKey) == -1) {
                var series = new XYSeries(seriesKey);
                List<Float> drags = dragsList.get(i);
                allDrag.addAll(drags);
                for (int j = 0; j <= 100; j++) {
                    double speed = minSpeed + (j * step);
                    series.add(speed, drags.get(j));
                }
                dataset.addSeries(series);
            }
        }
        return dataset;
    }


    private XYDataset createThrustAndDragDataset() {
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
        double step = (maxSpeed - minSpeed) / 100.0;
        List<List<Float>> dragsList = getDragByPlanes(planes);
        for (int i = 0; i < planes.size(); i++) {
            Plane p = planes.get(i);
            String seriesKey = "Drag " + p.actualName;
            if (dataset.getSeriesIndex(seriesKey) == -1) {
                var series = new XYSeries(seriesKey);
                List<Float> drags = dragsList.get(i);
                tempDrag.add(new ArrayList<>(drags));
                allDrag.addAll(drags);
                for (int j = 0; j <= 100; j++) {
                    double speed = minSpeed + (j * step);
                    series.add(speed, drags.get(j));
                }
                dataset.addSeries(series);
            }
        }

        return dataset;
    }

    private XYSeriesCollection createAccelerationDataset() {
        var dataset = new XYSeriesCollection();
        double step = (maxSpeed - minSpeed) / 100.0;
        for (int i = 0; i < planes.size(); i++) {
            Plane p = planes.get(i);
            double mass = Float.parseFloat(p.emptyWeight) + (Float.parseFloat(p.fuelWeight) * ((double) fuels.get(i) / 10));
            //System.out.println((p.getThrust(alt,speed,p.thrusts) - drags.get(j))* 9.807);
            String seriesKey = "Acceleration " + p.actualName;
            if (dataset.getSeriesIndex(seriesKey) == -1) {
                var series = new XYSeries(seriesKey);
                List<Float> drags = new ArrayList<>(tempDrag.get(i));
                // drag.addAll(drags);
                for (int j = 0; j <= 100; j++) {
                    double speed = minSpeed + (j * step);
                    double accel1 = ((p.getThrust(alt, speed, p.thrusts) - drags.get(j)) * 9.807) / mass;

                    accel.add(accel1);
                    series.add(speed, accel1);
                }
                dataset.addSeries(series);
            }
        }

        return dataset;
    }

    static final float stdRo0 = 1.225f;
    static final float _ro0 = 1.225f;
    static final float _T0 = 288.16f;
    static final float _hMax = 18300.0f;
    static final float[] Temperature = {1.f, -2.27712e-05f, 2.18069e-10f, -5.71104e-14f, 3.97306e-18f};
    static final float[] Density = {1.f, -9.59387e-05f, 3.53118e-09f, -5.83556e-14f, 2.28719e-19f};

    double getTASSpeed(int IAS) {
        return IAS * calc_tas_coeff((float) alt);
    }

    static float poly(float[] tab, float v) {
        return (((tab[4] * v + tab[3]) * v + tab[2]) * v + tab[1]) * v + tab[0];
    }

    static float density(float h) {
        return _ro0 * poly(Density, Math.min(h, _hMax)) * (_hMax / Math.max(_hMax, h));
    }

    static float calc_tas_coeff(float alt) {
        return (float) Math.sqrt(stdRo0 / density(alt));
    }


    static float temperature(float h) {
        return _T0 * poly(Temperature, Math.min(h, _hMax));
    }

    static float sonicSpeed(float h) {
        return 20.1f * (float) Math.sqrt(temperature(h));
    }

    public static double calculateMachFromTas(double tas_kph, double altitude_m) {
        return ((tas_kph * 1000) / 3600) / sonicSpeed((float) altitude_m);
    }

    public static double calculateTasFromMach(double mach, double altitude_m) {
        return mach * sonicSpeed((float) altitude_m) * 3600 / 1000;
    }

}