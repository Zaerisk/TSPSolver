package app;

import app.TSP.Route;
import app.TSP.TraversalData;
import app.TSP.Vert;

import java.util.ArrayList;

public class Utility
{

    //simple swap of 0th element with index specified
    public static ArrayList<Vert> swapFirst(ArrayList<Vert> arr, int index)
    {
        Vert temp = arr.get(0);
        arr.set(0, arr.get(index));
        arr.set(index, temp);
        return arr;
    }

    //calculate distance between two vertices using distance formula
    public static double calcDist(double x1, double y1, double x2, double y2)
    {
        return Math.sqrt(  Math.pow((x2 - x1), 2)
                +
                Math.pow((y2 - y1), 2)
        );
    }

    public static double calcDist(Vert v1, Vert v2)
    {
        return Math.sqrt(  Math.pow((v2.getXCoord() - v1.getXCoord()), 2)
                +
                Math.pow((v2.getYCoord() - v1.getYCoord()), 2)
        );
    }

    //calculate total distance across the given ArrayList of vertices
    public static double calcTotalDist(ArrayList<Vert> vertArr)
    {
        double x1, y1, x2, y2, total = 0;

        //go through array and select each subsequent pair of points
        for(int i = 0; i < vertArr.size() - 1; i++)
        {
            x1 = vertArr.get(i).getXCoord();
            y1 = vertArr.get(i).getYCoord();
            x2 = vertArr.get(i+1).getXCoord();
            y2 = vertArr.get(i+1).getYCoord();

            //calculate distance between two points and add to total
            total += calcDist(x1, y1, x2, y2);
        }
        return total;
    }

    //additional actions needed for calculating looping path
    public static double calcTotalDistLoop(ArrayList<Vert> vertArr)
    {


        double total = calcTotalDist(vertArr);

        //brute total is a loop, so add distance of last to first element
        double x1, y1, x2, y2;
        x1 = vertArr.get(vertArr.size() - 1).getXCoord();
        y1 = vertArr.get(vertArr.size() - 1).getYCoord();
        x2 = vertArr.get(0).getXCoord();
        y2 = vertArr.get(0).getYCoord();
        total += calcDist(x1, y1, x2, y2);

        return total;
    }

    //return ArrayList of vertex numbers that are adjacent to the selected vertex for a given adjacency table
    public static ArrayList<Integer> getConnectedVerts(boolean[][] adj, int v)
    {
        ArrayList<Integer> connections = new ArrayList<>();
        for(int i = 0; i < 11; i++)
        {
            if(adj[v - 1][i])
                connections.add(i+1);
        }
        return connections;
    }

    //backtrace from destination to start, recording full path
    public static ArrayList<Vert> backTracePath(TraversalData td, Route route)
    {
        //start at destination
        int n = td.getDest();
        ArrayList<Vert> path = new ArrayList<>();
        //backtrace using predecessor data
        while(n != td.getStart())
        {
            path.add(0, route.getVertById(n));
            n = td.getMapPredecessor().get(n);
        }
        path.add(0, route.getVertById(td.getStart()));

        return path;
    }

    public static double calcAngle(double oppSide, double adjSide1, double adjSide2)
    {
        //law of cosines: c^2 = a^2 + b^2 -2ab*cos(theta)
        //returns angle opposite of side c, where a and b are adjacent sides
        return Math.acos((adjSide1*adjSide1 + adjSide2*adjSide2 - oppSide*oppSide)/(2*adjSide1*adjSide2));
    }

    public static double calcPointToLine(double x0, double y0, double x1, double y1, double x2, double y2)
    {
        //vector parameter for minimized distance to line
        double t = -1 *( ((x1-x0)*(x2-x1) + (y1-y0)*(y2-y1)) / (Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2)) );
        if(t >=0 && t <= 1)
            //perpendicular line from point to edge intersects edge, so use its distance
            return Math.abs((x2-x1)*(y1-y0)-(x1-x0)*(y2-y1)) / Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
            //otherwise closest distance is to one of the edge's two vertices
        else
        {
            double distToV1 = Utility.calcDist(x0, y0, x1, y1);
            double distToV2 = Utility.calcDist(x0, y0, x2, y2);

            if(distToV1 <= distToV2)
                return distToV1;
            else
                return distToV2;
        }
    }

    public static double percentChange(double oldValue, double newValue)
    {
        return ( Math.abs(newValue - oldValue) / ( (newValue + oldValue)/2 ) );
    }
}
