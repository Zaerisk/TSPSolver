package app.Algorithms;

import app.TSP.Edge;

public class KVEdgeFreq implements Comparable<KVEdgeFreq>
{
    private Edge edge;
    private Integer frequency;

    public KVEdgeFreq(Edge edge)
    {
        this.edge = edge;
        this.frequency = 1;
    }

    public Edge getEdge()
    {
        return edge;
    }

    public void setEdge(Edge edge)
    {
        this.edge = edge;
    }

    public Integer getFrequency()
    {
        return frequency;
    }

    public void setFrequency(Integer frequency)
    {
        this.frequency = frequency;
    }

    public void incOccurrence()
    {
        this.frequency++;
    }

    @Override
    //compareTo compares values of two compared objects
    public int compareTo(KVEdgeFreq other)
    {
        return this.frequency.compareTo(other.getFrequency());
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == this)
            return true;
        if(obj == null || obj.getClass() != this.getClass())
            return false;

        KVEdgeFreq other = (KVEdgeFreq) obj;
        return this.edge.equals(other.getEdge());
    }

    @Override
    public String toString()
    {
        return this.edge.toString() + "    " + this.frequency;
    }
}
