/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edmondskarp.Controller;

import edmondskarp.Model.BFSVisit;
import edmondskarp.Model.DFSVisit;
import edmondskarp.Model.Edge;
import edmondskarp.Gui.Arrow;
import edmondskarp.Gui.Circle;
import edmondskarp.Gui.Config;
import edmondskarp.Gui.EdmondsKarpGui;
import edmondskarp.Model.Graph;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class EdmondsKarpController {

    private final Graph graph;
    private final EdmondsKarpGui gui;
    private Timer tmr1;
    private int name;
    private int bfVisit;
    private final String[] history;
    private int indexHistory;
    private int older;
    private int newest;
    private boolean isSaved;

    public EdmondsKarpController(EdmondsKarpGui gui) {
        this.gui = gui;
        graph = Graph.getGraph();
        setTimer();
        bfVisit = 0;
        name = 0;
        history = new String[60];
        isSaved = false;
        indexHistory = 0;
        newest = 0;
        older = 0;
        saveState();
        graph.addObserver(gui);
    }

    public void saveState(String state) {
        history[indexHistory % history.length] = state;
        indexHistory++;
        if (newest == indexHistory - 1) {
            newest++;
        } else if (newest > indexHistory - 1) {
            newest = indexHistory;
        }
        older = indexHistory < history.length ? 0 : (indexHistory % history.length);
        isSaved = false;
    }

    public void saveState() {
        try {
            saveState(getState());
        } catch (JSONException ex) {
            Logger.getLogger(EdmondsKarpController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void restoreState(boolean back) {
        try {
            if (back) {
                if (indexHistory > older + 1) {
                    openState(history[(indexHistory - 2) % history.length]);
                    indexHistory--;
                }
            } else if (indexHistory < newest) {
                openState(history[indexHistory % history.length]);
                indexHistory++;
            }
        } catch (JSONException ex) {
            Logger.getLogger(EdmondsKarpController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean checkAddInverseArrow(String from, String to) {
        if (!Config.getConfig().isRandomCapacity()) {
            return graph.checkInverseArrowConnection(from, to, Config.getConfig().getFixedCapacity());
        }

        return graph.checkInverseArrowConnection(from, to, (int) ((Math.random() * 100) + 1));
    }

    public boolean addEdge(Arrow arrow) {
        Edge e;

        if (!Config.getConfig().isRandomCapacity()) {
            e = graph.connect(arrow.getFrom().getName(), arrow.getTo().getName(), Config.getConfig().getFixedCapacity(), 0);
        } else {
            e = graph.connect(arrow.getFrom().getName(), arrow.getTo().getName(), (int) ((Math.random() * 100) + 1), 0);
        }

        if (e != null) {
            arrow.setEdge(e);
            return true;
        } else {
            gui.displayMessage("invalid link\n");
            return false;
        }
    }

    public void addNode(Circle circle) {
        circle.addNode(graph.addNode("" + name++));
        saveState();
    }

    public void removeNode(Circle circle) {
        graph.removeNode(graph.getNode(circle.getName()));
    }

    public void setSink(Circle circle) {
        if (!graph.setSink(graph.getNode(circle.getName()))) {
            gui.displayMessage("The well cannot have outgoing arches\n");
        } else {
            gui.update();
            saveState();
        }
    }

    public void setSource(Circle node) {
        if (!graph.setSource(graph.getNode(node.getName()))) {
            gui.displayMessage("The source cannot have incoming arches");
        } else {
            gui.update();
            saveState();
        }
    }

    public void removeEdge(Arrow edge) {
        graph.disconnect(edge.getEdge());
    }

    public void play() {
        if (gui.isPlaySelected()) {
            tmr1.start();
        } else {
            tmr1.stop();
        }
    }

    public void setTimer() {
        int delay = 2000; //milliseconds
        ActionListener taskPerformer1 = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (oneStepForward()) {
                    tmr1.stop();
                    gui.setUpPlayButton();
                }

            }
        };

        tmr1 = new Timer(delay, taskPerformer1);
        tmr1.setRepeats(true);
    }

    public void stop() {
        bfVisit = 0;
        graph.edmondsKarpClear();
        graph.BFVisitClear();
        tmr1.stop();
        gui.update();
    }

    public void run() {
        if (graph.getSource() != null && graph.getSink() != null) {
            graph.edmondsKarpClear();
            if (!graph.EdmondsKarp()) {
                gui.displayMessage("Error");
            }
        } else {
            gui.displayMessage("Error");
        }
        gui.update();
    }

    public void onStepBack() {
        if (bfVisit == 0) {
            return;
        } else if (bfVisit == 1) {
            stop();
            gui.resetLabel();
            return;
        }
        gui.resetLabel();
        graph.edmondsKarpClear();
        graph.BFVisitClear();
        int temp = bfVisit - 1;

        for (int i = 0; i < temp; i++) {
            oneStepForward();
        }
        bfVisit = temp;
        gui.update();
    }

    public boolean oneStepForward() {
        if (graph.getSource() != null && graph.getSink() != null) {
            if (graph.getSink().getParent() == null) {
                if (graph.BFSVisit()) {
                    if (graph.getSink().getParent() == null) {
                        gui.displayMessage("Finished: The maximum flow has been reached");
                        return true;
                    }
                    graph.selectPath();
                } else {
                    gui.displayMessage("BFS failed\n");
                    return true;
                }
            } else if (graph.EdmondsKarpOneStep()) {
                graph.getSink().setParent(null);
            } else {
                gui.displayMessage("Error: inconsistent graph state.\n" +
                        "Press stop to restart the simulation");
                return true;
            }
        } else {
            gui.displayMessage("Error");
            return true;
        }

        bfVisit++;
        gui.update();

        return false;
    }

    public void newGraph() {
        gui.getCircles().clear();
        name = 0;
        bfVisit = 0;
        indexHistory = 0;
        newest = 0;
        older = 0;
        graph.clearGraph();
        gui.update();
    }

    public void setVisit(int visit) {
        if (visit == 0) {
            graph.setVisit(new BFSVisit());
        } else {
            graph.setVisit(new DFSVisit());
        }
    }

    public void open(File f) throws JSONException {
        if (f == null) {
            return;
        }

        String s = "";

        try {
            FileReader fileReader = new FileReader(f);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                s += line;
            }
        } catch (IOException e) {

        }

        newGraph();
        saveState();
        openState(s);
    }

    public void openState(String s) throws JSONException {
        if (s == null || s.equals("")) {
            graph.clearGraph();
            gui.getCircles().clear();
            name = 0;
            gui.update();
            return;
        }

        Map<String, Circle> circles = new HashMap<>();
        graph.clearGraph();

        int maxIndex = 0;
        JSONObject jNodeEdge = new JSONObject(s); // Parse the JSON to a JSONObject
        JSONArray jNodes = jNodeEdge.getJSONArray("Node");
        JSONArray jEdges = jNodeEdge.getJSONArray("Edge");

        for (int i = 0; i < jNodes.length(); i++) { // Loop over each each row of node
            Circle circle = new Circle();
            Point point = new Point();

            JSONObject jNode = jNodes.getJSONObject(i);

            point.setLocation(jNode.getInt("PosX"), jNode.getInt("PosY"));
            circle.setFirstPoint(point);
            circle.addNode(graph.addNode("" + jNode.getString("ID")));

            circles.put(jNode.getString("ID"), circle);
            if (jNode.getInt("ID") > maxIndex) {
                maxIndex = jNode.getInt("ID");
            }
        }

        name = ++maxIndex;

        for (int i = 0; i < jEdges.length(); i++) { // Loop over each each row of edge
            JSONObject jEdge = jEdges.getJSONObject(i);
            Edge e = graph.connect(jEdge.getString("From"), jEdge.getString("To"), jEdge.getInt("Capacity"), 0);
            if (e == null) {
                graph.checkInverseArrowConnection(jEdge.getString("From"), jEdge.getString("To"), jEdge.getInt("Capacity"));
            } else {
                Arrow arrow = new Arrow(circles.get(jEdge.getString("From")), circles.get(jEdge.getString("To")));
                arrow.setEdge(e);
                circles.get(jEdge.getString("From")).addArrowFrom(arrow);
                circles.get(jEdge.getString("To")).addArrowTo(arrow);
            }

        }

        if (!jNodeEdge.getString("Source").equals("")) {
            graph.setSource(graph.getNode(jNodeEdge.getString("Source")));
        }
        if (!jNodeEdge.getString("Sink").equals("")) {
            graph.setSink(graph.getNode(jNodeEdge.getString("Sink")));
        }

        gui.setCircles(new ArrayList(circles.values()));
        gui.update();
        System.gc();
    }

    public void save(String path) throws JSONException {
        try {
            FileWriter writer = new FileWriter(path);
            BufferedWriter bWriter = new BufferedWriter(writer);
            bWriter.write(getState());
            bWriter.close();
            writer.close();
            isSaved = true;
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
    }

    public String getState() throws JSONException {

        if (graph.getNodes().isEmpty()) {
            return "";
        }

        JSONArray nodeJArray = new JSONArray();
        JSONArray edgeJArray = new JSONArray();

        ArrayList<Circle> arrayCircle = gui.getCircles();
        ArrayList<Arrow> arrayArrow;

        for (Circle circle : arrayCircle) {
            arrayArrow = circle.getArrowFrom();
            JSONObject jNode = new JSONObject();

            jNode.put("ID", circle.getNode().getName());
            jNode.put("PosX", circle.getCenter().getX());
            jNode.put("PosY", circle.getCenter().getY());

            for (Arrow arrow : arrayArrow) {

                JSONObject jEdge = new JSONObject();
                jEdge.put("From", arrow.getEdge().getNodeA().getName());
                jEdge.put("To", arrow.getEdge().getNodeB().getName());
                jEdge.put("Capacity", arrow.getEdge().getCapacity());
                edgeJArray.put(jEdge);

                if (!arrow.getEdge().getInverse().isResidual()) {
                    JSONObject jEdge2 = new JSONObject();
                    jEdge2.put("From", arrow.getEdge().getInverse().getNodeA().getName());
                    jEdge2.put("To", arrow.getEdge().getInverse().getNodeB().getName());
                    jEdge2.put("Capacity", arrow.getEdge().getInverse().getCapacity());
                    edgeJArray.put(jEdge2);
                }
            }
            nodeJArray.put(jNode);
        }

        JSONObject jNodeEdge = new JSONObject();
        jNodeEdge.put("Node", nodeJArray);
        jNodeEdge.put("Edge", edgeJArray);

        if (graph.getSource() != null) {
            jNodeEdge.put("Source", graph.getSource().getName());
        } else {
            jNodeEdge.put("Source", "");
        }
        if (graph.getSink() != null) {
            jNodeEdge.put("Sink", graph.getSink().getName());
        } else {
            jNodeEdge.put("Sink", "");
        }

        return jNodeEdge.toString();
    }

    public void setCapacity(Arrow arrow, int capacity, int edge) {
        if (edge == 0) {
            arrow.getEdge().setCapacity(capacity);
        } else if (edge == 1) {
            arrow.getEdge().getInverse().setCapacity(capacity);
        }

        saveState();

    }

    public void setFlow(Arrow arrow, int flow, int edge) {
        arrow.getEdge().setFlow(flow);
        if (edge == 0) {
            arrow.getEdge().setFlow(flow);
        } else if (edge == 1) {
            arrow.getEdge().getInverse().setFlow(flow);
        }
        saveState();

    }

    public void loadExample() {

        String example = "{\"Source\":\"0\",\"Node\":[{\"PosY\":261,\"PosX\":643,\"ID\":\"3\"},{\"PosY\":453,\"PosX\":549,\"ID\":\"2\"},"
                + "{\"PosY\":454,\"PosX\":181,\"ID\":\"1\"},{\"PosY\":260,\"PosX\":112,\"ID\":\"0\"},{\"PosY\":59,\"PosX\":548,\"ID\":\"7\"},"
                + "{\"PosY\":58,\"PosX\":163,\"ID\":\"6\"},{\"PosY\":320,\"PosX\":477,\"ID\":\"5\"},{\"PosY\":318,\"PosX\":279,\"ID\":\"4\"},"
                + "{\"PosY\":181,\"PosX\":476,\"ID\":\"9\"},{\"PosY\":181,\"PosX\":278,\"ID\":\"8\"}],\"Sink\":\"3\",\"Edge\":[{\"To\":\"3\",\"Capacity\":4,\"From\":\"2\"},"
                + "{\"To\":\"2\",\"Capacity\":4,\"From\":\"1\"},{\"To\":\"5\",\"Capacity\":5,\"From\":\"1\"},{\"To\":\"1\",\"Capacity\":4,\"From\":\"0\"},"
                + "{\"To\":\"4\",\"Capacity\":8,\"From\":\"0\"},{\"To\":\"6\",\"Capacity\":4,\"From\":\"0\"},{\"To\":\"8\",\"Capacity\":8,\"From\":\"0\"},"
                + "{\"To\":\"3\",\"Capacity\":4,\"From\":\"7\"},{\"To\":\"7\",\"Capacity\":4,\"From\":\"6\"},{\"To\":\"9\",\"Capacity\":5,\"From\":\"6\"},"
                + "{\"To\":\"3\",\"Capacity\":5,\"From\":\"5\"},{\"To\":\"2\",\"Capacity\":5,\"From\":\"4\"},{\"To\":\"9\",\"Capacity\":4,\"From\":\"4\"},"
                + "{\"To\":\"3\",\"Capacity\":5,\"From\":\"9\"},{\"To\":\"7\",\"Capacity\":5,\"From\":\"8\"},{\"To\":\"5\",\"Capacity\":4,\"From\":\"8\"}]}";
        try {
            openState(example);
            saveState();
            isSaved = true;
        } catch (JSONException ex) {
            Logger.getLogger(EdmondsKarpController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void exit() {
        int response = 0;
        if (indexHistory > 1 && !isSaved) {
            response = gui.checkSave();
        }

        if (response == -1) {
            return;
        }

        System.exit(0);
    }

    public void searchDefaultPreference() {
        File newFile = new File("config.properties");
        InputStream os;
        try {
            Properties properties = new Properties();
            os = new FileInputStream(newFile);
            properties.load(os);
            Config.getConfig().setDefaultArrow(new Color(Integer.parseInt(properties.getProperty("defaultArrow"))));
            Config.getConfig().setUsedArrow(new Color(Integer.parseInt(properties.getProperty("usedArrow"))));
            Config.getConfig().setFilledArrow(new Color(Integer.parseInt(properties.getProperty("filledArrow"))));
            Config.getConfig().setSelectedArrow(new Color(Integer.parseInt(properties.getProperty("selectedArrow"))));
            Config.getConfig().setDimText(Integer.parseInt(properties.getProperty("dimText")));
            Config.getConfig().setDimCircle(Integer.parseInt(properties.getProperty("dimCircle")));
            Config.getConfig().setPosText(Integer.parseInt(properties.getProperty("posText")));
            Config.getConfig().setFixedCapacity(Integer.parseInt(properties.getProperty("fixedCapacity")));
            Config.getConfig().setStrokeCircle(Float.parseFloat(properties.getProperty("strokeCircle")));
            Config.getConfig().setStrokeArrow(Float.parseFloat(properties.getProperty("strokeArrow")));
            Config.getConfig().setRandomCapacity(Boolean.valueOf(properties.getProperty("randomCapacity")));
            gui.updatePrefMenu();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EdmondsKarpController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EdmondsKarpController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void saveConfig() {
        File newFile = new File("config.properties");
        OutputStream os;
        try {
            Properties properties = new Properties();
            os = new FileOutputStream(newFile);

            properties.setProperty("defaultArrow", Config.getConfig().getDefaultArrow().getRGB() + "");
            properties.setProperty("selectedArrow", Config.getConfig().getSelectedArrow().getRGB() + "");
            properties.setProperty("usedArrow", Config.getConfig().getUsedArrow().getRGB() + "");
            properties.setProperty("filledArrow", Config.getConfig().getFilledArrow().getRGB() + "");
            properties.setProperty("dimText", Config.getConfig().getDimText() + "");
            properties.setProperty("dimCircle", Config.getConfig().getDimCircle() + "");
            properties.setProperty("posText", Config.getConfig().getPosText() + "");
            properties.setProperty("fixedCapacity", Config.getConfig().getFixedCapacity() + "");
            properties.setProperty("strokeCircle", Config.getConfig().getStrokeCircle() + "");
            properties.setProperty("strokeArrow", Config.getConfig().getStrokeArrow() + "");
            properties.setProperty("randomCapacity", Config.getConfig().isRandomCapacity() + "");
            properties.store(os, "Properties File");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EdmondsKarpController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EdmondsKarpController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
