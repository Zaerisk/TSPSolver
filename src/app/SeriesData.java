package app;

import javafx.scene.chart.XYChart;

//quick and simple class for holding series data for comparison of two calculations
public class SeriesData
{
    private static XYChart.Series<Integer, Double> Series;
    private static XYChart.Series<Integer, Double> prevSeries;

    public static XYChart.Series<Integer, Double> getSeries()
    {
        return Series;
    }

    public static void setSeries(XYChart.Series<Integer, Double> series)
    {
        Series = series;
    }

    public static XYChart.Series<Integer, Double> getPrevSeries()
    {
        return prevSeries;
    }

    public static void setPrevSeries(XYChart.Series<Integer, Double> prevSeries)
    {
        SeriesData.prevSeries = prevSeries;
    }
}
