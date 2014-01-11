// The plane that changes velocities of Atoms

public class Plane 
{
	double xO; // x coordinate of origin, centre of rotation
	double yO; // y coordinate of origin, centre of rotation
	double l1; // length of plane from the Mid to End1
	double l2; // length of plane from the Mid to End2
	double angle; // angle of rotation, origin as centre (in radiance)
	double magnitude; // magnitude of plane vector
	double x; // x of unit vector, the vector is perpendicular to the surface of plane, (right = +)
	double y; // y of unit vector, the vector points at (xO, yO), midpoint as centre (up = +)
	public double xMid; // x coordinate of midpoint of plane line
	public double yMid; // y coordinate of midpoint of plane line
	public double xEnd1; // x coordinate of endpoint1 of plane line
	public double yEnd1; // y coordinate of endpoint1 of plane line
	public double xEnd2; // x coordinate of endpoint2 of plane line
	public double yEnd2; // y coordinate of endpoint2 of plane line
	double cor; // coefficient of restitution
	double spin; // not used
	static double depth; // depth of reflection (pixel)
	
	// Constructor
	public Plane (double xO, double yO, double l1, double l2, double angle, double magnitude, double cor)
	{
		this.xO = xO;
		this.yO = yO;
		this.magnitude = magnitude;
		this.angle = angle;
		this.l1 = l1;
		this.l2 = l2;
		this.cor = cor;
		locate();
	}
	
	// Constructor of endpoints (rotates around midpoint)
	public Plane (double x1, double y1, double x2, double y2, double cor)
	{
		// origin is same as midpoint
		xO = (x1 + x2) / 2;
		yO = (y1 + y2) / 2;
		magnitude = 0;
		angle = Universe.angleCoord(x1, y1, x2, y2) - Math.PI / 2;
		l1 = l2 = Universe.distance(x1, y1, x2, y2) / 2;
		this.cor = cor;
		locate();
	}
	
	// Override toString
	public String toString ()
	{
		return String.format("x: %5.4f y: %5.4f angle: %5.4f*Pi xMid: %5.1f yMid: %5.1f",
								x , y, angle / Math.PI, xMid, yMid);
	}
	
	// Information of atom
	public String info ()
	{
		return "" + xO + ", " + yO + ", " + l1 + ", " + l2 + ", " + angle + ", " + magnitude + ", " + cor;
	}
	
	public static void setDepth (double newDepth)
	{
		depth = newDepth;
	}
	
	// Determine coordinates of the points on screen
	public void point ()
	{
		xMid = xO + (-x * magnitude);
		yMid = yO - (-y * magnitude);
		xEnd1 = xO + (-x * magnitude + Math.cos(angle - Math.PI / 2) * l1);
		yEnd1 = yO - (-y * magnitude + Math.sin(angle - Math.PI / 2) * l1);
		xEnd2 = xO + (-x * magnitude + Math.cos(angle + Math.PI / 2) * l2);
		yEnd2 = yO - (-y * magnitude + Math.sin(angle + Math.PI / 2) * l2);
	}
	
	// Locate plane vector according to angle
	public void locate ()
	{
		//direction of plane vector is opposite to angle
		x = -Math.cos(angle);
		y = -Math.sin(angle);
		point();
	}
	
	// Rotate angle of plane around origin clockwise (rad/s)
	public void rotateCW (double speed)
	{
		angle -= speed / Universe.fpsCap;
		if (angle < 0)
			angle += 2 * Math.PI;
		locate();
	}
	
	// Rotate angle of plane around origin counterclockwise (rad/s)
	public void rotateCCW (double speed)
	{
		angle += speed / Universe.fpsCap;
		if (angle >= 360)
			angle -= 2 * Math.PI;
		locate();
	}
}
