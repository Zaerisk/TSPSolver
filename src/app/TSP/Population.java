package app.TSP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;



public class Population
{
    private ArrayList<GenRoute> routes;

    public Population()
    {
        this.routes = new ArrayList<>();
    }

    public Population(Route route, int popSize)
    {
        GenRoute genRoute = new GenRoute(route);
        initPop(popSize, genRoute);
    }

    public Population(Population population)
    {
        this.routes = new ArrayList<>(population.getRoutes());
    }

    public int size()
    {
        return routes.size();
    }

    public ArrayList<GenRoute> getRoutes()
    {
        ArrayList<GenRoute> newRoutes = new ArrayList<>(routes);
        return newRoutes;
    }

    public void setRoutes(ArrayList<GenRoute> routes)
    {
        this.routes = new ArrayList<>(routes);
    }

    public GenRoute getRoute(int index)
    {
        return new GenRoute(this.routes.get(index));
    }

    public void addRoute(GenRoute genRoute)
    {
        routes.add(new GenRoute(genRoute));
    }

    public void removeRoute(int index)
    {
        routes.remove(index);
    }

    //copy original GenTSP, shuffle, and add to ArrayList for population
    private void initPop(int popSize, GenRoute genRoute)
    {
        routes = new ArrayList<>();
        GenRoute newGenRoute;
        ArrayList<Vert> shuffled;
        for(int x = 0; x < popSize; x++)
        {
            shuffled = genRoute.getPath();
            Collections.shuffle(shuffled);
            newGenRoute = new GenRoute();
            newGenRoute.setPath(shuffled);
            addRoute(newGenRoute);
        }
    }

    public void sortPopByDistAsc()
    {
        Collections.sort(routes);
    }

    public GenRoute getFittest()
    {
        GenRoute fittest = new GenRoute(routes.get(0));
        for(GenRoute genRoute: routes)
        {
            if(genRoute.getFitness() > fittest.getFitness())
                fittest = genRoute;
        }
        return fittest;
    }

    public GenRoute removeFittest()
    {
        GenRoute fittest = new GenRoute(routes.get(0));
        int index = 0;
        for(int i = 0; i < size(); i++)
        {
            if(routes.get(i).getFitness() > fittest.getFitness())
            {
                fittest = routes.get(i);
                index = i;
            }
        }
        routes.remove(index);
        return fittest;
    }

    //randomly select from population, then get the fittest of those selected
    public GenRoute selectByTournament(int numberSelected)
    {
        Random rand = new Random();
        int randIndex;
        //make copy to allow for full removal of routes when picking
        Population popCopy = new Population(this);
        //pool of routes that will compete for selection
        Population tourney = new Population();

        //fill tourney with random routes from population
        for (int i = 0; i < numberSelected; i++)
        {
            randIndex = rand.nextInt(popCopy.size());
            tourney.addRoute(popCopy.getRoute(randIndex));
            popCopy.removeRoute(randIndex);
        }
        //find the fittest route and return it
        return tourney.getFittest();
    }

    public String toString()
    {
        return "size: " + this.size();
    }
}
