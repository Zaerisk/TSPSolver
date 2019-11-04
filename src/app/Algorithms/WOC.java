package app.Algorithms;

import app.TSP.*;
import app.Utility;

import java.util.ArrayList;
import java.util.Collections;

public class WOC
{

    private static double percentSelected = 0.15;
    private static double percentAgreementThreshold = 0.75;
    private static double sizeThreshold = 0.8;

    //select a subset of the population based on a preset percentage
    private static Population selectExperts(Population population)
    {
        population.sortPopByDistAsc();
        Population bestOfCrowd = new Population();
        //get a new population of the top percentage of individuals of the population
        for(int i = 0; i < (int)(population.size() * percentSelected); i++)
        {
            bestOfCrowd.addRoute(population.getRoute(i));
        }
        return bestOfCrowd;
    }

    //retrieve and sort all edges of the experts, sort them and select those with the preset agreement percentage
    private static ArrayList<KVEdgeFreq> getSortedEdges(Population experts)
    {
        ArrayList<KVEdgeFreq> edges = new ArrayList<>();
        KVEdgeFreq KVEdgeFreq;
        int index;
        //add all edges to a list with their number of occurences
        for(GenRoute genRoute: experts.getRoutes())
        {
            for(int i = 0; i < genRoute.size(); i++)
            {
                //if not at last index, add new edge of current vert and next vert
                if(i != genRoute.size() - 1)
                    KVEdgeFreq = new KVEdgeFreq(new Edge(genRoute.getVert(i), genRoute.getVert(i + 1)));
                    //otherwise next vert is index 0
                else
                    KVEdgeFreq = new KVEdgeFreq(new Edge(genRoute.getVert(i), genRoute.getVert(0)));

                //add new edge if not already in list
                if(!edges.contains(KVEdgeFreq))
                {
                    edges.add(KVEdgeFreq);
                }
                //otherwise, just increment the current edge in list
                else
                {
                    index = edges.lastIndexOf(KVEdgeFreq);
                    edges.get(index).incOccurrence();
                }
            }
        }

        //sort edges in descending order; comparator uses occurrence value
        Collections.sort(edges);
        Collections.reverse(edges);

        //remove all edges that have agreement below threshold
        double percentAgreement;
        ArrayList<KVEdgeFreq> finalEdges = new ArrayList<>();
        for(KVEdgeFreq e: edges)
        {
            percentAgreement = e.getFrequency() / (double) experts.size();
            if(percentAgreement >= percentAgreementThreshold)
            {
                finalEdges.add(e);
            }
        }

        //if(GA.getGenerations() % 10 == 0)
            //System.out.println("    e0: " + edges.size());

        //if(GA.getGenerations() % 10 == 0)
            //System.out.println("    e1: " + finalEdges.size());

        return finalEdges;
    }

    //add the edges to a list of subsequences of consecutive edges for combining later
    private static ArrayList<ArrayList<Vert>> addEdgesToSubsequences(ArrayList<KVEdgeFreq> edges)
    {
        ArrayList<Vert> subseq = new ArrayList<>();
        ArrayList<ArrayList<Vert>> subsequences = new ArrayList<>();
        ArrayList<Integer> addedVerts = new ArrayList<>();
        Edge edge = edges.get(0).getEdge();
        subseq.add(edge.getVert1());
        addedVerts.add(edge.getVert1().getId());
        subseq.add(edge.getVert2());
        addedVerts.add(edge.getVert2().getId());
        subsequences.add(subseq);
        edges.remove(0);
        boolean containsVert1, containsVert2, vertAdded;

        while(!edges.isEmpty())
        {
            edge = edges.get(0).getEdge();

            containsVert1 = containsVert2 = false;
            if(addedVerts.contains(edge.getVert1().getId()))
                containsVert1 = true;
            if(addedVerts.contains(edge.getVert2().getId()))
                containsVert2 = true;

            //don't try to add vert if both have already been added
            if(!(containsVert1 && containsVert2))
            {
                vertAdded = false;
                //iterate through subsequences to try and find a point where the next edge can be appended
                for(int i = 0; i < subsequences.size(); i++)
                {
                    subseq = subsequences.get(i);
                    //check if edge can be appended to front and add opposite vertex
                    if(edge.getVert1().equals(subseq.get(0)))
                    {
                        subseq.add(0, edge.getVert2());
                        addedVerts.add(edge.getVert2().getId());
                        vertAdded = true;
                        break;
                    }
                    else if(edge.getVert2().equals(subseq.get(0)))
                    {
                        subseq.add(0, edge.getVert1());
                        addedVerts.add(edge.getVert1().getId());
                        vertAdded = true;
                        break;
                    }
                    //check if edge can be appended to end
                    else if(edge.getVert1().equals(subseq.get(subseq.size() - 1)))
                    {
                        subseq.add(edge.getVert2());
                        addedVerts.add(edge.getVert2().getId());
                        vertAdded = true;
                        break;
                    }
                    else if(edge.getVert2().equals(subseq.get(subseq.size() - 1)))
                    {
                        subseq.add(edge.getVert1());
                        addedVerts.add(edge.getVert1().getId());
                        vertAdded = true;
                        break;
                    }
                }
                //if edge vert was not added, and both verts are new
                //create new subsequence with new verts
                if(!vertAdded && !(containsVert1 || containsVert2))
                {
                    subseq = new ArrayList<>();
                    subseq.add(edge.getVert1());
                    addedVerts.add(edge.getVert1().getId());
                    subseq.add(edge.getVert2());
                    addedVerts.add(edge.getVert2().getId());
                    subsequences.add(subseq);
                }
            }
            edges.remove(0);
        }
        return subsequences;
    }

    //construct a route by connecting edges that share the same vertex
    private static Route constructRoute(ArrayList<KVEdgeFreq> edges, GenRoute route)
    {
        Route constructedRoute = new Route();
        if(!edges.isEmpty())
        {
            ArrayList<ArrayList<Vert>> subsequences = addEdgesToSubsequences(edges);

            ArrayList<Vert> existingVerts = new ArrayList<>();
            for(int i = 0; i < subsequences.size(); i++)
            {
                existingVerts.addAll(subsequences.get(i));
            }

            //add first subsequence to constructedRoute and remove from subsequences list
            constructedRoute.addAll(subsequences.get(0));
            subsequences.remove(0);

            double dist, nearestDist;
            Vert currVert, nearestVert;
            int index = -1;
            while((!subsequences.isEmpty() //|| !missingVerts.isEmpty())
                    && constructedRoute.size() < route.size()))
            {
                //currVert is last vert of constructedRoute
                currVert = constructedRoute.getVert(constructedRoute.size() - 1);
                nearestDist = Double.POSITIVE_INFINITY;
                nearestVert = new Vert();
                if(!subsequences.isEmpty())
                {
                    //calculate distances to all endpoints
                    for(int i = 0; i < subsequences.size(); i++)
                    {
                        //calculate distance to first vert and last vert of each subsequence
                        for(int j = 0; j < subsequences.get(i).size(); j += subsequences.get(i).size())
                        {
                            dist = Utility.calcDist(currVert, subsequences.get(i).get(j));
                            if(dist < nearestDist)
                            {
                                nearestDist = dist;
                                nearestVert = new Vert(subsequences.get(i).get(j));
                                index = i;
                            }
                        }
                    }
                }

                if(!subsequences.isEmpty())
                {
                    //just add the subsequence if the vert is at the front
                    if(nearestVert.equals(subsequences.get(index).get(0)))
                    {
                        constructedRoute.addAll(subsequences.get(index));
                    }
                    //if vert is at end, reverse subsequence first
                    else
                    {
                        ArrayList<Vert> reversed = new ArrayList<>(subsequences.get(index));
                        Collections.reverse(reversed);
                        constructedRoute.addAll(reversed);
                    }
                    subsequences.remove(index);
                }
            }
        }
        return constructedRoute;
    }

    public static GenRoute wocSolution(Population population)
    {
        GenRoute finalRoute = new GenRoute();

        Population experts = new Population(selectExperts(population));

        ArrayList<KVEdgeFreq> edges = getSortedEdges(experts);

        Route constructedRoute = constructRoute(edges, population.getRoute(0));

        //just return a randomized route if route is not complete to a certain degree, based on preset
        //..this prevents the solution being wholly determined by a greedy algorithm
        if((double) constructedRoute.size() / (double) population.getRoute(0).size() < sizeThreshold)
        {
            ArrayList<Vert> shuffled = new ArrayList<>(population.getRoute(0).getPath());
            Collections.shuffle(shuffled);
            finalRoute.addAll(shuffled);
        }
        else if(constructedRoute.size() < population.getRoute(0).size())
        {
            //if the route is nearly complete, finish it with the greedy expansion algorithm
            finalRoute.addAll(Greedy.expand(constructedRoute.getPath(), population.getRoute(0)));
        }
        else
            finalRoute.addAll(constructedRoute.getPath());

        return finalRoute;
    }



}
