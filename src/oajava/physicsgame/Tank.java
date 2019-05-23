package oajava.physicsgame;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import oajava.util.Util;
import oajava.util.Util.Angle;
import oajava.util.gl.Texture;
import oajava.util.gl.gui.GUI;
import oajava.util.glfw.DefaultGLFW;

public class Tank {
	
	public static final Vector2f offset_cannon_wheel = new Vector2f(), projectileInitialPos = new Vector2f();
	
	
	public Vector2f pos;
	public Texture texture;
	public boolean isControlledByCurrentUser;
	public Angle angle;
	
	public void render() {
		texture.bind(0);
		GL30.glBindVertexArray(GUI.vao);
		
		TankShader.shader.bind();
		TankShader.shader.setAngleRad(0);
		TankShader.shader.setOffset(pos.mul(1f/PhysicsGame.ZOOM, new Vector2f()));
		TankShader.shader.setSize(new Vector2f(2, 1).mul(1f/PhysicsGame.ZOOM, new Vector2f()));
		TankShader.shader.setAspectRatio(DefaultGLFW.width, DefaultGLFW.height);
		
		
		GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);
		
		TankShader.shader.setAngleRad((float) angle.angRadians());
		TankShader.shader.setOffset(pos.add(offset_cannon_wheel, new Vector2f()).mul(1f/PhysicsGame.ZOOM, new Vector2f()));
	}
	
	public void launchProjectile() {
		
	}
	
}
