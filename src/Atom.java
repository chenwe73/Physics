// The fundamental atom that responds to forces

public class Atom 
{
	public int xCoord; // x coordinate of centre, (0, 0) at top left
	public int yCoord; // y coordinate of centre (pixel)
	double radius; // radius of circle (pixel)
	double mass; // mass (kg)
	double cor; // coefficient of restitution
	private static double g = 0; // gravitational field strength (N/kg) (down = +)
	private static double cof = 0; // coefficient of kinetic friction
	private static double density = 0; // density of surrounding fluid (kg/m^3)
	private double invMass; // 1 / mass (1/kg)
	private double xF; // horizontal force (N) (right = +)
	private double yF; // vertical velocity (N) (up = +)
	private double xA; // horizontal acceleration (m/s/s) (right = +)
	private double yA; // vertical acceleration (m/s/s) (up = +)
	private double xV; // horizontal velocity (m/s) (right = +)
	private double yV; // vertical velocity (m/s) (up = +)
	private double x; // x coordinate of centre, (0, 0) at top left
	private double y; // y coordinate of centre (pixel)
	
	// Constructor
	public Atom (double x, double y, double radius, double mass, double cor, double xV, double yV)
	{
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.mass = mass;
		this.cor = cor;
		this.xV = xV;
		this.yV = yV;
		xA = 0;
		yA = 0;
		xF = 0;
		yF = 0;
		invMass = 1.0 / mass;
		xCoord = (int) Math.round(x);
		yCoord = (int) Math.round(y);
	}
	
	// Constructor of a copy
	public Atom (Atom a)
	{
		this(a.x, a.y, a.radius, a.mass, a.cor, a.xV, a.yV);
	}
	
	// Default constructor
	public Atom ()
	{
		this(10, 10, 10, 1, 1, 0, 0);
	}
	
	// Override toString
	public String toString ()
	{
		return String.format("x: %10.6f y: %10.6f @ xV: %10.6f(m/s) yV: %10.6f(m/s)" ,x, y, xV, yV);
	}
	
	// Information of atom
	public String info ()
	{
		return "" + x + ", " + y + ", " + radius + ", " + mass + ", " + cor + ", " + xV + ", " + yV;
	}
		
	// Mutator of variable g
	public static void setGravity (double newG)
	{
		g = newG;
	}
	
	// Mutator of variable cof
	public static void setCof (double newCof)
	{
		cof = newCof;
	}
	
	// Mutator of variable density
	public static void setDensity (double newDensity)
	{
		density = newDensity;
	}
	 
	// Net speed, range = (+)
	public double netV ()
	{
		return Math.sqrt(xV * xV + yV * yV);
	}
	
	// Is at rest and will stay at rest
	public boolean isStationary ()
	{
		return xV == 0 && yV == 0 && xF == 0 && yF == 0;
	}
	
	// Input force towards angle
	public void applyForceAngle (double angle, double net)
	{
		// adjacent = cosine (angle) * hypotenuse
		xF = Math.cos(angle) * net;
		// opposite = sine (angle) * hypotenuse
		yF = Math.sin(angle) * net;
		react(); // ???
	}
	
	// Input force towards angle
	public void applyForceVector (double xV, double yV, double net)
	{
		applyForceAngle(Universe.angleVector(xV, yV), net);
	}
	
	// Input force towards (xM, yM) coordinates
	public void applyForceCoord (double xM, double yM, double net)
	{
		applyForceAngle(Universe.angleCoord(x, y, xM, yM), net);
	}
	
	// Input impulse towards angle
	public void applyImpulseAngle (double angle, double net)
	{
		applyForceAngle(angle, net * mass * Universe.fpsCap);
	}
	
	// Input elastic force towards (xM, yM)
	public void applyElastic (double xM, double yM, double mF)
	{
		xF = (xM - x) * mF;
		yF = -(yM - y) * mF;
		react();
	}// unfinished
	
	// Input repulsive field force with the centre of field at (xM, yM)
	// f is the force applied at 1 pixel distance
	public void applyRepulsion (double xM, double yM, double f, double mRadius)
	{
		// Fg = G * m * M / distance^2
		double net;
		final double SAFE_DIST = mRadius / 4;
		if (mRadius == 0)
			net = 0;
		else if (Universe.distance(x, y, xM, yM) <= SAFE_DIST)
			net = f * mRadius / Math.pow(SAFE_DIST, 2);
		else
			net = f * mRadius / Math.pow(Universe.distance(x, y, xM, yM), 2);
		if (Universe.distance(x, y, xM, yM) == 0 && mRadius != 0) // close to centre of field (force == infinity)
			applyForceAngle(Math.random() * 2 * Math.PI, 10 * mass); // random force
		else if (Universe.distance(x, y, xM, yM) - radius <= mRadius) // within field
		{
			applyForceCoord(xM, yM, -net);
			//applyFriction(1); // elastic resistance
		}
		else;
	}
	
	// Input attractive field force with the centre of field at (xM, yM)
	// f is the force applied at 1 pixel distance
	public void applyAttraction (double xM, double yM, double f, double mRadius)
	{
		double net;
		if (mRadius == 0)
			net = 0;
		else
			net = f * mRadius / Math.pow(1 / Universe.distance(x, y, xM, yM), 2);
		if (Universe.distance(x, y, xM, yM) <= 0 && mRadius != 0)
			;
		else if (Universe.distance(x, y, xM, yM) - radius <= mRadius)
		{
			applyForceCoord(xM, yM, net);
		}
		else;
	}
	
	//Input collision field force with the centre of field at (xM, yM)
	public void applyCollide (double xM, double yM, double field)
	{
		if (Universe.distance(x, y, xM, yM) <= field)
		{
			xF = Math.cos(Universe.angleCoord(x, y, xM, yM)) * netV() * 2 * mass;
			yF = Math.sin(Universe.angleCoord(x, y, xM, yM)) * netV() * 2 * mass + g * mass;
		}
	}// unfinished
	
	// Input gravitational force
	public void applyGravity (double angle)
	{
		//Fg = m * g
		applyForceAngle(angle, mass * g);
	}
	
	//Input friction force
	public void applyFriction (double net)
	{
		// acceleration due to friction is greater than net velocity
		if (xV == 0 && yV == 0) // Atom is at rest
		;
		else if (net / mass / Universe.fpsCap >= netV())
			// -v * m / m + v = 0
			applyForceVector(xV, yV, -netV() * mass * Universe.fpsCap);
		else // Atom is in motion
			// direction of friction force is the opposite of velocity
			applyForceVector(xV, yV, -net);
	}
	
	//Input friction force at top view
	public void applyNormalFriction ()
	{
		// cof = Fk / FN
		final double NET = Math.abs(mass * g) * Atom.cof;
		applyFriction (NET);
	}
	
	// Input air drag force
	public void applyDrag ()
	{
		// FD = 1/2 * relative velocity^2 * surface area * drag coefficient * density
		final double NET = 0.5 * netV() * netV() * Math.PI * Math.pow(radius * Universe.mpp, 2) * 0.4 * density;
		if (xV == 0 && yV == 0) // Atom is at rest
			;
		else
			// direction of air drag force is the opposite of velocity
			applyForceVector(xV, yV, -NET);
	}
	
	// Process forces, reaction engine
	public void react ()
	{
		// F = m * a
		if (mass == Double.POSITIVE_INFINITY)
		{
			xA = 0;
			yA = 0;
		}
		else
		{
			xA = xF / mass;
			yA = yF / mass;
		}
		// a = (v2 - v1) / t
		// (m/s/s) * (s/f) = (m/s[/f])
		xV += xA * 1 / Universe.fpsCap;
		yV += yA * 1 / Universe.fpsCap;
		xF = 0; 
		yF = 0;
		xA = 0;
		yA = 0;
	}
	
	// Integrate the coordinates of Atom
	public void integrate ()
	{
		// d = v * t
		// (m/s) / (f/s) / (m/p) = (p/f)
		x += xV / Universe.fpsCap / Universe.mpp;
		// integration from vector to coordinates require to reverse y
		y -= yV / Universe.fpsCap / Universe.mpp;
		xCoord = (int) Math.round(x);
		yCoord = (int) Math.round(y);
	}
	
	// Is Atom penetrating the Plane
	public boolean isReflectInfinite (Plane p)
	{
		// normal distance = x * p.x + y * p.y + p.magnitude
		final boolean isND = (x - p.xO) * p.x + -(y - p.yO) * p.y + p.magnitude - radius <= 0;
		// relative normal velocity = V[i] dot-product N[i]
		final boolean isRNV = xV * p.x + yV * p.y < 0 ;
		return isND && isRNV;
	}
	
	// Is Atom penetrating the Plane at given parameters
	public boolean isReflect (Plane p)
	{
		// normal distance = x * p.x + y * p.y + p.magnitude
		final boolean isND = (x - p.xO) * p.x + -(y - p.yO) * p.y + p.magnitude - radius <= 0;
		// relative normal velocity = V[i] dot-product N[i]
		final boolean isRNV = xV * p.x + yV * p.y < 0 ;
		final boolean isDepth = (x - p.xO) * p.x + -(y - p.yO) * p.y + p.magnitude >= radius - Plane.depth;
		// sine (relative angle) * distance to midpoint = parallel distance
		final boolean isLeft = Math.sin(Universe.angleCoord(p.xMid, p.yMid, x, y) - (p.angle + Math.PI))
			* Universe.distance(x, y, p.xMid, p.yMid) < p.l1;
		final boolean isRight = Math.sin(-(Universe.angleCoord(p.xMid, p.yMid, x, y) - (p.angle + Math.PI)))
			* Universe.distance(x, y, p.xMid, p.yMid) < p.l2;
		return isND && isRNV && isDepth && isLeft && isRight;
	}

	// Reflect Atom off plane p
	public void reflect (Plane p)
	{
		if (isReflect(p))
		{
			// R = V – (1+e)*N*(V•N)
			final double TEMP = (xV * p.x + yV * p.y) * (1 + (p.cor + cor) / 2);
			xV = xV - p.x * TEMP;
			yV = yV - p.y * TEMP;
		}
	}
	
	// Is Atom going to penetrate Plane
	public boolean isSpecReflect (Plane p)
	{
		// normal distance < relative normal velocity
		final boolean isPenetrate = (x - p.xO) * p.x + -(y - p.yO) * p.y + p.magnitude - radius
			< Math.abs(xV * p.x + yV * p.y) / Universe.mpp / Universe.fpsCap;
		final boolean isRNV = xV * p.x + yV * p.y < 0;
		final boolean isND = (x - p.xO) * p.x + -(y - p.yO) * p.y + p.magnitude - radius >= 0 - 0.0001; // >= 0 is glitch-y
		final boolean isLeft = Math.sin(Universe.angleCoord(p.xMid, p.yMid, x, y) - (p.angle + Math.PI))
			* Universe.distance(x, y, p.xMid, p.yMid) < p.l1;
		final boolean isRight = Math.sin(-(Universe.angleCoord(p.xMid, p.yMid, x, y) - (p.angle + Math.PI)))
			* Universe.distance(x, y, p.xMid, p.yMid) < p.l2;
		return isPenetrate && isRNV && isND && isLeft && isRight;
	}
	
	// Applies an impulse so Atom does not penetrate Plane
	public void specReflect (Plane p)
	{
		if (isSpecReflect(p))
		{
			// impulse = -(relative normal velocity + distance)
			final double REMOVE = -((xV * p.x + yV * p.y) + 
				((x - p.xO) * p.x + -(y - p.yO) * p.y + p.magnitude - radius) * Universe.fpsCap * Universe.mpp);
			applyImpulseAngle(p.angle + Math.PI, REMOVE);
		}
	}
	
	// Is Atom a penetrating Atom b
	public static boolean isCollide (Atom a, Atom b)
	{
		final boolean isPenetrate = Universe.distance(a.x, a.y, b.x, b.y) < a.radius + b.radius;
		final boolean isRNV = (a.xV - b.xV) * (a.x - b.x) + (a.yV - b.yV) * -(a.y - b.y) <= 0;
		return isPenetrate && isRNV;
	}
	
	// Is Atom a going to penetrate Atom b
	public static boolean isSpecCollide (Atom a, Atom b)
	{
		return Universe.distance(a.x, a.y, b.x, b.y) - a.radius - b.radius
			< -((a.xV - b.xV) * (a.x - b.x) + (a.yV - b.yV) * (a.y - b.y))
			&& (a.xV - b.xV) * (a.x - b.x) + (a.yV - b.yV) * (a.y - b.y) <= 0;
	}//not working
	
	// Reflect Atom a and Atom b off each other
	public static void collide (Atom a, Atom b)
	{
		if (isCollide(a, b))
		{
			// I = (1+e)*N*(Vr • N) / (1/Ma + 1/Mb)
			final double TEMP = ((a.xV - b.xV) * Math.cos(Universe.angleCoord(a.x, a.y, b.x, b.y)) + 
				(a.yV - b.yV) * Math.sin(Universe.angleCoord(a.x, a.y, b.x, b.y))) / 
				(a.invMass + b.invMass) * (1 + (a.cor + b.cor) / 2);
			//Va –= I * 1/Ma
			a.xV -= Math.cos(Universe.angleCoord(a.x, a.y, b.x, b.y)) * TEMP * a.invMass;
			a.yV -= Math.sin(Universe.angleCoord(a.x, a.y, b.x, b.y)) * TEMP * a.invMass;
			//Vb += I * 1/Mb
			b.xV += Math.cos(Universe.angleCoord(a.x, a.y, b.x, b.y)) * TEMP * b.invMass;
			b.yV += Math.sin(Universe.angleCoord(a.x, a.y, b.x, b.y)) * TEMP * b.invMass;
		}
	}
	
	// Soft-body collision
	public static void softCollide (Atom a, Atom b, double mF)
	{
		if (Universe.distance(a.x, a.y, b.x, b.y) <= a.radius + b.radius)
		{
			a.applyRepulsion(b.x, b.y, mF, b.radius);
			b.applyRepulsion(a.x, a.y, mF, a.radius);
		}
	} // over penetration causes "nuke" effect
	
	// Discrete impulse collision
	public static void discCollide (Atom a, Atom b)
	{
		if (Universe.distance(a.x, a.y, b.x, b.y) < a.radius + b.radius)
		{
			final double impulseA = (a.radius + b.radius - Universe.distance(a.x, a.y, b.x, b.y)) * 50 * Universe.mpp;
			final double impulseB = (a.radius + b.radius - Universe.distance(a.x, a.y, b.x, b.y)) * 50 * Universe.mpp;
			a.applyImpulseAngle(Universe.angleCoord(b.x, b.y, a.x, a.y), impulseA);
			b.applyImpulseAngle(Universe.angleCoord(a.x, a.y, b.x, b.y), impulseB);
		}
		
	}
}
