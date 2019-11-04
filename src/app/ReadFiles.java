package app;

import app.TSP.Vert;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Optional;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

public class ReadFiles 
{
    //opens a file selection dialog and returns file objected wrapped in an
    //..optional if one was selected
    public static Optional<File> fileSelection()
    {
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"), FileSystemView.getFileSystemView());
        fileChooser.setFileFilter(new FileNameExtensionFilter("TSP Files", "tsp"));
        
        int ok = fileChooser.showOpenDialog(null);
        
        //check if a file was selected and return as an optional
        if(ok == JFileChooser.APPROVE_OPTION)
            return Optional.of(new File(fileChooser.getSelectedFile().getAbsolutePath()));
        //just return an empty optional
        else
            return Optional.empty();
    }
    
    //scan through a .tsp file and get the coordinate values for each node as ArrayList
    public static ArrayList<Vert> getTSPVerts(String path)
    {
        Scanner fileScan = null;
        ArrayList<Vert> vertArr = new ArrayList<>();
        //try catch to catch filenotfoundexception and exit program
        try
        {
            fileScan = new Scanner(new File(path));
        }
        catch(FileNotFoundException fnf)
        {
            System.out.println(fnf.getMessage());
            System.exit(0);
        }
        
        Vert vert;
        int n;
        double x;
        double y;
        
        //continue with scanner until EOF
        while(fileScan.hasNextLine())
        {
            //check if the next line starts with int (indicates start of coords)
            if(fileScan.hasNextInt())
            {
                //save number of node and its following two coords
                n = fileScan.nextInt();
                x = fileScan.nextDouble();
                y = fileScan.nextDouble();
                
                //create node with coords and add to Node array
                vert = new Vert(n, x, y);
                vertArr.add(vert);
            }
            //move to next line
            fileScan.nextLine();
        }
        return vertArr;
    }
}
