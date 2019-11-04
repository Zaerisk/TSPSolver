package app;

import app.Algorithms.BFSDFS;
import app.Algorithms.Brute;
import app.Algorithms.GA;
import app.Algorithms.Greedy;
import app.TSP.*;
import app.Algorithms.MutationType;
import app.Algorithms.CrossoverType;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Optional;

//controller for javafx objects using fxml file
public class Controller
{
    @FXML
    private ScatterChart<Double, Double> chart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private Pane pane;
    @FXML
    private Group lines;
    @FXML
    private Text txtDist;
    @FXML
    private Text txtCalcTime;
    @FXML
    private Text txtIterations;
    @FXML
    private Text txtAvgCalcTime;
    @FXML
    private Label lblTransitions;
    @FXML
    private Text txtTransitions;
    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML
    private Hyperlink hyperlink;
    @FXML
    ToggleGroup tgCrossover;
    @FXML
    ToggleGroup tgMutation;
    @FXML
    RadioMenuItem rmOrder;
    @FXML
    RadioMenuItem rmUniform;
    @FXML
    RadioMenuItem rmSwap;
    @FXML
    RadioMenuItem rmSubseq;
    @FXML
    CheckMenuItem chkWOC;


    private Route route;
    private Route bestRoute;
    private int transitions;
    private XYChart.Series<Double, Double> series;
    private int iterations;
    private double calcTime;
    private double avgCalcTime;
    private SequentialTransition st;

    @FXML
    private void initialize()
    {
        //set options of choicebox
        choiceBox.setItems(FXCollections.observableArrayList("Brute Force", "Breadth First Search",
                "Depth First Search", "Greedy", "Genetic"));
        //set default option
        choiceBox.getSelectionModel().clearAndSelect(4);


        //add listener to reset certain values when choicebox is changed
        choiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) ->
        {
            if(route != null)
            {
                clearLines();
                resetData();
                //only draw adjacency lines for bfs or dfs of size 11
                if((choiceBox.getSelectionModel().isSelected(1) || choiceBox.getSelectionModel().isSelected(2))
                    && route.size() == 11)
                {
                    DirRoute dirRoute = new DirRoute(route);
                    drawAdjacencyLines(dirRoute.getAdjTable());
                }
            }
           resetData();
            if(!choiceBox.getSelectionModel().isSelected(4))
                lblTransitions.setText("Number of Transitions");
            else
                lblTransitions.setText("Number of Generations: ");
        });

        //add listener to erase lines when window is resized, to prevent visuals from being skewed
        ChangeListener<Number> paneSizeListener = (observable, oldValue, newValue) ->
        clearLines();
        pane.widthProperty().addListener(paneSizeListener);
        pane.heightProperty().addListener(paneSizeListener);

        //listeners to change crossover and mutation operators
        tgCrossover.selectedToggleProperty().addListener((observableValue, toggle, t1) ->
        {
            if(tgCrossover.getSelectedToggle().equals(rmOrder))
                GA.setCrossoverType(CrossoverType.ORDER);
            else
                GA.setCrossoverType(CrossoverType.UNIFORM);

            resetData();
        });
        tgMutation.selectedToggleProperty().addListener((observableValue, toggle, t1) ->
        {
            if(tgMutation.getSelectedToggle().equals(rmSwap))
                GA.setMutationType(MutationType.SWAP);
            else
                GA.setMutationType(MutationType.SUBSEQUENCE);

            resetData();
        });
        if(chkWOC.isSelected())
            GA.setUseWOC(true);
        else
            GA.setUseWOC(false);
        chkWOC.selectedProperty().addListener((observableValue, aBoolean, t1) ->
        {
            if(chkWOC.isSelected())
                GA.setUseWOC(true);
            else
                GA.setUseWOC(false);

            resetData();
        });

        bestRoute = new Route();
        bestRoute.setDistance(Double.POSITIVE_INFINITY);
    }

    //reset tsp object, data values, and text
    private void resetData()
    {
        calcTime = 0;
        avgCalcTime = 0;
        iterations = 0;
        transitions = 0;
        txtDist.setText("");
        txtCalcTime.setText("");
        txtIterations.setText("");
        txtAvgCalcTime.setText("");
        txtTransitions.setText("");
        hyperlink.setDisable(true);
        hyperlink.setVisible(false);
        bestRoute = new Route();
        bestRoute.setDistance(Double.POSITIVE_INFINITY);
    }

    //handler for open button
    public void onOpen()
    {
        //open file with the filechooser and assign to optional; may be empty
        Optional<File> opFile = ReadFiles.fileSelection();
        //Optional<File> opFile = Optional.of(new File("D:\\Users\\Jusdan\\Dropbox\\School\\AI\\Project4\\Random100.tsp"));


        //if a file was returned(user selected a file and did not cancel)
        if(opFile.isPresent())
        {
            //create new TSP object from file
            route = new Route(opFile.get().getAbsolutePath());
            Stage stage = (Stage) pane.getScene().getWindow();
            stage.setTitle("Graph Solver - " + opFile.get().getName());

            int choice = choiceBox.getSelectionModel().getSelectedIndex();

            clearLines();
            chart.getData().remove(series);
            series = new XYChart.Series<>();
            resetData();

            double highestX = 0;
            double highestY = 0;
            double lowestX = 100;
            double lowestY = 100;

            //add points to series for a chart
            ArrayList<Vert> verts = route.getPath();
            for(Vert n: verts)
            {
                //also find bounds of current dataset to zoom in chart afterwards
                if(n.getXCoord() > highestX)
                    highestX = n.getXCoord();
                if(n.getYCoord() > highestY)
                    highestY = n.getYCoord();
                if(n.getXCoord() < lowestX)
                    lowestX = n.getXCoord();
                if(n.getYCoord() < lowestY)
                    lowestY = n.getYCoord();

                series.getData().add(new XYChart.Data<>(n.getXCoord(), n.getYCoord()));
            }
            chart.getData().add(series);

            //set bounds of chart axes
            xAxis.setUpperBound(Math.round(highestX) + 3);
            yAxis.setUpperBound(Math.round(highestY) + 3);
            xAxis.setLowerBound(Math.round(lowestX) - 3);
            yAxis.setLowerBound(Math.round(lowestY) - 3);

            //create hard-coded adjacency table and draw adjacency lines if option for a directed graph is chosen
            if((choice == 1 || choice == 2)
                    && route.getPath().size() == 11)
            {
                DirRoute dirRoute = new DirRoute(route);
                drawAdjacencyLines(dirRoute.getAdjTable());
            }
        }
    }

    //handler for calculate button
    public void onBtnCalcClick()
    {
        //if a tsp object is loaded
        if(route != null)
        {
            int choice = choiceBox.getSelectionModel().getSelectedIndex();
            //bfs and dfs only set up for tsp of size 11, don't calculate for them otherwise
            if(!((choice == 1 || choice == 2) && route.getPath().size() != 11))
            {
                ArrayList<ArrayList<Vert>> stateProgression = new ArrayList<>();
                //remove current lines
                clearLines();

                //start time calculations
                long startTime = System.nanoTime();
                //determine algorithm based on choicebox selection
                if(choice == 0)
                    Brute.calcBruteForce(route);
                else if(choice == 1)
                {
                    DirRoute dirRoute = new DirRoute(route);
                    BFSDFS.bfs(1, 11, dirRoute);
                    transitions = dirRoute.getTransitions();
                    route = new Route(dirRoute);
                }
                else if(choice == 2)
                {
                    DirRoute dirRoute = new DirRoute(route);
                    BFSDFS.dfs(1, 11, dirRoute);
                    transitions = dirRoute.getTransitions();
                    route = new Route(dirRoute);
                }
                else if(choice == 3)
                {
                    stateProgression = new ArrayList<>(Greedy.calcGreedySolution(route));
                }
                else
                {
                    Population population = new Population(route, 200);
                    route = GA.calcGeneticSolution(population);
                }

                //measure end calculation time
                long calcTimeNano = (System.nanoTime() - startTime);
                calcTime = (double)calcTimeNano / 1000000;
                iterations++;
                avgCalcTime = ((avgCalcTime * (iterations - 1)) + calcTime) / iterations;

                //formatting for text objects
                DecimalFormat df = new DecimalFormat("0.##");

                //fill text objects with data
                txtDist.setText(df.format(route.getDistance()));
                txtCalcTime.setText(df.format((calcTime)) + "ms");
                txtIterations.setText(Integer.toString(iterations));
                txtAvgCalcTime.setText(df.format(avgCalcTime) + "ms");
                if(choice == 1 || choice == 2)
                    txtTransitions.setText(df.format(transitions));
                else if(choice == 4)
                {
                    lblTransitions.setText("Number of Generations");
                    txtTransitions.setText(Integer.toString(GA.getGenerations()));
                    hyperlink.setDisable(false);
                    hyperlink.setVisible(true);
                }
                else
                    txtTransitions.setText("n/a");

                //draw looping path for brute algorithm
                if(choice == 0)
                    lines.getChildren().addAll(drawPath(route.getPath(), true));
                //redraw adjacency lines and straight path for bfs and dfs
                else if(choice == 1 || choice == 2)
                {
                    //drawAdjacencyLines();
                    lines.getChildren().addAll(drawPath(route.getPath(), false));
                }
                //draw looping path with animation for greedy algorithm
                else if(choice == 3)
                    drawGreedyProgression(stateProgression);
                else
                    drawGeneticHistory(GA.getAncestry());

                if(route.getDistance() < bestRoute.getDistance())
                    bestRoute = new Route(route);
            }
        }
    }

    public void onClickShowBestRoute()
    {
        if(bestRoute.size() > 0)
        {
            clearLines();

            int choice = choiceBox.getSelectionModel().getSelectedIndex();

            DecimalFormat df = new DecimalFormat("0.##");
            txtDist.setText(df.format(bestRoute.getDistance()));

            //draw looping path for brute algorithm
            if(choice == 0)
                lines.getChildren().addAll(drawPath(bestRoute.getPath(), true));
                //redraw adjacency lines and straight path for bfs and dfs
            else if(choice == 1 || choice == 2)
            {
                //drawAdjacencyLines();
                lines.getChildren().addAll(drawPath(bestRoute.getPath(), false));
            }
            //draw looping path with animation for greedy algorithm
            else
                lines.getChildren().addAll(drawPath(bestRoute.getPath(), true));
        }
    }

    //method for drawing a single line on the graph: actually adds lines to pane overlaying the graph
    private Group drawLine(double xPos1, double yPos1, double xPos2, double yPos2, boolean isArrow, Color color)
    {
        Group newLine = new Group();
        //get points' position on each chart axis, then convert to fit chart's local positioning
        double x1 = chart.sceneToLocal(xAxis.localToScene(xAxis.getDisplayPosition(xPos1), 0)).getX();
        double y1 = chart.sceneToLocal(yAxis.localToScene(0, yAxis.getDisplayPosition(yPos1))).getY();
        double x2 = chart.sceneToLocal(xAxis.localToScene(xAxis.getDisplayPosition(xPos2), 0)).getX();
        double y2 = chart.sceneToLocal(yAxis.localToScene(0, yAxis.getDisplayPosition(yPos2))).getY();
        Line line = new Line(x1, y1, x2, y2);
        line.setStroke(color);
        line.setStrokeWidth(2);

        //draw arrowhead for directed graph lines
        //code adapted from https://coderanch.com/t/340443/java/Draw-arrow-head-line
        if(isArrow)
        {
            //use midpoint; overlapping arrows at endpoints are harder to read
            x1 = (x1 + x2) / 2.0;
            y1 = (y1 + y2) / 2.0;
            //length of each line of the arrowhead
            int len = 6;
            //angle of arrowhead lines, relative to base line
            double phi = Math.toRadians(40);
            //find angle of line
            double theta = Math.atan2((y2 - y1), (x2 - x1));
            //trigonometry measurements to determine endpoint coordinates for arrowhead lines
            x2 = x1 - len * Math.cos(theta + phi);
            y2 = y1 - len * Math.sin(theta + phi);
            Line ah1 = new Line(x1, y1, x2, y2);
            x2 = x1 - len * Math.cos(theta - phi);
            y2 = y1 - len * Math.sin(theta - phi);
            Line ah2 = new Line(x1, y1, x2, y2);

            ah1.setStroke(color);
            ah1.setStrokeWidth(2);
            ah2.setStroke(color);
            ah2.setStrokeWidth(2);
            newLine.getChildren().addAll(line, ah1, ah2);
        }
        else
            newLine.getChildren().add(line);

        return newLine;
    }

    //draws lines for directed graph's adjacency lines
    private void drawAdjacencyLines(boolean[][] adjTable)
    {
        //calling layout forces chart to render fully before continuing
        //otherwise coordinate translations might be skewed
        chart.layout();

        clearLines();
        double x1, y1, x2, y2;

        for(int i = 0; i < 11; i++)
        {
            for(int j = 0; j < 11; j++)
            {
                //draw lines based on preset adjacency table
                if(adjTable[i][j])
                {
                    x1 = route.getPath().get(i).getXCoord();
                    y1 = route.getPath().get(i).getYCoord();
                    x2 = route.getPath().get(j).getXCoord();
                    y2 = route.getPath().get(j).getYCoord();
                    lines.getChildren().add(drawLine(x1, y1, x2, y2, true, Color.BLACK));
                }
            }
        }
    }

    //use the shortestPath array to determine coordinates for drawing lines
    private Group drawPath(ArrayList<Vert> path, Boolean loop)
    {
        Group drawnPath = new Group();
        double x1, y1, x2, y2;
        //draw lines between each set of nodes using shortestPath list
        //..to denote the shortest path
        for(int i = 0; i < path.size(); i++)
        {
            x1 = path.get(i).getXCoord();
            y1 = path.get(i).getYCoord();
            //if not at end of ArrayList
            if(i != path.size() - 1)
            {
                x2 = path.get(i+1).getXCoord();
                y2 = path.get(i+1).getYCoord();
            }
            //at end, set up (x2, y2) as first element for drawing loop
            else
            {
                x2 = path.get(0).getXCoord();
                y2 = path.get(0).getYCoord();
            }
            //if loop is being drawn
            if(loop)
                drawnPath.getChildren().addAll(drawLine(x1, y1, x2, y2, false, Color.RED));
            //show arrows for directed graphs; also not a loop, so omit last line
            else if(i != route.getPath().size() - 1)
            {
                drawnPath.getChildren().addAll(drawLine(x1, y1, x2, y2, true, Color.RED));
            }
        }
        return drawnPath;
    }

    //method for clearing all lines from the graph
    private void clearLines()
    {
        pane.getChildren().remove(lines);
        lines = new Group();
        pane.getChildren().add(lines);
    }

    private void drawGreedyProgression(ArrayList<ArrayList<Vert>> stateProgression)
    {
        Group pathState;
        FadeTransition ft;
        st = new SequentialTransition();
        for(int i = 0; i < stateProgression.size(); i++)
        {
            //create new group
            pathState = new Group();
            //draw lines and assign returned group
            pathState.getChildren().addAll(drawPath(stateProgression.get(i), true));
            //add group of lines to lines ui object
            lines.getChildren().add(pathState);
            //set up instant fade in transition and add to sequential transition
            ft = new FadeTransition(Duration.millis(1), pathState);
            ft.setFromValue(0.0);
            ft.setToValue(100.0);
            ft.setDelay(Duration.millis(1));
            st.getChildren().add(ft);
            if(i != stateProgression.size() - 1)
            {
                //set up delayed instant fade out transition and add to sequential transition
                ft = new FadeTransition(Duration.millis(1), pathState);
                ft.setFromValue(100.0);
                ft.setToValue(0.0);
                ft.setDelay(Duration.millis(500));
                st.getChildren().add(ft);
            }
        }
        //play animation of drawn lines at each state of the solution
        st.play();
    }

    //draws progression of genetic populations
    private void drawGeneticHistory(ArrayList<GenRoute> ancestry)
    {
        Group path;
        FadeTransition ft;
        st = new SequentialTransition();
        for(int i = 1; i < ancestry.size(); i+=10)
        {
        //create new group
        path = new Group();

            //don't repeat identical solutions
            if(ancestry.get(i - 1).getDistance() != ancestry.get(i).getDistance())
            {
                //draw lines and assign returned group
                path.getChildren().addAll(drawPath(ancestry.get(i).getPath(), true));
                //add group of lines to lines ui object
                lines.getChildren().add(path);
                //set up instant fade in transition and add to sequential transition
                ft = new FadeTransition(Duration.millis(0.1), path);
                ft.setFromValue(0.0);
                ft.setToValue(100.0);
                ft.setDelay(Duration.millis(0.1));
                st.getChildren().add(ft);
                //don't fade out last solution
                if(i != ancestry.size() - 1)
                {
                    //set up delayed instant fade out transition and add to sequential transition
                    ft = new FadeTransition(Duration.millis(0.1), path);
                    ft.setFromValue(100.0);
                    ft.setToValue(0.0);
                    ft.setDelay(Duration.millis(30));
                    st.getChildren().add(ft);
                }
            }

        }
        //play animation of drawn lines at each state of the solution
        st.play();
    }

    //load a new window using a new fxml file to list statistics and graph data
    public void showGeneticStatistics() throws Exception
    {
        Stage statisticsWindow = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("statistics.fxml"));
        Parent root = loader.load();
        statisticsWindow.setTitle("Genetic Statistics");
        statisticsWindow.setScene(new Scene(root, 800, 600));
        statisticsWindow.show();
    }


    public void onRepeatRuns()
    {
        for(int x = 0; x < 50; x++)
            onBtnCalcClick();
    }
}
