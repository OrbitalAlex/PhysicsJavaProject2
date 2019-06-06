package oajava.physicsgame;

import java.io.Serializable;

import org.joml.Vector2f;
import org.joml.Vector2fc;

import oajava.util.Util;

public class Projectile implements Serializable {

	private static final long serialVersionUID = 1499166575095122991L;

	public Vector2f initialPosition;//keep
	public Vector2f constantAcceleration;
	public Vector2f airResistance; // a = -bv/m = -cv (c=b/m = data stored in vector)
	public Vector2f velocity;//keep
	public float initialTime; 
	public int windValue = ((int)Math.random() * 20) - 10;
	public float AirResistancex;
	public float AirResistancey;
	public float gravity = 9.80665f;
	
	public Projectile(Vector2f initialPosition, Vector2f constantAcceleration, Vector2f airResistance,
			Vector2f velocity) {
		super();
		this.initialPosition = initialPosition;
		this.constantAcceleration = constantAcceleration;
		this.airResistance = airResistance;
		this.velocity = velocity;
		this.initialTime = PhysicsGame.time_seconds;
	}

	public float getAngle() 
	{
		return velocity.angle(new Vector2f(1, 0)); // DO THIS ANDREW!!!!!
	}

	public Vector2f getPosition(float time_seconds) 
	{
		//vector.set(x,y)
		//vector.x = 
		Vector2f position = new Vector2f();
		AirResistancex = (float) (0.2*windValue + 0.1*velocity.x); //Should go left .75 is quite random, need to test
		AirResistancey = (float) (0.1*velocity.y); //Should go up
		position.x = (float) (initialPosition.x + velocity.x * 0.01667f - AirResistancex * 0.01667f * 0.01667f); //Change in time set as 0.01667
		position.y = (float) (initialPosition.y + velocity.y * 0.01667f - (gravity - AirResistancey) * 0.01667f * 0.01667f);
		velocity.x -= AirResistancex * 0.01667f;
		velocity.y -= (gravity - AirResistancey) / 60f;
		initialPosition.x = position.x;
		initialPosition.y = position.y;
		if (position.distance(PhysicsGame.tanks[0].pos) <= 0.05f && PhysicsGame.side == Util.NET_CLIENT_SIDE)
		{
			PhysicsGame.removeProjectile(this);
			PhysicsGame.heart.removeHeartp1();
		}
		
		if (position.distance(PhysicsGame.tanks[1].pos) <= 0.05f && PhysicsGame.side == Util.NET_SERVER_SIDE)
		{
			PhysicsGame.removeProjectile(this);
			PhysicsGame.heart.removeheartp2();
		}
		
		
		
		//PhysicsGame.tanks[0].pos
		//PhysicsGame.removeProjectile(p);
		return position; // DO THIS ANDREW!!! maybe use Euler method to approximate velocity when using air resistance??? Just figure it out.
	}
	
}
