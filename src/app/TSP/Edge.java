package app.TSP;

import app.Utility;

public class Edge implements Comparable<Edge>
{
    private Vert vert1;
    private Vert vert2;
    private Double length;

    public Edge(Vert vert1, Vert vert2)
    {
        this.vert1 = vert1;
        this.vert2 = vert2;
        calcLength();
    }

    public Edge(Edge edge)
    {
        this.vert1 = edge.getVert1();
        this.vert2 = edge.getVert2();
        calcLength();
    }

    public Vert getVert1() {
        return vert1;
    }

    public void setVert1(Vert vert1) {
        this.vert1 = vert1;
        calcLength();
    }

    public Vert getVert2() {
        return vert2;
    }

    public void setVert2(Vert vert2) {
        this.vert2 = vert2;
        calcLength();
    }

    public Double getLength() {
        return length;
    }

    public boolean containsVert(Vert vert)
    {
        return this.vert1.equals(vert) || this.vert2.equals(vert);
    }

    private void calcLength()
    {
        this.length = Utility.calcDist(vert1.getXCoord(), vert1.getYCoord(),
                vert2.getXCoord(), vert2.getYCoord());
    }

    @Override
    public int compareTo(Edge other)
    {
        return this.length.compareTo(other.getLength());
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == this)
            return true;
        if(obj == null || obj.getClass() != this.getClass())
            return false;

        Edge edge = (Edge) obj;
        return (this.vert1.equals(edge.getVert1()) && this.vert2.equals(edge.getVert2())) ||
                (this.vert1.equals(edge.getVert2()) && this.vert2.equals(edge.getVert1()));
    }

    @Override
    public String toString()
    {
        return this.vert1.getId() + "---" + this.vert2.getId();
    }
}
