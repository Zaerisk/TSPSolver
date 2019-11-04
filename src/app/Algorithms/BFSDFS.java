package app.Algorithms;

import app.*;
import app.TSP.DirRoute;
import app.TSP.KVEntry;
import app.TSP.TraversalData;
import app.TSP.Vert;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class BFSDFS
{
    //breadth first search algorithm modified to perform like dijkstra's algorithm to
    //..find shortest path on a weighted graph
    public static void bfs(int start, int dest, DirRoute dirRoute)
    {
        dirRoute.resetTransitions();
        TraversalData td = new TraversalData(start, dest);
        PriorityQueue<KVEntry> pQueue = new PriorityQueue<>();
        for(Vert v: dirRoute.getPath())
        {
            //set up branches/connection for each vertex based on adjacency table
            td.getMapBranches().put(v.getId(), Utility.getConnectedVerts(dirRoute.getAdjTable(), v.getId()));
            td.getMapVisited().put(v.getId(), false);
            //set all vertices, except start, with a distance of infinity for comparison
            if(v.getId() != start)
            {
                //need to use Map object to keep distance in addition to queue, as
                //..PriorityQueue does not allow for removing without exact object duplicate
                td.getMapDistance().put(v.getId(), Double.POSITIVE_INFINITY);
                pQueue.add(new KVEntry(v.getId(), Double.POSITIVE_INFINITY));
            }
            else
            {
                td.getMapDistance().put(v.getId(), 0.0);
                pQueue.add(new KVEntry(v.getId(), 0.0));
            }
        }
        td.getMapPredecessor().put(start, start);

        int curr;
        double dist, newDist;
        Vert vert1, vert2;

        while(!pQueue.isEmpty())
        {
            //pop pQueue
            curr = pQueue.poll().getNum();

            td.getMapVisited().put(curr, true);

            //new transition each time a node is visited, except first
            if(curr != start)
                dirRoute.incTransitions();

            if(curr == dest)
                break;

            for(Integer v: td.getMapBranches().get(curr))
            {
                //get vertices and calculate distance to predecessor
                vert1 = dirRoute.getVertById(curr);
                vert2 = dirRoute.getVertById(v);
                dist = Utility.calcDist(vert1, vert2);
                //for each adjacent vertex, update the weight/distance if new distance is shorter
                if (td.getMapDistance().get(v) > td.getMapDistance().get(curr) + dist)
                {
                    //easiest way to update priority queue is remove then add with new value
                    pQueue.remove(new KVEntry(v, td.getMapDistance().get(v)));
                    newDist = td.getMapDistance().get(curr) + dist;
                    //update distance value
                    td.getMapDistance().put(v, newDist);
                    //update predecessor
                    td.getMapPredecessor().put(v, curr);
                    //re-add key with new value
                    pQueue.add(new KVEntry(v, td.getMapDistance().get(v)));
                }

            }
        }
        //backtrace path from destination and update path
        dirRoute.setPath(Utility.backTracePath(td, dirRoute));
    }

    //recursive function from original DFS algorithm, used to visit nodes and travel deeper
    private static void dfsVisit(int vertId, TraversalData td, DirRoute dirRoute)
    {
        //new transition each time a node is visited, except first
        if(vertId != td.getStart())
            dirRoute.incTransitions();

        //set node as having been visited
        td.getMapVisited().put(vertId, true);

        //stop if on destination node
        if(vertId != td.getDest())
        {
            int i, v;
            //if there are multiple branches on the path here
            if(td.getMapBranches().get(vertId).size() > 1)
            {
                boolean firstSplit = true;
                //..and this is the first time reaching this point with multiple
                //..branches, i.e. all adjacent nodes are unvisited,
                for(Integer x: td.getMapBranches().get(vertId))
                {
                    if(td.getMapVisited().get(x))
                        firstSplit = false;
                }
                if(firstSplit == true)
                {
                    //..create a new recursion branch where other nodes besides the current first
                    //..node in line are visited first instead
                    //..by creating a copy of the current TraversalData to pass on to dfsVisit()
                    TraversalData tdCopy;
                    for(i = 1; i < td.getMapBranches().get(vertId).size(); i++)
                    {
                        tdCopy = new TraversalData(td);
                        v = td.getMapBranches().get(vertId).get(i);
                        tdCopy.getMapPredecessor().put(v, vertId);
                        dfsVisit(v, tdCopy, dirRoute);
                    }
                }
            }

            //default action for the current recursion path
            for(i = 0; i < td.getMapBranches().get(vertId).size(); i++)
            {
                //get first adjacent node
                v = td.getMapBranches().get(vertId).get(i);
                //if not visited, set the current node as its predecessor and visit it
                if(!td.getMapVisited().get(v))
                {
                    td.getMapPredecessor().put(v, vertId);
                    dfsVisit(v, td, dirRoute);
                }
            }
        }
        //stop and calculate total distance if destination reached
        else
        {
            ArrayList<Vert> path;
            path = Utility.backTracePath(td, dirRoute);
            double dist = Utility.calcTotalDist(path);
            if(dist < dirRoute.getDistance() || dirRoute.getDistance() == 0.0)
            {
                dirRoute.setPath(path);
            }
        }
    }

    //modified depth first search algorithm
    public static void dfs(int start, int dest, DirRoute dirRoute)
    {
        //reset data for new calculations
        dirRoute.resetTransitions();
        //create TraversalData to keep track of node data
        TraversalData td = new TraversalData(start, dest);
        //for each vertex, set as not visited and calculate adjacent nodes
        for(Vert v: dirRoute.getPath())
        {
            td.getMapVisited().put(v.getId(), false);
            td.getMapBranches().put(v.getId(), Utility.getConnectedVerts(dirRoute.getAdjTable(), v.getId()));
        }
        //perform first dfsVisit on start vertex
        dfsVisit(start, td, dirRoute);
        //then continue with the rest, if not visited
        for(Vert v: dirRoute.getPath())
        {
            if(!td.getMapVisited().get(v.getId()))
                dfsVisit(v.getId(), td, dirRoute);
        }
    }


}
