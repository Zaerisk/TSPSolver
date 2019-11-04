package app.TSP;

import app.ReadFiles;
import app.Utility;

import java.util.*;

//represents a travelling salesman problem .tsp file
public class Route implements Comparable<Route>
{
    protected ArrayList<Vert> path;
    //distance of shortest path found
    private Double distance;

    public Route()
    {
        this.path = new ArrayList<>();
        this.distance = 0.0;
    }

    public Route(String filePath)
    {
        //initialize original permutation from file
        this.path = ReadFiles.getTSPVerts(filePath);
        update();
    }

    public Route(Route route)
    {
        this.setPath(route.getPath());
    }

    public double getDistance() {return distance;}

    public void setDistance(Double distance)
    {
        this.distance = distance;
    }

    public ArrayList<Vert> getPath()
    {
        ArrayList<Vert> path = new ArrayList<>(this.path);
        return path;
    }

    public void setPath(ArrayList<Vert> path)
    {
        this.path = new ArrayList<>(path);
        update();
    }

    public int size()
    {
        return this.path.size();
    }

    public void addAll(ArrayList<Vert> arr)
    {
        for(Vert v: arr)
        {
            this.path.add(new Vert(v));
        }
        update();
    }

    public void addVert(Vert vert)
    {
        this.path.add(new Vert(vert));
        update();
    }

    public void insertVert(int index, Vert vert)
    {
        this.path.add(index, new Vert(vert));
        update();
    }

    public void replaceVert(int index, Vert vert)
    {
        path.set(index, new Vert(vert));
        update();
    }

    public Vert getVert(int index)
    {
        return new Vert(this.path.get(index));
    }

    public Vert getVertById(int id)
    {
        return new Vert(this.path.get(getVertIndex(id)));
    }

    public void removeVert(int index)
    {
        this.path.remove(index);
        update();
    }

    public int getVertIndex(int num)
    {
        for(Vert v: path)
        {
            if(v.getId() == num)
                return path.indexOf(v);
        }
        return -1;
    }

    public boolean containsVert(Vert vert)
    {
        for(Vert v: path)
        {
            if(v.equals(vert))
                return true;
        }
        return false;
    }
    public void update()
    {
        distance = Utility.calcTotalDistLoop(path);
    }

    @Override
    public int compareTo(Route other) {return this.distance.compareTo(other.getDistance());}

    @Override
    public String toString()
    {
        String string = "";
        for(int i = 0; i < this.path.size(); i++)
        {
            if(i != this.path.size() - 1)
                string += this.path.get(i).getId() + " -> ";
            else
                string += this.path.get(i).getId();
        }
        string += "    Distance: " + this.distance;
        return string;
    }

}
