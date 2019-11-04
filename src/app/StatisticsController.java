package app;

import app.Algorithms.GA;
import app.TSP.GenRoute;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.util.ArrayList;

//controller that pulls up a new window with graphed data and listed statistics
public class StatisticsController
{
    @FXML
    Text txtStatistics;
    @FXML
    LineChart<Integer, Double> lineChart;
    @FXML
    NumberAxis yAxis;
    @FXML
    Button btnCompare;

    XYChart.Series<Integer, Double> series;

    @FXML
    private void initialize()
    {
        //disable compare button at start
        btnCompare.setDisable(true);
        btnCompare.setVisible(false);

        //shift series data over if there is pre-existing data
        if(SeriesData.getSeries() != null)
            SeriesData.setPrevSeries(SeriesData.getSeries());

        //list statistics string
        txtStatistics.setText(GA.getStatistics());
        ArrayList<GenRoute> ancestry = GA.getAncestry();
        series = new XYChart.Series<>();
        String strWOC;
        if(GA.isUseWOC())
            strWOC = "WOC";
        else
            strWOC = "No WOC";
        series.setName(GA.getCrossoverType() + ", " + GA.getMutationType() + ", " +strWOC);

        for(int i = 0; i < ancestry.size(); i+= 10)
        {
            GenRoute genRoute = ancestry.get(i);
            series.getData().add(new XYChart.Data<>(i, genRoute.getDistance()));
        }

        lineChart.getData().add(series);
        SeriesData.setSeries(series);

        //if prexisting series, enable compare button
        if(SeriesData.getPrevSeries() != null)
        {
            btnCompare.setDisable(false);
            btnCompare.setVisible(true);
        }
    }

    //compare with previous series if another series has previously been graphed
    public void onCompareClick()
    {
        if(SeriesData.getPrevSeries() != null)
        {
            lineChart.setLegendVisible(true);
            lineChart.getData().add(SeriesData.getPrevSeries());
        }
    }
}
