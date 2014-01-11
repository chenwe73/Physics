import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

// Mouse and keyboard inputs

class Control implements MouseMotionListener, MouseListener, KeyListener, MouseWheelListener
{
	// Mouse
	static int xM; // x coordinate of mouse (pixel)
	static int yM; // y coordinate of mouse (pixel)
	static double wheel; // mouse wheel
	static double mForceInc; // mouse force increment (N/kg/s)
	static double maxF; // maximum mouse force (N/kg)
	static boolean isMouse; // is mouse hold down
	static double mForce; // mouse force (N)
	// Keyboard
	static double keyForce; // keyboard force (N/kg)
	static boolean[] isKey; // is keyboard hold down
	// Magnet
	static boolean isMagnet; // is mouse force field on
	static boolean isMagnetActive; // is mouse force field closing / opening
	static int mRadius; // mouse force field radius (pixel)
	static int mRadiusInc; // mouse force field radius increment (pixel/s)
	static double mField; // mouse force field force (field unit)
	static double maxMRadius; // Maximum mouse force field radius (pixel)
	// Others
	static boolean isGravity; // is gravity on
	static boolean isAdd; // is add Atom
	static boolean isDelete; // is delete Atom
	static boolean isShoot; // is shoot Atom
	static boolean isTap; // is tap on
	static boolean isShader; // is shadow on
	static boolean isPrint; // is system messages on
	static boolean isPause; // is pause on
	static boolean isSave; // is save
	static boolean isRefresh; // is refresh
	
	public Control ()
	{
		Main.draw.addMouseMotionListener(this);
		Main.draw.addMouseListener(this);
		Main.draw.addMouseWheelListener(this);
		Main.frame.addKeyListener(this);
		Control.initialize();
	}
	
	// Initialize all fields
	public static void initialize ()
	{
		//xM = 0;
		//yM = 0;
		wheel = 0;
		mForceInc = 10;
		maxF = 10000;
		isMouse = false;
		mForce = 0;
		keyForce = 10;
		isKey = new boolean[4];
		isMagnet = false;
		isMagnet = false;
		mRadius = 0;
		mRadiusInc = 3;
		mField = 2000;
		maxMRadius = 50;
		isGravity = false;
		isAdd = false;
		isDelete = false;
		isShoot = false;
		isTap = false;
		isShader = false;
		isPrint = false;
		isPause = false;
	}
	
	// Add an Atom with keyboard
	public static void add ()
	{
		if (isAdd == true)
			Main.plusAtom(xM, yM, Main.radius, Main.mass, Main.corA, 0, 0);
		isAdd = false;
	}
	
	// Delete an Atom with keyboard
	public static void delete ()
	{
		if (isDelete == true && Main.ballSize > 0)
			Main.minusAtom(Main.ballSize);
		isDelete = false;
	}
	
	
	// Spawn random Atoms
	public static void tap (double x1, double y1, double x2, double y2, double flow)
	{
		for (int i = 1; isTap && i <= Math.round(flow / Universe.fpsCap); i++)
			Main.plusAtom(x1 + Math.random() * (x2 - x1), y1 + Math.random() * (y2 - y1), 
					Main.radius, Main.mass, Main.corA, 0, 0);
	}
	
	// Spawn random Atoms on mouse pointer
	public static void mouseTap (double length, double height, double flow)
	{
		tap(xM - length / 2, yM - height / 2, xM + length / 2, yM + height / 2, flow);
	}
	
	// Open and close magnet field
	public static void magnetSwitch ()
	{
		if (isMagnet && mRadius < maxMRadius && isMagnetActive)
			if (mRadius + mRadiusInc > maxMRadius)
				mRadius += maxMRadius - mRadius;
			else
				mRadius += mRadiusInc;
		else if (!isMagnet && mRadius > 0 && isMagnetActive)
			if (mRadius - mRadiusInc < 0)
				mRadius -= mRadius;
			else
				mRadius -= mRadiusInc;
		if (isMagnet && mRadius == maxMRadius || !isMagnet && mRadius == 0)
			isMagnetActive = false;
	}
	
	// Zoom the magnet field with mouse wheel
	public static void magnetZoom ()
	{
		wheel *= -mRadiusInc;
		if (wheel < 0 && mRadius + wheel < 0)
			mRadius -= mRadius;
		else if (wheel > 0 || wheel < 0 && mRadius > 0)
			mRadius += wheel;
		wheel = 0;
	}
	
	// Apply arrow key forces
	public static void arrowKey (Atom atom)
	{
		if (atom != null)
		{
			if (isKey[0] == true)
				atom.applyForceAngle (Math.PI * 1 / 2, keyForce * atom.mass);
			if (isKey[1] == true)
				atom.applyForceAngle (Math.PI * 3 / 2, keyForce * atom.mass);
			if (isKey[2] == true)
				atom.applyForceAngle (Math.PI, keyForce * atom.mass);
			if (isKey[3] == true)
				atom.applyForceAngle (Math.PI * 2, keyForce * atom.mass);
		}
	}
	
	// Mouse force increase
	public static void mForceInc (Atom atom)
	{
		if (atom != null && mForce < maxF * atom.mass && isMouse == true)
			mForce += mForceInc * atom.mass;
	}
	
	// Apply magnet force
	public static void applyMagnet (Atom atom)
	{
		atom.applyRepulsion(xM, yM, mField * atom.mass, mRadius);
		//atom.applyAttraction(xM, yM, mField * atom.mass, mRadius);
	}
	
	// Release of mouse button and apply instantaneous force on Atom
	public static void shoot (Atom atom)
	{
		if (isShoot)
		{
			if (atom != null)
				atom.applyForceCoord(xM, yM, mForce);
			mForce = 0;
			isShoot = false;
		}
	}
	
	public static void save ()
	{
		if (isSave)
		{
			Main.outputSave(Main.outputSaveName, Main.ball, Main.ballSize, Main.wall, Main.wallSize);
			isSave = false;
		}
	}
	
	public static void refresh()
	{
		if (isRefresh)
		{
			Main.initialize();
			//initialize();
		}
		isRefresh = false;
	}
	
	// Keyboard
	public void keyPressed(KeyEvent e) 
	{
		int keyCode = e.getKeyCode();
		switch (keyCode) 
		{
		// '`' turn on/off system messages
		case KeyEvent.VK_BACK_QUOTE :
			isPrint = !isPrint;
			break;
		// ' ' pause dynamics
		case KeyEvent.VK_SPACE:
			isPause = !isPause;
			break;
		// 'Enter' restart program
		case KeyEvent.VK_ENTER:
			if (isPrint)
				System.out.println("Refresh");
			isRefresh = true;
			break;
		// 'F1' turn on/off gravity
		case KeyEvent.VK_F1:
			isGravity = !isGravity;
			break;
		// 'F2' turn on/off tap
		case KeyEvent.VK_F2:
			isTap = !isTap;
			break;
		// 'F2' turn on/off shadow
		case KeyEvent.VK_F3:
			isShader = !isShader;
			break;
		// 'F9' saves current world
		case KeyEvent.VK_F9:
			isSave = true;
			break;
		// 'Shift' turn on/off magnet
		case KeyEvent.VK_SHIFT:
			isMagnet = !isMagnet;
			isMagnetActive = true;
			break;
		// '=' add an Atom
		case KeyEvent.VK_EQUALS : 
			//if (xM > X && xM < X + LENGTH && yM > Y && yM < Y + HEIGHT && 
			isAdd = true;
			if (isPrint)
				System.out.println("New ball");
			break;
		// '-' delete an Atom
		case KeyEvent.VK_MINUS:
			isDelete = true;
			if (isPrint)
				System.out.println("Delete ball");
			break;
		// 'Arrow keys' apply force in different directions
		case KeyEvent.VK_UP:
			isKey[0] = true;
			break;
		case KeyEvent.VK_DOWN:
			isKey[1] = true;
			break;
		case KeyEvent.VK_LEFT:
			isKey[2] = true;
			break;
		case KeyEvent.VK_RIGHT:
			isKey[3] = true;
			break;
		// 'Esc' exit program
		case KeyEvent.VK_ESCAPE : 
			if (isPrint)
				System.out.println("Program terminated.");
			System.exit(0);
			break;
		}
	}

	public void keyReleased (KeyEvent e) 
	{
		int keyCode = e.getKeyCode();
		switch (keyCode) 
		{
		case KeyEvent.VK_UP:
			isKey[0] = false;
			break;
		case KeyEvent.VK_DOWN:
			isKey[1] = false;
			break;
		case KeyEvent.VK_LEFT:
			isKey[2] = false;
			break;
		case KeyEvent.VK_RIGHT:
			isKey[3] = false;
			break;
		}
	} 

	public void keyTyped (KeyEvent e) {}

	public void  mouseClicked (MouseEvent e) {}

	public void  mouseEntered (MouseEvent e) {}

	public void  mouseExited (MouseEvent e) {}

	public void  mousePressed (MouseEvent e)
	{
		isMouse = true;
	}

	public void mouseReleased (MouseEvent e)
	{
		isMouse = false;
		isShoot = true;
		if (isPrint)
			System.out.println("Shoot");
	}

	public void mouseDragged (MouseEvent e)
	{
		xM = e.getX();
		yM = e.getY();
	}

	public void mouseMoved (MouseEvent e)
	{
		xM = e.getX();
		yM = e.getY();
	}

	public void mouseWheelMoved (MouseWheelEvent e)
	{
		wheel = e.getWheelRotation();
	}
}
