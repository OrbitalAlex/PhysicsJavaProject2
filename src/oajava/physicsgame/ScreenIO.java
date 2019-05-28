package oajava.physicsgame;

import static oajava.util.Util.*;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import oajava.physicsgame.net.PacketNewTurn;
import oajava.util.Util;
import oajava.util.gl.Texture;
import oajava.util.gl.gui.GUI;
import oajava.util.glfw.DefaultGLFW;
import oajava.util.glfw.GameController;

public class ScreenIO implements GameController {

	public static Texture projectile_texture;
		
	@Override
	public void postOpenGLInit() {
		GL11.glDisable(GL11.GL_DEPTH_TEST); // disable 3d stuff
		GL11.glDepthMask(false); // disable 3d stuff
		
		int sync_frame = Util.ioGenSyncingFrame();
		ioSetSyncingFrameName(sync_frame, "Loading Game");
		ioSetSyncingStatus(sync_frame, "Loading game...");
		ioSetSyncingProgressf(sync_frame, 0f);
		ioShowSyncingFrame(sync_frame);
		
//		GL11.glClearColor(126/255f, 194/255f, 1f, 1f);
		GL11.glClearColor(1f, 0f, 0f, 0f);
		
		
		projectile_texture = new Texture(glApplyTransparentColor(glReadImage(ScreenIO.class.getResourceAsStream("/assets/projectile.bmp")), 0x0000FF00));
		
		ioSetSyncingStatus(sync_frame, "Loading projectile shader...");
		ioSetSyncingProgressf(sync_frame, 0.1f);
		ProjectileShader.shader.getClass(); // trigger
	
		ioSetSyncingStatus(sync_frame, "Loading cannon shader and textures...");
		ioSetSyncingProgressf(sync_frame, 0.6f);
		Tank.touch(); // load textures
		
		ioHideSyncingFrame(sync_frame);
		ioDeleteSyncingFrame(sync_frame);
		
		PhysicsGame.loadNetworkIO();
		
		GLFW.glfwShowWindow(DefaultGLFW.window);
		
	}

	@Override
	public void preOpenGLInit() {
		DefaultGLFW.fps = 60;
		GLFW.glfwHideWindow(DefaultGLFW.window);
	}

	@Override
	public void preRender() {
		PhysicsGame.time_seconds = (System.currentTimeMillis() - PhysicsGame.time0)/1000f;
	}

	@Override
	public void renderElements() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		PhysicsGame.terrain.render();
//		renderProjectiles();
		for (Tank t : PhysicsGame.tanks) {
			if (t != null) {
				t.render();
			}
		}
		
		if (PhysicsGame.angle_texture != null) PhysicsGame.angle_texture.free(); // i'm lazy
		
//		float texture_height = PhysicsGame.angle_texture.getHeight()/200f;
//		float texture_width = PhysicsGame.angle_texture.getWidth()/200f;
		
		PhysicsGame.angle_texture = Texture.glTexture("Angle: " + ((int) ((360 + (PhysicsGame.tanks[0].side == PhysicsGame.side ? PhysicsGame.tanks[0] : PhysicsGame.tanks[1]).angle.angDegrees()) * 100)) / 100f, PhysicsGame.font, new Vector4f(1f, 1f, 1f, 1f), new Vector4f(0, 0, 0, 0.25f));
		PhysicsGame.angle_texture.bind(0);
		GUI.shader.bind();
		GUI.shader.setUniform(GUI.u_pos, new Vector2f());
		GUI.shader.setUniform(GUI.u_size, new Vector2f(PhysicsGame.angle_texture.getWidth()/600f,PhysicsGame.angle_texture.getHeight()/600f*DefaultGLFW.width*1f/DefaultGLFW.height));
		GUI.drawQuad();
	}
	
	private void renderProjectiles() {
		ProjectileShader.shader.bind();
		projectile_texture.bind(0);
		ProjectileShader.shader.setAspectRatio(DefaultGLFW.width, DefaultGLFW.height);
		ProjectileShader.shader.setSize(new Vector2f(0.5f, 0.5f));
		GL30.glBindVertexArray(0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_SRC_ALPHA);
		GL11.glBegin(GL11.GL_POINTS);
		
			for (Projectile p : PhysicsGame.projectiles) {
				ProjectileShader.shader.setAngle(p.getAngle());
				ProjectileShader.shader.setPosition(p.getPosition(PhysicsGame.time_seconds).mul(1f/PhysicsGame.ZOOM));
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
		if (glfw_key == GLFW.GLFW_KEY_SPACE) {
			mousePress(-1);
			return;
		}
	}

	@Override
	public void keyReleased(int glfw_key) {
		
	}

	@Override
	public void whileKeyPressed(int glfw_key) {
		
	}
	
	@Override
	public boolean mouseMoved(int old_x, int old_y, int new_x, int new_y, Vector2f old, Vector2f newPos) {
		newPos.mul(2).sub(1, 1);
		for (Tank t : PhysicsGame.tanks) {
			if (t != null) {
				if(t.side == PhysicsGame.side) {
					t.mouseMoved(newPos);
				}
			}
		}
		return true;
	}
	
	@Override
	public void mousePress(int button) {
		if (PhysicsGame.turn == PhysicsGame.side) {
			for (Tank t : PhysicsGame.tanks) {
				if (t.side == PhysicsGame.side) {
					t.launchProjectile();
				}
			}
			PhysicsGame.turn = PhysicsGame.side == Util.NET_CLIENT_SIDE ? Util.NET_SERVER_SIDE : Util.NET_CLIENT_SIDE;
			Util.netSendPacket(PhysicsGame.socket, new PacketNewTurn(PhysicsGame.turn));
		}
	}


}
