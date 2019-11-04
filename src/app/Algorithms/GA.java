package app.Algorithms;

import app.TSP.GenRoute;
import app.TSP.Population;
import app.Utility;
import app.TSP.Vert;
import java.text.DecimalFormat;
import java.util.*;

//genetic algorithm class; holds main calculation method, as well as crossovers and mutations
public class GA
{
    private static int generations;
    private static ArrayList<GenRoute> ancestry;
    private static int popSize;
    private static CrossoverType crossoverType = CrossoverType.ORDER;
    private static MutationType mutationType = MutationType.SWAP;
    private static double mutationChance = 0.002;
    private static double terminationCounter;
    private static double terminationGenerations = 100;
    private static double percentChangeTolerance = 0.0075;
    private static int elitism = 2;
    private static int tourneySelectionNumber = 3;
    private static int numTimesChildIsBetter;
    private static int numCrossovers;
    private static double avgDist = 0.0;
    private static double minDist = Double.POSITIVE_INFINITY;
    private static double maxDist = 0.0;
    private static double avgNumGenerations = 0.0;
    private static double avgSuperiorSelectionRate = 0.0;
    private static int iterations = 0;
    private static boolean useWOC = false;

    public static int getGenerations() {
        return generations;
    }

    public static ArrayList<GenRoute> getAncestry() {
        return ancestry;
    }

    private static void resetStats()
    {
        minDist = Double.POSITIVE_INFINITY;
        avgDist = 0.0;
        maxDist = 0.0;
        avgNumGenerations = 0.0;
        avgSuperiorSelectionRate = 0.0;
        iterations = 0;
    }

    //if crossover type is changed, reset variables related to calculating averages
    public static void setCrossoverType(CrossoverType crossoverType)
    {
        GA.crossoverType = crossoverType;
        resetStats();
    }

    public static CrossoverType getCrossoverType()
    {
        return crossoverType;
    }

    //if muation type is changed, reset variables related to calculating averages
    public static void setMutationType(MutationType mutationType)
    {
        GA.mutationType = mutationType;
        resetStats();
    }

    public static MutationType getMutationType()
    {
        return mutationType;
    }

    public static boolean isUseWOC()
    {
        return useWOC;
    }

    public static void setUseWOC(boolean useWOC)
    {
        GA.useWOC = useWOC;
        resetStats();
    }

    //determine whether to terminate based on preset variables
    private static boolean terminate(double lastShortestPath, double currShortestPath)
    {
        //calculate percent change between current path and previous generation
        double percentChange = Utility.percentChange(lastShortestPath, currShortestPath);
        //if below tolerance, increment counter
        if(percentChange <= percentChangeTolerance)
            terminationCounter++;
        //otherwise reset the counter
        else
            terminationCounter = 0.0;

        //if below tolerance for a preset number of generations, terminate
        if(terminationCounter >= terminationGenerations)
            return true;
        //hard-coded max generations limit
        if(generations > 800)
            return true;

        return false;
    }

    //determine if mutation should occur based on preset chance
    private static boolean calcMutateChance()
    {
        Random rand = new Random();
        return rand.nextDouble() <= mutationChance;
    }

    //just swap two vertices in the route
    private static void mutateSwap(Population population)
    {
        //try to mutate each GenTSP based on mutationChance
        for(GenRoute genRoute: population.getRoutes())
        {
            if(calcMutateChance())
            {
                int size = genRoute.size();
                Random rand = new Random();

                int index1, index2;
                Vert vert1, vert2;
                //randomly select vertices by index
                index1 = index2 = rand.nextInt(size - 1);
                //continue drawing if indices are the same to ensure different indices
                while(index1 == index2)
                {
                    index2 = rand.nextInt(size - 1);
                }

                //swap vertices
                vert1 = genRoute.getVert(index1);
                vert2 = genRoute.getVert(index2);
                genRoute.replaceVert(index1, vert2);
                genRoute.replaceVert(index2, vert1);
            }
        }

    }

    //select a random subsequence of a route and shuffle the vertices
    private static void mutateSubsequence(Population population)
    {
        //try to mutate each GenTSP based on mutationChance
        for(GenRoute genRoute: population.getRoutes())
        {
            if(calcMutateChance())
            {
                Random rand = new Random();
                //select a random length for a subsequence to be mutated
                //maximum 1/3 size of route, minimum 3
                int seqLength = 0;
                 while(seqLength < 3)
                     seqLength = rand.nextInt(genRoute.size() / 3);
                //set up array for shuffling vertices
                ArrayList<Vert> rearrangedVerts = new ArrayList<>();
                int startIndex, currIndex;
                //select random index for start of subsequence
                startIndex = currIndex = rand.nextInt(genRoute.size());

                //add each element of subsequence to ArrayList
                for(int y = 0; y <= seqLength; y++)
                {
                    rearrangedVerts.add(genRoute.getVert(currIndex));
                    //loop index value if necessary
                    if(currIndex != genRoute.size() - 1)
                        currIndex++;
                    else
                        currIndex = 0;
                }
                //shuffle the subsequence
                Collections.shuffle(rearrangedVerts);

                //replace original subsequence elements with shuffled elements
                currIndex = startIndex;
                for(Vert v: rearrangedVerts)
                {
                    genRoute.replaceVert(currIndex, v);
                    if(currIndex != genRoute.size() - 1)
                        currIndex++;
                    else
                        currIndex = 0;
                }
                //update distance value for new path
                genRoute.update();
            }
        }
    }

    //inherit a subsequence from parent1, then fill the rest with parent2 while maintaining relative order or vertices
    private static ArrayList<GenRoute> crossoverOrder(GenRoute parent1, GenRoute parent2)
    {
        GenRoute child1 = new GenRoute();
        GenRoute child2 = new GenRoute();
        Random rand = new Random();
        //select random sequence length; minimum of 1
        int seqLength = rand.nextInt(parent1.size()) + 1;
        //select random start index
        int index = rand.nextInt(parent1.size());
        //add subsequence from parent 1 to child
        for(int i = 0; i < seqLength; i++)
        {
            child1.addVert(parent1.getVert(index));
            child2.addVert(parent2.getVert(index));
            if(index != parent1.size() - 1)
                index++;
            else
                index = 0;
        }
        //add remaining vertices from other parent if not already in child, starting at next index after last vertex added
        for(int i = 0; i < parent1.size(); i++)
        {
            if(!child1.containsVert(parent2.getVert(index)))
            {
                child1.addVert(parent2.getVert(index));
            }
            if(!child2.containsVert(parent1.getVert(index)))
            {
                child2.addVert(parent1.getVert(index));
            }
            if(index != parent1.size() - 1)
                index++;
            else
                index = 0;
        }
        ArrayList<GenRoute> children = new ArrayList<>();
        children.add(child1);
        children.add(child2);
        return children;
    }

    //randomly choose whether each vertex will be inherited from the first parent and maintain same index position if so
    private static ArrayList<GenRoute> crossoverUniformOrder(GenRoute parent1, GenRoute parent2)
    {
        //use simple array to maintain indices
        Vert[] child1PathArr = new Vert[parent1.size()];
        Vert[] child2PathArr = new Vert[parent1.size()];
        GenRoute child1 = new GenRoute();
        GenRoute child2 = new GenRoute();
        Random rand = new Random();
        //ArrayList to keep track of vertices already inherited
        ArrayList<Vert> child1Verts = new ArrayList<>();
        ArrayList<Vert> child2Verts = new ArrayList<>();
        for(int i = 0; i < parent1.size(); i++)
        {
            //choose 0 or 1 randomly and inherit if 1
            if(rand.nextInt(2) == 1)
            {
                child1PathArr[i] = parent1.getVert(i);
                child1Verts.add(parent1.getVert(i));
                child2PathArr[i] = parent2.getVert(i);
                child2Verts.add(parent2.getVert(i));
            }
        }

        //ArrayLists to save verts that need to be added into a different position
        ArrayList<Vert> c1VertsNeeded = new ArrayList<>();
        ArrayList<Vert> c2VertsNeeded = new ArrayList<>();
        //get uniform vertices from other parent if possible
        for(int i = 0; i < parent1.size(); i++)
        {
            if(child1PathArr[i] == null && !child1Verts.contains(parent2.getVert(i)))
            {
                child1PathArr[i] = parent2.getVert(i);
                child1Verts.add(parent2.getVert(i));
            }
            else if(child1PathArr[i] != null && !child1Verts.contains(parent2.getVert(i)))
            {
                c1VertsNeeded.add(parent2.getVert(i));
            }
            if(child2PathArr[i] == null && !child2Verts.contains(parent1.getVert(i)))
            {
                child2PathArr[i] = parent1.getVert(i);
                child2Verts.add(parent1.getVert(i));
            }
            else if(child2PathArr[i] != null && !child2Verts.contains(parent1.getVert(i)))
            {
                c2VertsNeeded.add((parent1.getVert(i)));
            }
        }

        //fill remaining null vertices wherever they fit
        for(int i = 0; i < parent1.size(); i++)
        {
            if(child1PathArr[i] == null)
            {
                child1PathArr[i] = c1VertsNeeded.remove(0);
            }

            if(child2PathArr[i] == null)
            {
                child2PathArr[i] = c2VertsNeeded.remove(0);
            }
        }

        //add array to the child ArrayList to be returned
        for(int x = 0; x < parent1.size(); x++)
        {
            child1.addVert(child1PathArr[x]);
            child2.addVert(child2PathArr[x]);
        }

        ArrayList<GenRoute> children = new ArrayList<>();
        children.add(child1);
        children.add(child2);
        return children;
    }

    //calculate a solution for a TSP given a population of routes
    public static GenRoute calcGeneticSolution(Population population)
    {
        //reset data for current calculation
        numTimesChildIsBetter = 0;
        numCrossovers = 0;
        popSize = population.size();
        generations = 0;
        terminationCounter = 0;

        //set up ancestry array for progression data and add 0th population
        ancestry = new ArrayList<>();
        ancestry.add(population.getFittest());
        Population prevPopulation = new Population(population);
        Population newPopulation;
        Population popCopy;
        boolean term = false;
        //continue until termination criteria met
        while(!term)
        {
            //if(generations % 10 == 0)
                //System.out.println(generations);

            newPopulation = new Population();
            GenRoute parent1, parent2;
            ArrayList<GenRoute> children = new ArrayList<>();
            popCopy = new Population(prevPopulation);

            if(useWOC)
            {
                children.add(WOC.wocSolution(prevPopulation));
            }

            for(int x = 0; x < elitism; x++)
            {
                //remove fittest from a copy of the population so that the same route isn't selected multiple times
                children.add(popCopy.removeFittest());
            }

            for(GenRoute child: children)
            {
                newPopulation.addRoute(child);
            }

            //continue until the new population is the same size as the previous
            while(newPopulation.size() < population.size())
            {
                numCrossovers++;
                parent1 = prevPopulation.selectByTournament(tourneySelectionNumber);
                parent2 = prevPopulation.selectByTournament(tourneySelectionNumber);

                if(crossoverType == CrossoverType.ORDER)
                    children = crossoverOrder(parent1, parent2);
                else
                    children = crossoverUniformOrder(parent1, parent2);

                //determine if either child was superior to both parents
                if((children.get(0).getFitness() >= parent1.getFitness() && children.get(0).getFitness() >= parent2.getFitness())
                ||(children.get(1).getFitness() >= parent1.getFitness() && children.get(1).getFitness() >= parent2.getFitness()))
                    numTimesChildIsBetter++;

                //add children to new population
                for(GenRoute child: children)
                {
                    if(newPopulation.size() < population.size())
                        newPopulation.addRoute(child);
                }
            }

            if(mutationType == MutationType.SWAP)
                mutateSwap(newPopulation);
            else
                mutateSubsequence(newPopulation);

            GenRoute fittest = newPopulation.getFittest();
            //calculate termination criteria
            term = terminate(prevPopulation.getFittest().getDistance(),
                    fittest.getDistance());

            ancestry.add(fittest);
            generations++;

            prevPopulation = newPopulation;
        }

        //update statistics over a number of repeated calculations
        iterations++;
        double finalDist = ancestry.get(ancestry.size() - 1).getDistance();
        avgNumGenerations = ((avgNumGenerations * (iterations - 1)) + generations) / iterations;

        if(finalDist < minDist)
            minDist = finalDist;
        if(finalDist > maxDist)
            maxDist = finalDist;

        avgDist = ((avgDist * (iterations - 1) + ancestry.get(ancestry.size() - 1).getDistance())
                / iterations);
        avgSuperiorSelectionRate = (avgSuperiorSelectionRate * (iterations - 1)
                + ((double) numTimesChildIsBetter / (double) numCrossovers)) / iterations;

        DecimalFormat df = new DecimalFormat("0.##");
        System.out.println(df.format(finalDist));

        return ancestry.get(ancestry.size() - 1);
    }

    //simple "toString" method of outputting all current statistics
    public static String getStatistics()
    {
        DecimalFormat df = new DecimalFormat("0.##");
        String string = "Population size: " + popSize
                + "\nCrossover Type: " + crossoverType
                + "\nMutation Type: " + mutationType
                + "\nMutation Chance: " + mutationChance*100 + "%"
                + "\nElitism Number: " + elitism
                + "\nTournament - Number Selected: " + tourneySelectionNumber
                + "\nTermination Criteria: Terminate after " + (int) terminationGenerations
                    + " generations with " + percentChangeTolerance*100 + "% change or less."
                + "\nRate of creating superior child during crossover: "
                    + df.format( ((double)numTimesChildIsBetter / (double)numCrossovers)*100) + "%"
                + "\nResults over " + iterations + " iterations:"
                + "\n - Minimum Distance: " + df.format(minDist)
                + "\n - Average Distance: " + df.format(avgDist)
                + "\n - Maximum Distance: " + df.format(maxDist)
                + "\n - Average Number of Generations: " + df.format((int)avgNumGenerations)
                + "\n - Average Superior Child Rate: " + df.format(avgSuperiorSelectionRate*100) + "%";
        return string;
    }
}
