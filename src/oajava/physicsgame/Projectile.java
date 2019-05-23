package oajava.physicsgame;

import java.io.Serializable;

import org.joml.Vector2f;

public class Projectile implements Serializable {

	private static final long serialVersionUID = 1499166575095122991L;

	public Vector2f initialPosition;
	public Vector2f constantAcceleration;
	public Vector2f airResistance; // a = -bv/m = -cv (c=b/m = data stored in vector)
	public Vector2f velocity;
	public float initialTime;
	
	public Projectile(Vector2f initialPosition, Vector2f constantAcceleration, Vector2f airResistance,
			Vector2f velocity) {
		super();
		this.initialPosition = initialPosition;
		this.constantAcceleration = constantAcceleration;
		this.airResistance = airResistance;
		this.velocity = velocity;
		this.initialTime = PhysicsGame.time_seocnds;
	}
	
	public void move() {
		
	}

	public float getAngle() {
		return 0; // DO THIS ANDREW!!!!!
	}

	public Vector2f getPosition(float time_seocnds) {
		return null; // DO THIS ANDREW!!! maybe use Euler method to approximate velocity when using air resistance??? Just figure it out.
	}
	
}
