package app.TSP;

//custom key-value entry for PriorityQueue that has proper sorting via custom compareTo function
public class KVEntry implements Comparable<KVEntry>
{
    //key: vertex number
    private int num;
    //value: distance to vertex from start
    private Double distance;

    public KVEntry(int nodeNumber, double distanceToNode)
    {
        num = nodeNumber;
        distance = distanceToNode;
    }

    public KVEntry(KVEntry kvEntry)
    {
        num = kvEntry.getNum();
        distance = kvEntry.getDistance();
    }

    public int getNum() {return num;}
    public void setNum(int n) {num = n;}
    public Double getDistance() {return distance;}
    public void setDistance(Double d) {distance = d;}

    @Override
    //compareTo compares values of two compared objects
    public int compareTo(KVEntry other)
    {
        return this.distance.compareTo(other.getDistance());
    }
}
