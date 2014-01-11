// Development Testing

public class Test 
{
	public static void main (String[] args)
	{
		Atom ball1 = new Atom(20.0, 5.0, 10, 10, 0.6, 100, 0);
		Atom ball2 = new Atom(ball1);
		System.out.println(ball1);
		Atom.setGravity (-9.8);
		Atom.setCof (0.15);
		Atom.setDensity (0.1);
		
		System.out.println("distance: " + Universe.distance(0, 0, 100, 300));
		System.out.println("angleCoord: " + Universe.angleCoord(0, 0, -10, -10));
		System.out.println("angleVector: " + Universe.angleVector(-10, -10));
		System.out.println("netV: " + ball1.netV());
		System.out.println("isStationary: " + ball1.isStationary());
		
		ball1.applyForceCoord (10, 10, 100);
		ball1.applyForceAngle (0.75 * Math.PI, 100);
		ball1.applyForceVector (10, 10, 100);
		ball1.applyRepulsion (20, -20, 50, 100);
		ball1.applyAttraction (20, -20, 50, 100);
		System.out.println(ball1);
		
		ball1.applyNormalFriction();
		ball1.applyDrag();
		ball1.applyGravity(Math.PI * 3 / 2);
		ball1.react();
		System.out.println(ball1);
		ball1.integrate();
		System.out.println(ball1);
		
		Plane wall1 = new Plane (0, 0, 100, 100, 0.75 * Math.PI, 10, 0.9);
		Plane wall2 = new Plane (10, 100, 10, 0, 0.9);
		System.out.println(wall1);
		System.out.println(wall2);
		wall2.locate();
		wall2.point();
		System.out.println(wall2.xMid + ", " + wall2.yMid + ", " + 
				wall2.xEnd1 + ", " + wall2.yEnd1 + ", " + 
				wall2.xEnd2 + ", " + wall2.yEnd2);
		wall1.rotateCW(Math.PI);
		System.out.println(wall1);
		
		System.out.println(ball2.isReflect(wall2));
		ball2.reflect(wall2);
		System.out.println(ball2);
		
	}
}
