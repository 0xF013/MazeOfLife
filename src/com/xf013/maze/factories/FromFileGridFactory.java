package com.xf013.maze.factories;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import sun.misc.Regexp;

import com.xf013.maze.Grid;

public class FromFileGridFactory implements GridFactory {
	
	private String filename;
	
	public FromFileGridFactory(String filename) {
		this.filename = filename;
	}

	@Override
	public Grid createGrid() throws GridFactoryException{
		FileInputStream fstream;
		try {
			fstream = new FileInputStream(filename);

		  // Get the object of DataInputStream
		  DataInputStream in = new DataInputStream(fstream);
		  BufferedReader br = new BufferedReader(new InputStreamReader(in));
		  String strLine;
		  // gonna split by " +" because there might be more than one whitespace
		  // given the conditions
		  String splitRegex = " +";
		  
		  String[] parts = br.readLine().split(splitRegex);
		  int height = Integer.valueOf(parts[0]);
		  int width = Integer.valueOf(parts[1]);
		  
		  parts = br.readLine().split(splitRegex);
		  int targetX = Integer.valueOf(parts[0]) - 1;
		  int targetY = Integer.valueOf(parts[1]) - 1;			  
		  
		  parts = br.readLine().split(splitRegex);
		  int intelligentX = Integer.valueOf(parts[0]) - 1;
		  int intellingentY = Integer.valueOf(parts[1]) - 1;		
		  
	  
		  Grid grid = new Grid(width, height, intelligentX, intellingentY, targetX, targetY);
		  while ((strLine = br.readLine()) != null)   {
			  parts = strLine.split(splitRegex);
			  for (int i = 0; i <= parts.length - 1; i+=2) {
				  if (parts[i].equals("0") && parts[i+1].equals("0")) {
					  break;
				  }
				  grid.setCell(Integer.valueOf(parts[i]) - 1, Integer.valueOf(parts[i+1]) - 1, true);
			  }
		  }
		  in.close();
		  return grid;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new GridFactoryException(e);
		}		  
		
		
	}

}
