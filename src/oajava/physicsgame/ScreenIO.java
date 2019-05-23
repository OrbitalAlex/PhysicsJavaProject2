package oajava.physicsgame;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import oajava.util.glfw.DefaultGLFW;
import oajava.util.glfw.GameController;

public class ScreenIO implements GameController {

	@Override
	public void postOpenGLInit() {
		GL11.glDisable(GL11.GL_DEPTH_TEST); // disable 3d stuff
		GL11.glDepthMask(false); // disable 3d stuff
		
		GL11.glClearColor(126/255f, 194/255f, 1f, 1f);
	}

	@Override
	public void preOpenGLInit() {
		DefaultGLFW.fps = 60;
	}

	@Override
	public void preRender() {
		
	}

	@Override
	public void renderElements() {
		PhysicsGame.terrain.render();
		renderProjectiles();
		
	}
	
	private void renderProjectiles() {
		ProjectileShader.shader.bind();
		ProjectileShader.shader.setAspectRatio(DefaultGLFW.width, DefaultGLFW.height);
		ProjectileShader.shader.setSize(new Vector2f(0.5f, 0.5f));
		GL30.glBindVertexArray(0);
		GL11.glBegin(GL11.GL_POINTS);
		
			for (Projectile p : PhysicsGame.projectiles) {
				ProjectileShader.shader.setAngle(p.getAngle());
				ProjectileShader.shader.setPosition(p.getPosition(PhysicsGame.time_seocnds));
				GL11.glVertex2f(0, 0); // call the shader
			}
		
		GL11.glEnd();
	}

	@Override
	public void postRender() {
		
	}

	@Override
	public void shutdown() {
		
	}

	@Override
	public void keyPressed(int glfw_key) {
		
	}

	@Override
	public void keyReleased(int glfw_key) {
		
	}

	@Override
	public void whileKeyPressed(int glfw_key) {
		
	}

}
