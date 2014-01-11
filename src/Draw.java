// Graphics

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;

class Draw extends JComponent
{
	private static final long serialVersionUID = 1L; // ???
	static long timeLast; // time of end of last loop (millisecond)
	static long timeNow; // time of end of current loop (millisecond)
	static double fpsscr; // screen FPS (Hz)
	int width; // width of JComponent (pixel)
	int height; // height of JComponent (pixel)
	
	public Draw ()
	{
		repaint();
	}

	public void paint (Graphics g1)
	{
		width = getWidth();
		height = getHeight();
		Graphics2D g = (Graphics2D)g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setColor(Color.red);
		dispMouse (g, Main.ballUser, Control.xM, Control.yM);
		g.setColor(Color.gray);
		dispSpecialAtom (g, Main.ballUser);
		g.setColor(Color.gray);
		dispAtoms (g, Main.ball, Main.ballSize);
		g.setColor(Color.blue);
		dispPlane (g, Main.wall, Main.wallSize);
		g.setColor(Color.red);
		dispPlane (g, Main.end, Main.endSize);
		g.setColor(Color.blue);
		g.setFont(new Font("Monospaced", Font.PLAIN, 13));
		dispInformation(g);
		updatefps();
	}
	
	// Mouse pointer
	public static void dispMouse (Graphics2D g, Atom atom, int xM, int yM)
	{
		g.drawLine(xM, yM, atom.xCoord, atom.yCoord);
		g.drawOval(xM - Control.mRadius, yM - Control.mRadius, Control.mRadius * 2, Control.mRadius * 2);
	}
	
	// Special Atom
	public static void dispSpecialAtom (Graphics2D g, Atom atom)
	{
		if (Control.isShader)
			for (int j = 0; j <= atom.radius; j++)
			{
				g.setColor(grayScale(j + 0.3 * atom.radius, atom.radius * 1.5));
				g.fillOval(atom.xCoord - (int)atom.radius + j, atom.yCoord - (int)atom.radius + j, 
						(int)atom.radius * 2 - 2 * j, (int)atom.radius * 2 - 2 * j);
			}
		else
		{
			g.drawOval(atom.xCoord - (int)atom.radius, atom.yCoord - (int)atom.radius, 
					(int)atom.radius * 2, (int)atom.radius * 2);
		}
	}
	
	// General Atoms
	public static void dispAtoms (Graphics2D g, Atom[] atom, int atomSize)
	{
		for (int i = 0; i < atomSize; i++)
		{
			if (atom[i] != null)
			{
				if (Control.isShader)
					for (int j = 0; j <= atom[i].radius; j++)
					{
						g.setColor(grayScale(j, atom[i].radius * 1.5));
						g.fillOval(atom[i].xCoord - (int)atom[i].radius + j, atom[i].yCoord - (int)atom[i].radius + j, 
								(int)atom[i].radius * 2 - 2 * j, (int)atom[i].radius * 2 - 2 * j);
					}
				else
				{
					//g.setColor(Color.gray);
					g.fillOval(atom[i].xCoord - (int)atom[i].radius, atom[i].yCoord - (int)atom[i].radius, 
							(int)atom[i].radius * 2, (int)atom[i].radius * 2);
					//g.setColor(Color.BLACK);
					//g.drawOval(atom[i].xCoord - (int)atom[i].radius, atom[i].yCoord - (int)atom[i].radius, 
					//		(int)atom[i].radius * 2, (int)atom[i].radius * 2);
				}
			}
		}
	}
	
	// Planes
	public static void dispPlane (Graphics2D g, Plane[] plane, int planeSize)
	{
		for (int i = 0; i < planeSize; i++)
		{
			if (plane[i] != null)
				g.drawLine((int)plane[i].xEnd1, (int)plane[i].yEnd1, (int)plane[i].xEnd2, (int)plane[i].yEnd2);
		}
	}
	
	// Display information
	public static void dispInformation (Graphics2D g)
	{
		// variables
		g.drawString(String.format("Partical Count: %4d", Main.ballSize), 10, 20);
		g.drawString(String.format("Power: %7.1f (N)", Control.mForce), 10, 40);
		g.drawString(String.format("Physics FPS: %7.1f (Hz)", Universe.fps), 10, 60);
		g.drawString(String.format("Screen FPS: %8.1f (Hz)", fpsscr), 10, 80);
		
		g.drawString(String.format("Metres Per Pixel: %5.3f (m/p), Scale: 1:%1.0f", 
				Universe.mpp, Universe.mpp / 0.0003), 300, 20);
		g.drawString(String.format("FPS Capacity: %5.1f (Hz)", Universe.fpsCap), 300, 40);
		g.drawString("Mouse: (" + Control.xM + ", " + Control.yM + ")", 300, 60);
		
		// booleans
		g.drawString("Gravity: " + Control.isGravity, Main.draw.width - 200, 20);
		g.drawString("Pause: " + Control.isPause, Main.draw.width - 200, 40);
		g.drawString("Fancy Graphics: " + Control.isShader, Main.draw.width - 200, 60);
	}
	
	// returns a gray color
	public static Color grayScale (double colNum, double scale)
	{
		if (colNum > scale)
			colNum = scale;
		int rgbNum = (int)(((double)colNum / scale) * 255); // * 255
		return new Color (rgbNum,rgbNum,rgbNum);
	}
	
	// FPS
	public static void updatefps ()
	{
		timeLast = timeNow;
		timeNow = System.currentTimeMillis();
		fpsscr = 1000.0 / (timeNow - timeLast);
	}
}
