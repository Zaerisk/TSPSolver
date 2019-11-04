package app.TSP;

public class DirRoute extends Route
{
    private int transitions;
    private boolean[][] adjTable;

    public DirRoute()
    {
        super();
        transitions = 0;
        initAdjTable();
    }

    public DirRoute(Route route)
    {
        super(route);
        transitions = 0;
        initAdjTable();
    }

    public int getTransitions()
    {
        return transitions;
    }

    public void resetTransitions()
    {
        transitions = 0;
    }

    public void incTransitions()
    {
        transitions++;
    }

    public boolean[][] getAdjTable()
    {
        return adjTable;
    }

    private void initAdjTable()
    {
        boolean[][] adj = new boolean[11][11];
        adj[0][1] = true;
        adj[0][2] = true;
        adj[0][3] = true;
        adj[1][2] = true;
        adj[2][3] = true;
        adj[2][4] = true;
        adj[3][4] = true;
        adj[3][5] = true;
        adj[3][6] = true;
        adj[4][6] = true;
        adj[4][7] = true;
        adj[5][7] = true;
        adj[6][8] = true;
        adj[6][9] = true;
        adj[7][8] = true;
        adj[7][9] = true;
        adj[7][10] = true;
        adj[8][10] = true;
        adj[9][10] = true;
        this.adjTable = adj;
    }
}
