// Physics

class Engine extends Thread
{
	static long timeLast; // time of end of last loop (milliseconds)
	static long timeNow; // time of end of current loop (milliseconds)
	static long sleepTime; // amount of sleep (milliseconds)
	static int i; // index of Atom loop
	static boolean isDeleted; // is disintegrated
	
	public void run ()
	{
		try
		{
			timeNow = System.currentTimeMillis();
			while (true)
			{
				Control.refresh();
				//Control.tap(Main.X + Main.radius + 90, Main.Y + Main.radius, 
						//Main.X + Main.radius + 90 + 5, Main.Y + Main.radius + 5, Universe.fpsCap * 1);
				Control.mouseTap(10, 10, Universe.fpsCap * 1);
				Control.add();
				Control.delete();
				Control.magnetSwitch();
				Control.arrowKey(Main.ballUser);
				Control.mForceInc(Main.ballUser);
				Control.magnetZoom();
				Control.shoot(Main.ballUser);
				if (!Control.isPause)
				{
					physics(Main.ballUser);
					for (i = 0; i < Main.ballSize; i++)
						physics(Main.ball[i], i);
				}
				Main.draw.repaint();
				Control.save();
				// FPS
				// Sleep Time = Total Wait Time - Work Time
				sleepTime = Math.round(1000.0 / Universe.fpsCap) - System.currentTimeMillis() + timeNow;
				if (sleepTime > 0) // have spare time
					Thread.sleep(sleepTime + 0); // + a value for artificial slow motion
				else; // running behind
				updatefps();
			}
		}
		catch(InterruptedException e) {};
	}
	
	// special Atom physics loop
	public static void physics (Atom atom)
	{
		Control.applyMagnet(atom); 
		applyForces(atom, Control.isGravity);
		collision(atom, -1, Main.ball, Main.ballSize);
		reflection(atom, Main.wall, Main.wallSize);
		//disintegration(atom, Main.end, Main.endSize);
		integration(atom);
	}
	
	// Atom physics loop
	public static void physics (Atom atom, int index)
	{
		Control.applyMagnet(atom);
		applyForces(atom, Control.isGravity);
		collision(atom, index, Main.ball, Main.ballSize);
		reflection(atom, Main.wall, Main.wallSize);
		disintegration(atom, Main.end, Main.endSize);
		integration(atom);
	}
	
	// Static forces
	public static void applyForces (Atom atom, boolean isGravity)
	{
		if (isGravity)
			atom.applyGravity(Math.PI * 3 / 2);
	}
	
	public static void collision (Atom atom, int index, Atom[] atoms, int atomSize)
	{
		for (int j = index + 1 ; j < atomSize; j++)
			Atom.collide(atom, atoms[j]);
	}
	
	// Reflection with all Planes
	public static void reflection (Atom atom, Plane[] plane, int planeSize)
	{
		for (int j = 0; j < planeSize; j++)
		{
			if (plane[j] != null)
				//atom.reflect(plane[j]);
				atom.specReflect(plane[j]);
		}
	}
	
	// Disintegration with Planes
	public static void disintegration (Atom atom, Plane[] plane, int planeSize)
	{
		for (int j = 0; j < planeSize; j++)
		{
			if (plane[j] != null && atom.isSpecReflect(plane[j]))
			{
				Main.minusAtom(i);
				isDeleted = true;
			}
		}
	}
	
	// Integration
	public static void integration (Atom atom)
	{
		if (isDeleted)
		{// loop on atom[i] again and skip integration
			i--;
			isDeleted = false;
		}
		else
		{
			atom.applyNormalFriction();
			atom.applyDrag();
			if (atom == Main.ball[0] && Control.isPrint)
				System.out.println(Main.ball[0]);
			atom.integrate();
		}
	}
	
	// FPS
	public static void updatefps ()
	{
		timeLast = timeNow;
		timeNow = System.currentTimeMillis();
		Universe.fps = 1000.0 / (timeNow - timeLast);
	}
}