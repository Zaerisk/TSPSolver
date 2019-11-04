package app.TSP;

import java.util.*;

//records important information used in traversing a graph via the
//..BFS and DFS algorithms
public class TraversalData
{
    //number of the start vertex
    private int start;
    //number of the destination vertex
    private int dest;
    //Map with vertex number as key and boolean indicating visited status of the vertex
    private Map<Integer, Boolean> mapVisited;
    //Map with vertex number as key and distance to vertex from start as value
    private Map<Integer, Double> mapDistance;
    //Map with vertex number as key and the number of the predecessor vertex as value
    private Map<Integer, Integer> mapPredecessor;
    //Map with vertex number as key and list of adjacent vertex numbers as value
    private Map<Integer, ArrayList<Integer>> mapBranches;

    public TraversalData(int s, int d)
    {
        start = s;
        dest = d;
        mapVisited = new HashMap<>();
        mapDistance = new HashMap<>();
        mapPredecessor = new HashMap<>();
        mapBranches = new HashMap<>();
    }

    public TraversalData(TraversalData td)
    {
        start = td.getStart();
        dest = td.getDest();
        mapVisited = new HashMap<>(td.getMapVisited());
        mapDistance = new HashMap<>(td.getMapDistance());
        mapPredecessor = new HashMap<>(td.getMapPredecessor());
        mapBranches = new HashMap<>(td.getMapBranches());
    }


    public int getStart() {return start;}
    public void setStart(int s) {start = s;}
    public int getDest() {return dest;}
    public void setDest(int d) {dest = d;}
    public Map<Integer, Boolean> getMapVisited() {return mapVisited;}
    public void setMapVisited(Map<Integer, Boolean> v) {
        mapVisited = new HashMap<>(v);}
    public Map<Integer, Double> getMapDistance() {return mapDistance;}
    public void setMapDistance(Map<Integer, Double> d) {mapDistance = new HashMap<>(d);}
    public Map<Integer, Integer> getMapPredecessor() {return mapPredecessor;}
    public void setMapPredecessor(Map<Integer, Integer> p) {
        mapPredecessor = new HashMap<>(p);}
    public Map<Integer, ArrayList<Integer>> getMapBranches() {return mapBranches;}
    public void setMapBranches(Map<Integer, ArrayList<Integer>> b) {
        mapBranches = new HashMap<>(b);}
}
