// Main class

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import javax.swing.*;

public class Main
{
	static final String TITLE = "IMPULSE Physics Engine - By: Tommy Chen";
	// Atom
	static final int maxAtom = 100; // maximum amount of Atoms (unlimited)
	static final double radius = 3; // radius
	static final double mass = 1; // mass
	static final double corA = 0.95; // coefficient of restitution of Atom 
	// Plane
	static final int maxPlane = 100; // maximum amount of Planes
	static final double corP = 0.7; //coefficient of restitution of Plane
	// Setup
	static final int WINDOWL = 1024; // length of window
	static final int WINDOWH = 740; // height of window
	static final int END = 1000; // distance from end to window
	// Global
	static int ballSize; // amount of ball being processed
	static int wallSize; // amount of wall being processed
	static int endSize; // amount of end being processed
	// Main
	static Atom ballUser; // user controlled Atom
	static Atom[] ball; // array of Atoms
	static Plane[] wall; // array of Planes
	static Plane[] end; // array of Planes that deletes Atoms
	static JFrame frame; // window
	static Draw draw; // Graphics
	static Engine engine; // physics
	static Control control; // input
	
	// Save
	static String outputSaveName; // save file name (.save)
	static String inputSaveName; // load file name (.save)
	static JPanel pane; // pane
	
	// Constructor
	public Main ()
	{
		frame = new JFrame(TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(WINDOWL, WINDOWH);
		//frame.setResizable(false);
		frame.getContentPane().setBackground(Color.white);
		
		pane = (JPanel) frame.getContentPane();
		pane.setLayout(new BorderLayout());
		initialize();
		draw = new Draw();
		frame.add(draw, BorderLayout.CENTER);
		//pane.add(new JButton("button"), BorderLayout.SOUTH);
		
		engine = new Engine();
		control = new Control();
		frame.setVisible(true);
		engine.start();
	}
	
	// Initialize variables
	public static void initialize ()
	{
		ball = new Atom[maxAtom];
		wall = new Plane[maxPlane];
		end = new Plane[4];
		ballSize = 0;
		wallSize = 1;
		endSize = 4;
		Universe.fpsCap = 125;
		Universe.fps = Universe.fpsCap;
		Universe.mpp = 0.0003 * 50;
		Atom.setDensity (1.184);
		Atom.setCof (0.15);
		Atom.setGravity (9.81);
		Plane.setDepth(10);
		
		end[0] = new Plane(0 - END, WINDOWH + END, WINDOWL + END, WINDOWH + END, 0);
		end[1] = new Plane(WINDOWL + END, -END, -END, -END, 0);
		end[2] = new Plane(WINDOWL + END, WINDOWH + END, WINDOWL + END, -END, 0);
		end[3] = new Plane(-END, -END, 0 - END, WINDOWH + END, 0);
		wall[0] = new Plane(100, 500, 900, 500, 0);
		ballUser = new Atom(200, 375, 10, 1, 0.95, 0, 0);
		inputSave(inputSaveName);
	}
	
	
	// Main
	public static void main (String[] args)
	{
		if (args.length == 1)
		{
			inputSaveName = args[0];
			outputSaveName = args[0];
		}
		else
		{
			inputSaveName = "pool";
			outputSaveName = "save";
		}
		new Main();
	}
	
	// Plus 1 Atom
	public static void plusAtom (double x, double y, double radius, double mass, double cor, double xV, double yV)
	{
		if (ballSize >= ball.length)
		{
			Atom[] temp = new Atom [2 * ballSize];
			for (int i = 0; i < ball.length; i++)
				temp[i] = ball[i];
			ball = temp;
		}
		ball[ballSize] = new Atom(x, y, radius, mass, cor, xV, yV);
		ballSize ++;
	}
	
	// Minus 1 Atom
	public static void minusAtom (int i)
	{
		ballSize--;
		ball[i] = new Atom(ball[ballSize]);
		ball[ballSize] = null;
	}
	
	// Plus 1 Plane
	public static void plusPlane (double xO, double yO, double l1, double l2, double angle, double magnitude, double cor)
	{
		if (wallSize >= wall.length)
		{
			Plane[] temp = new Plane [2 * wall.length];
			for (int i = 0; i < wall.length; i++)
				temp[i] = wall[i];
			wall = temp;
		}
		wall[wallSize] = new Plane(xO, yO, l1, l2, angle, magnitude, cor);
		wallSize ++;
	}
	
	// read in a save file
	public static void inputSave (String name)
	{
		//Control.isPause = true;
		//ballSize = 0;
		//wallSize = 0;
		try
		{
			String input = "";
			double x, y, radius, mass, corA, xV, yV = 0;
			double xO, yO, l1, l2, angle, magnitude, corP = 0;
			String type;
			FileReader fr = new FileReader(name + ".save");
			BufferedReader inFile = new BufferedReader(fr);
			input = inFile.readLine();
			while (input != null)
			{
				StringTokenizer par = new StringTokenizer(input, " \t\n\r\f,()");
				type = par.nextToken();
				if (type.equalsIgnoreCase("ball"))
				{
					x = Double.parseDouble(par.nextToken());
					y = Double.parseDouble(par.nextToken());
					radius = Double.parseDouble(par.nextToken());
					mass = Double.parseDouble(par.nextToken());
					corA = Double.parseDouble(par.nextToken());
					xV = Double.parseDouble(par.nextToken());
					yV = Double.parseDouble(par.nextToken());
					plusAtom(x, y, radius, mass, corA, xV, yV);
				}
				else if (type.equalsIgnoreCase("wall"))
				{
					xO = Double.parseDouble(par.nextToken());
					yO = Double.parseDouble(par.nextToken());
					l1 = Double.parseDouble(par.nextToken());
					l2 = Double.parseDouble(par.nextToken());
					angle = Double.parseDouble(par.nextToken());
					magnitude = Double.parseDouble(par.nextToken());
					corP = Double.parseDouble(par.nextToken());
					plusPlane(xO, yO, l1, l2, angle, magnitude, corP);
				}
				input = inFile.readLine();
			}
			inFile.close();
		} catch(IOException e)
		{
			System.out.println("The following file is corrupted: " + name + "\n" + e);
		}
	}
	
	// Writes a file
	public static void outputSave (String name, Atom[] atom, int atomSize, Plane[] plane, int planeSize)
	{
		try
		{
			FileWriter fw = new FileWriter(name + ".save");
			PrintWriter outFile = new PrintWriter(fw);
			for (int i = 0; i < atomSize; i++)
				if (ball[i] != null)
					outFile.println("ball (" + atom[i].info() + ")");
			for (int i = 0; i < planeSize; i++)
				if (wall[i] != null)
					outFile.println("wall (" + plane[i].info() + ")");
			outFile.close();
		} catch (IOException e)
		{
			System.out.println("Error:\n" + e);
		}
	}
}