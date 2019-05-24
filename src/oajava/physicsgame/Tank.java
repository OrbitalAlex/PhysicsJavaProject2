package oajava.physicsgame;

import java.io.Serializable;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import oajava.util.Util;
import oajava.util.Util.Angle;
import oajava.util.gl.Texture;
import oajava.util.gl.gui.GUI;
import oajava.util.glfw.DefaultGLFW;

public class Tank implements Serializable {
	
	private static final long serialVersionUID = 5089741754986302396L;

	public static final Vector2f offset_cannon_wheel = new Vector2f(), projectileInitialPos = new Vector2f();
	
	public static final Texture body = new Texture(Util.glReadImage(Tank.class.getResourceAsStream("/assets/tank_body.bmp")));
	public static final Texture wheel = new Texture(Util.glReadImage(Tank.class.getResourceAsStream("/assets/tank_wheel.png")));
	
	public Vector2f pos;
	public Angle angle;
	public int side;// NET_SERVER_SIDE or NET_CLIENT_SIDE
	public byte health = 5;
	
	public Tank(Vector2f pos, int side) {
		super();
		this.pos = pos;
		this.side = side;
	}

	public void render() {
		GL30.glBindVertexArray(GUI.vao);
		
		TankShader.shader.bind();
		TankShader.shader.setAngleRad(0);
		TankShader.shader.setOffset(pos.mul(1f/PhysicsGame.ZOOM, new Vector2f()));
		TankShader.shader.setSize(new Vector2f(1, 1).mul(1f/PhysicsGame.ZOOM, new Vector2f()));
		TankShader.shader.setAspectRatio(DefaultGLFW.width, DefaultGLFW.height);
		wheel.bind(0);
		
		GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);
		
		TankShader.shader.setAngleRad((float) angle.angRadians());
		TankShader.shader.setOffset(pos.add(offset_cannon_wheel, new Vector2f()).mul(1f/PhysicsGame.ZOOM, new Vector2f()));
		TankShader.shader.setSize(new Vector2f(2, 1).mul(1f/PhysicsGame.ZOOM, new Vector2f()));
		body.bind(0);
		
		GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);
	}
	
	public void launchProjectile() {
		
	}
	
	public void mouseMoved(Vector2f new_pos) {
		Vector2f diff = new_pos.sub(pos, new Vector2f());
		angle = Angle.angleRad((float) (Math.atan(diff.y / diff.x) + (diff.x < 0 ? Math.PI : 0)));
		
	}
	
	public static void touch() {} // load static textures
	
}
