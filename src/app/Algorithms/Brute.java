package app.Algorithms;

import app.TSP.Route;
import app.Utility;
import app.TSP.Vert;

import java.util.ArrayList;

public class Brute
{
    //recursive brute force method of finding all permutations between
    //..elements in array
    //the first part (prefix) of the array is saved and recursive operations are
    //..performed on the remaining elements (suffix)
    //prefix and suffix are combined at end of each recursion path for final permutation
    private static void bruteRecurse(ArrayList<Vert> pref, ArrayList<Vert> suf, Route route)
    {
        ArrayList<Vert> newPref;
        ArrayList<Vert> newSuf;
        double dist;

        //go through index based on size of suffix array
        for(int i = 0; i < suf.size(); i++)
        {
            //permutation calculations are performed for each instance of
            //..the array in which a different element is the first
            //swapping index 0 with 0 does nothing, but results in the original
            //..permutation as a solution
            suf = Utility.swapFirst(suf, i);

            //with only 2 elements, solution is just current permutation and
            //..inverse permutation
            if(suf.size() <= 2)
            {
                //end of recursion: combine solutions and calculate
                ArrayList<Vert> finishedArr = new ArrayList<>();
                finishedArr.addAll(pref);
                finishedArr.addAll(suf);
                dist = Utility.calcTotalDistLoop(finishedArr);
                if(dist < route.getDistance() || route.getDistance() == 0.0)
                {
                    //update path with new shortest path
                    route.setPath(pref);
                    route.addAll(suf);
                }
            }
            else
            {
                //create copies of prefix and suffix arrays to pass on to
                //..further recursive calls
                newPref = new ArrayList<>(pref);
                newSuf = new ArrayList<>(suf);

                //"lock" first element in place for this recursive path by
                //..separating it and adding to prefix
                newPref.add(suf.get(0));
                newSuf.remove(0);

                //pass on new copies of prefix and suffix to further recursion
                bruteRecurse(newPref, newSuf, route);
            }
        }
    }

    //overloaded version with no parameters
    //sets up prefix and suffix and begins recursive operations
    //..by passing prefix and suffix to recursive calls of brute method
    public static void calcBruteForce(Route route)
    {
        ArrayList<Vert> prefix = new ArrayList<>();
        //1st node is start and finish, so add to prefix
        prefix.add(route.getVert(0));
        ArrayList<Vert> suffix = new ArrayList<>();
        //copy original permutation, but remove 1st node, as it is in prefix
        suffix.addAll(route.getPath());
        suffix.remove(0);

        bruteRecurse(prefix, suffix, route);
    }

}
