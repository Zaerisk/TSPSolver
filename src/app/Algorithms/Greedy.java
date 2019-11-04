package app.Algorithms;

import app.TSP.KVEntry;
import app.TSP.Route;
import app.Utility;
import app.TSP.Vert;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class Greedy
{
    private static ArrayList<ArrayList<Vert>> stateProgression;

    private static ArrayList<Vert> setupBaseRoute(Route route)
    {
        ArrayList<Vert> currState = new ArrayList<>();
        //find center of all points
        double xMax, xMin, yMax, yMin;
        xMax = yMax = 0;
        xMin = yMin = 100;
        for(Vert v: route.getPath())
        {
            if(v.getXCoord() > xMax)
                xMax = v.getXCoord();
            if(v.getXCoord() < xMin )
                xMin = v.getXCoord();
            if(v.getYCoord() > yMax)
                yMax = v.getYCoord();
            if(v.getYCoord() < yMin)
                yMin = v.getYCoord();
        }

        //determine center based on bounds
        double xCenter = xMax - (xMax - xMin)/2;
        double yCenter = yMax - (yMax - yMin)/2;

        //find vertex nearest the center and add to path
        //goal is to start in center and expand outward
        PriorityQueue<KVEntry> pQueue = new PriorityQueue<>();
        for(Vert v: route.getPath())
        {
            pQueue.add(new KVEntry(v.getId(), Utility.calcDist(xCenter, yCenter, v.getXCoord(), v.getYCoord())));
        }
        currState.add(route.getVertById(pQueue.poll().getNum()));
        stateProgression.add(new ArrayList<>(currState));

        //find vertex nearest the first and add to path
        pQueue = new PriorityQueue<>();
        Vert firstVert = new Vert(currState.get(0));
        for(Vert v: route.getPath())
        {
            if(v != firstVert)
                pQueue.add(new KVEntry(v.getId(), Utility.calcDist(firstVert.getXCoord(), firstVert.getYCoord(), v.getXCoord(), v.getYCoord())));
        }
        currState.add(route.getVertById(pQueue.poll().getNum()));
        stateProgression.add(new ArrayList<>(currState));

        return currState;
    }

    public static ArrayList<Vert> expand(ArrayList<Vert> currState, Route route)
    {
        //create ArrayList of all remaining vertices
        ArrayList<Vert> vertsRemaining = route.getPath();
        vertsRemaining.removeAll(currState);


        Double distToCurrEdge, distToNearestEdge, angleCurrEdge, angleNearestEdge;
        int currEdgeV1, currEdgeV2, nearestEdgeV1, nearestEdgeV2;
        nearestEdgeV1 = nearestEdgeV2 = 0;
        Vert vertToAdd = new Vert();

        //continue until all vertices added to route
        while(!vertsRemaining.isEmpty())
        {
            //reset nearest edge distance to infinity for comparison
            distToNearestEdge = Double.POSITIVE_INFINITY;
            //do for each remaining vertex
            for(Vert currVert: vertsRemaining)
            {
                //for each edge in the current route
                for(int i = 0; i < currState.size(); i++)
                {
                    //currEdgeV1 and currEdgeV2 make up vertices of current edge being compared
                    currEdgeV1 = i;
                    //set up for calculating return edge if at end of array
                    if(i != currState.size() - 1)
                        currEdgeV2 = i+1;
                    else
                        currEdgeV2 = 0;

                    //calculate distance to current edge
                    distToCurrEdge = Utility.calcPointToLine(currVert.getXCoord(), currVert.getYCoord(),
                            currState.get(currEdgeV1).getXCoord(), currState.get(currEdgeV1).getYCoord(),
                            currState.get(currEdgeV2).getXCoord(), currState.get(currEdgeV2).getYCoord());

                    //compare distance to current edge with the nearest distance thus far and update accordingly
                    if(distToCurrEdge < distToNearestEdge)
                    {
                        distToNearestEdge = distToCurrEdge;
                        vertToAdd = currVert;
                        nearestEdgeV1 = currEdgeV1;
                        nearestEdgeV2 = currEdgeV2;
                    }
                    //if the distances are equal, that means the nearest edge distance is to the vertex at
                    //..the end of two edges. calculate both angles from the current vertex to the vertices of
                    //..each potential edge and choose the more obtuse angle. this helps to prevent overlapping edges
                    //..that might add additional distance to the route
                    //also, don't calculate if there are only two vertices on path, as they create two identical edges
                    else if(distToCurrEdge.equals(distToNearestEdge) && currState.size() > 2)
                    {
                        angleCurrEdge = Utility.calcAngle(
                                Utility.calcDist(currState.get(currEdgeV1).getXCoord(), currState.get(currEdgeV1).getYCoord()
                                        , currState.get(currEdgeV2).getXCoord(), currState.get(currEdgeV2).getYCoord())
                                , Utility.calcDist(currState.get(currEdgeV1).getXCoord(), currState.get(currEdgeV1).getYCoord()
                                        , currVert.getXCoord(), currVert.getYCoord())
                                , Utility.calcDist(currState.get(currEdgeV2).getXCoord(), currState.get(currEdgeV2).getYCoord()
                                        , currVert.getXCoord(), currVert.getYCoord()) );
                        angleNearestEdge = Utility.calcAngle(
                                Utility.calcDist(currState.get(nearestEdgeV1).getXCoord(), currState.get(nearestEdgeV1).getYCoord()
                                        , currState.get(nearestEdgeV2).getXCoord(), currState.get(nearestEdgeV2).getYCoord())
                                , Utility.calcDist(currState.get(nearestEdgeV1).getXCoord(), currState.get(nearestEdgeV1).getYCoord()
                                        , currVert.getXCoord(), currVert.getYCoord())
                                , Utility.calcDist(currState.get(nearestEdgeV2).getXCoord(), currState.get(nearestEdgeV2).getYCoord()
                                        , currVert.getXCoord(), currVert.getYCoord()) );
                        if(angleCurrEdge > angleNearestEdge)
                        {
                            distToNearestEdge = distToCurrEdge;
                            vertToAdd = currVert;
                            nearestEdgeV1 = currEdgeV1;
                            nearestEdgeV2 = currEdgeV2;
                        }
                    }
                }
            }
            //add the vertex that is closest to an edge on the route
            currState.add(nearestEdgeV2, vertToAdd);
            if(stateProgression != null)
                stateProgression.add(new ArrayList<>(currState));
            vertsRemaining.remove(vertToAdd);
        }

        return currState;
    }

    public static ArrayList<ArrayList<Vert>> calcGreedySolution(Route route)
    {
        //array of vert arrays to record progression of states for animating the solution
        stateProgression = new ArrayList<>();
        ArrayList<Vert> currState;
        ArrayList<Vert> finalState;

        currState = setupBaseRoute(route);

        finalState = expand(currState, route);

        //update shortest path
        route.setPath(finalState);

        //just listing state progression on console for extra data/debugging
        for(int i = 0; i < stateProgression.size(); i++)
        {
            System.out.print("\nState " + i + ":  ");
            for(int j = 0; j < stateProgression.get(i).size(); j++)
            {
                if(j != stateProgression.get(i).size() - 1)
                    System.out.print(stateProgression.get(i).get(j).getId() + " -> ");
                else
                    System.out.print(stateProgression.get(i).get(j).getId() + "\n");
            }
        }
        System.out.println();
        return stateProgression;
    }

}
