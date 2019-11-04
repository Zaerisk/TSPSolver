package app.TSP;

//represents a vertex on a 2D coordinate grid
public class Vert
{
    //index number for vertex
    int id;
    //x coordinate value
    double xCoord;
    //y coordinate value
    double yCoord;
    
    public Vert()
    {
        id = 0;
        xCoord = 0;
        yCoord = 0;
    }

    public Vert(int n, double x, double y)
    {
        id = n;
        xCoord = x;
        yCoord = y;
    }

    public Vert(Vert vert)
    {
        id = vert.getId();
        xCoord = vert.getXCoord();
        yCoord = vert.getYCoord();
    }

    public int getId() {return id;}
    public void setId(int n) {
        id = n;}
    
    public double getXCoord() {return xCoord;}
    public void setXCoord(double x) {xCoord = x;}
    
    public double getYCoord() {return yCoord;}
    public void setYCoord(double y) {yCoord = y;}

    @Override
    public boolean equals(Object obj)
    {
        if(obj == this)
            return true;
        if(obj == null || obj.getClass() != this.getClass())
            return false;

        Vert other = (Vert) obj;
        return this.id == other.getId() && this.xCoord == other.getXCoord() && this.yCoord == other.getYCoord();
    }

    @Override
    public String toString()
    {
        return this.id + ":    (" + this.xCoord + ", " + this.yCoord + ")";
    }
}
