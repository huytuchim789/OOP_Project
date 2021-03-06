/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edmondskarp.Model;

import java.util.ArrayList;
import java.util.Observable;
public class Graph extends Observable {

    private final ArrayList<Node> nodes;
    private Node source;
    private Node sink;
    private Visit visit;
    public static Graph graph = new Graph();

    private Graph() {
        nodes = new ArrayList<>();
        source = null;
        sink = null;
        visit = new BFSVisit();
    }

    public static Graph getGraph() {
        return graph;
    }

    public void clearGraph() {
        nodes.clear();
        source = null;
        sink = null;
    }

    public Node getSource() {
        return source;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public int getSize() {
        return nodes.size();
    }

    public boolean setSource(Node source) {
        for (Edge edge : source.getEdges()) {
            if (edge.isResidual() && edge.getNodeA() == source) {
                return false;
            }
        }

        if (this.source != null) {
            this.source.setSource(false);
        }

        this.source = source;
        this.source.setSource(true);

        return true;
    }

    public Node getSink() {
        return sink;
    }

    public boolean setSink(Node sink) {
        for (Edge edge : sink.getEdges()) {
            if (!edge.isResidual() && edge.getInverse().isResidual() && edge.getNodeA() == sink) {
                return false;
            }
        }

        if (this.sink != null) {
            this.sink.setSink(false);
        }

        this.sink = sink;
        this.sink.setSink(true);

        return true;
    }

    public Node getNode(String name) {
        for (Node node : nodes) {
            if (node.getName().equals(name)) {
                return node;
            }
        }
        return null;
    }

    public Node addNode(String name) {
        Node node = new Node(name);
        nodes.add(node);

        return node;
    }

    public boolean removeNode(Node node) {
        if (nodes.isEmpty()) {
            return false;
        }

        for (int i = 0; i < node.getEdges().size(); i++) {
            disconnect(node.getEdges().get(i));
        }

        if (node == source) {
            source = null;
        } else if (node == sink) {
            sink = null;
        }

        nodes.remove(node);
        return true;
    }

    public boolean checkInverseArrowConnection(String a, String b, int capacity) {
        if (getNode(b) == null) {
            return false;
        }
        Edge inverse = getNode(b).getEdgeBNotResidual(getNode(a));
        if (inverse != null && getNode(a).getEdgeBNotResidual(getNode(b)) == null) {
            inverse.getInverse().setIsResidual(false);
            inverse.getInverse().setCapacity(capacity);
            inverse.getInverse().setFlow(0);
            return true;
        }
        return false;
    }

    public Edge connect(Node a, Node adjacent, int capacity, int flow) {
        if (nodes.size() == 1 || capacity < flow) {
            System.out.println("connection failed: there is only one node or stream value not allowed");
            return null;
        } else if (adjacent == source) {
            System.out.println("connection failed: the source cannot have incoming arcs");
            return null;
        } else if (a == sink) {
            System.out.println("connection failed: the well cannot have outgoing arches");
            return null;
        } else if (a.getEdgeBNotResidual(adjacent) != null) {
            return null;
        } else if (adjacent.getEdgeBNotResidual(a) != null) {
            return null;
        }

        Edge edge = new Edge(a, adjacent);
        edge.setCapacity(capacity);
        edge.setIsResidual(false);
        edge.setFlow(flow);
        a.addEdge(edge);

        Edge edgeRes = new Edge(adjacent, a);
        edgeRes.setCapacity(0);
        edgeRes.setFlow(0);
        edgeRes.setIsResidual(true);
        adjacent.addEdge(edgeRes);

        edge.setInverse(edgeRes);
        edgeRes.setInverse(edge);

        return edge;
    }

    public Edge connect(String a, String b, int capacity, int flow) {
        return connect(getNode(a), getNode(b), capacity, flow);
    }

    public void disconnect(Edge edge) {
        if (nodes.size() == 1) {
            System.out.println("Disconnect failed: there is only one node\n");
            return;
        }

        Node a = edge.getNodeA();
        Node b = edge.getNodeB();

        if (!a.removeEdge(b)) {
            System.out.println("Deletion error\n");
        }
        if (!b.removeEdge(edge.getInverse()))
            System.out.println("Deletion error\n");
        }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public void BFVisitClear() {
        for (Node node : nodes) {
            node.setParent(null);
            node.setIsDiscovered(false);
        }
    }

    public boolean BFSVisit() {
        if (source == null) {
            return false;
        }

        BFVisitClear();
        boolean pathFound = visit.visitGraph(source);
        if (pathFound) {
            this.setChanged();
            this.notifyObservers(0);
        }

        return pathFound;
    }

    public boolean EdmondsKarp() {
        if (source == null || sink == null) {
            System.out.println("source or well have not been assigned");
            return false;
        }

        edmondsKarpClear();
        BFSVisit();

        while (sink.getParent() != null) {

            Node tmpNode = sink.getParent();
            int min = tmpNode.getEdgeB(sink).getResidual();

            while (tmpNode.getParent() != null) {
                if (tmpNode.getParent().getEdgeB(tmpNode).getResidual() < min) {
                    min = tmpNode.getParent().getEdgeB(tmpNode).getResidual();
                }

                tmpNode = tmpNode.getParent();
            }

            tmpNode = sink;

            while (tmpNode.getParent() != null) {
                Edge tmpEdge = tmpNode.getParent().getEdgeB(tmpNode);
                tmpEdge.setFlow(tmpEdge.getFlow() + min);
                tmpNode.getEdgeB(tmpNode.getParent()).setFlow(tmpNode.getEdgeB(tmpNode.getParent()).getFlow() - min);
                tmpNode = tmpNode.getParent();
            }
            BFSVisit();
        }

        this.setChanged();
        this.notifyObservers(1);
        return true;
    }

    public int getMaxFlow() {
        if (source == null) {
            return 0;
        }
        int max = 0;
        for (Edge edge : source.getEdges()) {
            if (!edge.isResidual()) {
                max += edge.getFlow();
            }
        }

        return max;
    }

    public int getMinFlow() {
        if (sink == null || sink.getParent() == null) {
            return 0;
        }

        int min;
        Node tmpNode = sink.getParent();
        if (tmpNode.getEdgeB(sink) != null) {
            min = tmpNode.getEdgeB(sink).getResidual();
        } else {
            return 0;
        }

        while (tmpNode.getParent() != null) {
            if (tmpNode.getParent().getEdgeB(tmpNode) != null
                    && tmpNode.getParent().getEdgeB(tmpNode).getResidual() < min) {
                min = tmpNode.getParent().getEdgeB(tmpNode).getResidual();

            }
            tmpNode = tmpNode.getParent();
        }

        return min;
    }

    public void edmondsKarpClear() {
        for (Node node : nodes) {
            for (Edge edge : node.getEdges()) {
                if (!edge.isResidual()) {
                    edge.setFlow(0);
                } else {
                    edge.setFlow(edge.getCapacity());
                }
                edge.setIsDiscovered(false);
            }
        }
    }

    public void selectPath() {
        Node tmpNode = sink;

        while (tmpNode.getParent() != null) {
            tmpNode.getParent().getEdgeB(tmpNode).setIsDiscovered(true);
            tmpNode = tmpNode.getParent();
        }
    }

    public boolean isSelected() {
        return sink.getParent().getEdgeB(sink).isDiscovered();
    }

    public boolean isSafe() {
        return sink != null && sink.getParent() != null && sink.getParent().getEdgeB(sink) != null;
    }

    public boolean EdmondsKarpOneStep() {
        if (source == null || sink == null) {
            System.out.println("source or well have not been assigned");
            return false;
        }

        if (sink.getParent() != null) {

            Node tmpNode = sink.getParent();
            int min = 0;
            if (tmpNode.getEdgeB(sink) != null) {
                min = tmpNode.getEdgeB(sink).getResidual();
            } else {
                return false;
            }

            while (tmpNode.getParent() != null) {
                if (tmpNode.getParent().getEdgeB(tmpNode) != null
                        && tmpNode.getParent().getEdgeB(tmpNode).getResidual() < min) {

                    min = tmpNode.getParent().getEdgeB(tmpNode).getResidual();

                }
                tmpNode = tmpNode.getParent();
            }

            tmpNode = sink;

            while (tmpNode.getParent() != null) {
                Edge tmpEdge = tmpNode.getParent().getEdgeB(tmpNode);
                if (tmpEdge != null) {
                    tmpEdge.setFlow(tmpEdge.getFlow() + min);
                    tmpEdge.setIsDiscovered(false);
                    tmpNode.getEdgeB(tmpNode.getParent()).setFlow(tmpNode.getEdgeB(tmpNode.getParent()).getFlow() - min);
                    tmpNode = tmpNode.getParent();
                } else {
                    return false;
                }
            }

            this.setChanged();
            this.notifyObservers(1);
            return true;
        }
        return false;
    }
}
