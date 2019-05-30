package oajava.physicsgame;

import java.io.Serializable;

import org.joml.Vector2f;

public class Projectile implements Serializable {

	private static final long serialVersionUID = 1499166575095122991L;

	public Vector2f initialPosition;//keep
	public Vector2f constantAcceleration;
	public Vector2f airResistance; // a = -bv/m = -cv (c=b/m = data stored in vector)
	public Vector2f velocity;//keep
	public float initialTime;
	public int windValue = (int)Math.random() * 10 + 1;
	public float velocityy;
	public float velocityx;
	public float AirResistancex;
	public float AirResistancey;
	public float gravity = (float) 9.80665;
	
	public Projectile(Vector2f initialPosition, Vector2f constantAcceleration, Vector2f airResistance,
			Vector2f velocity) {
		super();
		this.initialPosition = initialPosition;
		this.constantAcceleration = constantAcceleration;
		this.airResistance = airResistance;
		this.velocity = velocity;
		this.initialTime = PhysicsGame.time_seconds;
	}
	
	//public void move() 
	//{
		
		
	//}

	public float getAngle() 
	{
		return velocity.angle(new Vector2f(1, 0)); // DO THIS ANDREW!!!!!
	}

	public Vector2f getPosition(float time_seconds) 
	{
		//vector.set(x,y)
		//vector.x = 
		Vector2f position = new Vector2f();
		AirResistancex = (float) (0.75*windValue + 0.75*velocityx); //Should go left .75 is quite random, need to test
		AirResistancey = (float) (0.75*velocityy); //Should go up
		position.x = (float) (initialPosition.x + velocityx * 0.01667 - AirResistancex * 0.01667 * 0.01667); //Change in time set as 0.01667
		position.y = (float) (initialPosition.y + velocityy * 0.01667 - (gravity - AirResistancey) * 0.01667 * 0.01667);
		initialPosition.x = position.x;
		initialPosition.y = position.y;
		return position; // DO THIS ANDREW!!! maybe use Euler method to approximate velocity when using air resistance??? Just figure it out.
	}
	
}
