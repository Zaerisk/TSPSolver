package app.TSP;

import java.util.ArrayList;

//extension of Route class that also calculates individual fitness values
public class GenRoute extends Route
{
    private double fitness;

    public GenRoute()
    {
        super();
        this.fitness = 0.0;
    }

    public GenRoute(Route route)
    {
        super(route);
        calcFitness();
    }
    public GenRoute(GenRoute genRoute)
    {
        super(genRoute);
        this.fitness = genRoute.getFitness();
    }

    public double getFitness() {
        return fitness;
    }

    private void calcFitness()
    {
        this.fitness =  1.0 / this.getDistance();
    }

    @Override
    public void setPath(ArrayList<Vert> path)
    {
        this.path = new ArrayList<>(path);
        update();
    }

    @Override
    public void addVert(Vert vert)
    {
        this.path.add(new Vert(vert));
        update();
    }

    @Override
    public void insertVert(int index, Vert vert)
    {
        this.path.add(index, new Vert(vert));
        update();
    }

    @Override
    public void removeVert(int index)
    {
        this.path.remove(index);
        update();
    }

    @Override
    public void update()
    {
        super.update();
        calcFitness();
    }

    @Override
    public int compareTo(Route other)
    {
        GenRoute otherGen = new GenRoute(other);
        other.update();
        Double d1 = this.fitness;
        Double d2 = otherGen.getFitness();
        return d1.compareTo(d2);
    }
}
