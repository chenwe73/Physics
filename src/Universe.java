// Universal

public class Universe 
{
	public static double fpsCap = 125; // frame per second capacity (Hz)
	public static double fps = fpsCap; // frame per second (Hz)
	public static double mpp = 0.0003 * 50; // meter per pixel (m/p), actual = 0.0003 (m/p)
	
	// Distance from the (x1, y1) to (x2, y2), range = (+)
	public static double distance (double x1, double y1, double x2, double y2)
	{
		// c^2 = a^2 + b^2
		return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}
	
	// Angle of (xM, yM) relative to (xO, yO), range = 0 ~ 2PI (in radiance)
	public static double angleCoord (double xO, double yO, double xM, double yM)
	{
		// angle = tangent^-1 (opposite / adjacent)
		if (xO == xM && yO == yM) // same coordinates
			return 0;
		else if (xM >= xO && yM <= yO) // I quadrant
			return Math.atan(-(yM - yO) / (xM - xO));
		else if (xM >= xO && yM > yO) // IV quadrant
			return Math.atan(-(yM - yO) / (xM - xO)) + 2 * Math.PI;
		else // II/III quadrant
			return Math.atan(-(yM - yO) / (xM - xO)) + Math.PI;
	}
	
	// Angle of a vector, range = 0 ~ 2PI (in radiance)
	public static double angleVector (double xV, double yV)
	{
		return angleCoord(0, 0, xV, -yV);
	}
}
