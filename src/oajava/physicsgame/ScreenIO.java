package oajava.physicsgame;

import static oajava.util.Util.*;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;

import oajava.physicsgame.net.PacketNewTurn;
import oajava.util.Util;
import oajava.util.gl.Query;
import oajava.util.gl.Texture;
import oajava.util.gl.gui.GUI;
import oajava.util.glfw.DefaultGLFW;
import oajava.util.glfw.GameController;

public class ScreenIO implements GameController {

	public static Texture projectile_texture;
	public static int DummyProjectileVAOIDontCareAbout;
		
	public static Query query;
	
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
//		
		DummyProjectileVAOIDontCareAbout = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(DummyProjectileVAOIDontCareAbout);
		
		int buff = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buff);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, new float[] {-0.5f,-0.5f,-0.5f,0.5f,0.5f,0.5f,0.5f,-0.5f}, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
		
		GL20.glEnableVertexAttribArray(0);
		
		GL30.glBindVertexArray(0);
		
		projectile_texture = new Texture(glApplyTransparentColor(glReadImage(ScreenIO.class.getResourceAsStream("/assets/projectile.png")), 0x0000FF00));
		
		ioSetSyncingStatus(sync_frame, "Loading projectile shader...");
		ioSetSyncingProgressf(sync_frame, 0.1f);
		ProjectileShader.shader.getClass(); // trigger
	
		ioSetSyncingStatus(sync_frame, "Loading cannon shader and textures...");
		ioSetSyncingProgressf(sync_frame, 0.3f);
		Tank.touch(); // load textures
		
		ioHideSyncingFrame(sync_frame);
		ioDeleteSyncingFrame(sync_frame);
		
		PhysicsGame.loadNetworkIO();
		
		GLFW.glfwShowWindow(DefaultGLFW.window);
		
		query = new Query();
		
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
//		CLDoer.fbo.setViewport();
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		PhysicsGame.terrain.render();
//		renderProjectiles();
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		for (Tank t : PhysicsGame.tanks) {
			if (t != null) {
				t.render();
			}
		}
		
		if (PhysicsGame.angle_texture != null) PhysicsGame.angle_texture.free(); // i'm lazy
		
		HeartShader.shader.bind();
		HeartShader.texture.bind(0);
		GL30.glBindVertexArray(HeartShader.shader.vao_left);
		GL31.glDrawArraysInstanced(GL11.GL_QUADS, 0, 4, PhysicsGame.heart.heartCountp1);
		
		GL30.glBindVertexArray(HeartShader.shader.vao_right);
		GL31.glDrawArraysInstanced(GL11.GL_QUADS, 0, 4, PhysicsGame.heart.heartCountp2);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		
//		float texture_height = PhysicsGame.angle_texture.getHeight()/200f;
//		float texture_width = PhysicsGame.angle_texture.getWidth()/200f;
		
		renderProjectiles();
		PhysicsGame.angle_texture = Texture.glTexture("Angle: " + Util.ioClampi(((int) ((360 + (PhysicsGame.tanks[0].side == PhysicsGame.side ? PhysicsGame.tanks[0] : PhysicsGame.tanks[1]).angle.angDegrees()) * 100)) , 0, 36000) / 100f + "   Wind: " + Projectile.displayWindValue(), PhysicsGame.font, new Vector4f(1f, 1f, 1f, 1f), new Vector4f(0, 0, 0, 0.25f));
		PhysicsGame.angle_texture.bind(0);
		GUI.shader.bind();
		GUI.shader.setUniform(GUI.u_pos, new Vector2f());
		GUI.shader.setUniform(GUI.u_size, new Vector2f(PhysicsGame.angle_texture.getWidth()/600f,PhysicsGame.angle_texture.getHeight()/600f*DefaultGLFW.width*1f/DefaultGLFW.height));
		GUI.drawQuad();
		
//		CLDoer.fbo.resetViewport(CLDoer.texture.getWidth(), CLDoer.texture.getHeight());
		
//		GL30.glBindVertexArray(GUI.vao);
//		ImageShader.shader.bind();
//		CLDoer.texture.bind(0);
		
//		GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);
		
//		GL11.glFinish();
		
//		CLHost.bind_cl(CLDoer.cl_img);
//		CL10.clFinish(CLHost.command_queue);
		
//		int count = CLDoer.checkSchool();
		
//		CLHost.bind_gl(CLDoer.cl_img);
//		CL10.clFinish(CLHost.command_queue);
		
//		System.out.println("count: " + count);
//		
//		if (count > 0) {
//			System.out.println("remove!");
//			try {
//				PhysicsGame.removeProjectile(PhysicsGame.projectiles.get(0));
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
		
	}
	
	private void renderProjectiles() {
		ProjectileShader.shader.bind();
		projectile_texture.bind(0);
		ProjectileShader.shader.setAspectRatio(DefaultGLFW.width, DefaultGLFW.height);
		ProjectileShader.shader.setSize(new Vector2f(0.5f, 0.5f));
		GL30.glBindVertexArray(DummyProjectileVAOIDontCareAbout);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
			for (Projectile p : PhysicsGame.projectiles.toArray(new Projectile[] {})) {
				Vector2f pos;
				query.begin();
				ProjectileShader.shader.setAngle(p.getAngle());
				ProjectileShader.shader.setPosition(pos = p.getPosition(PhysicsGame.time_seconds).mul(1f/PhysicsGame.ZOOM));
				GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);
				query.end();
				GL11.glFinish();
				System.out.println("passed: " + query.get() + " " + pos.y);
				if (query.get() < 4000 && PhysicsGame.time_seconds - p.initialTime >= 0.333f && pos.y < 0.5f) {
					PhysicsGame.removeProjectile(p);
				}
//				GL11.glVertex2f(0, 0); // call the shader
			}
		
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
		System.out.println(PhysicsGame.turn + " " + PhysicsGame.side + " " + Util.NET_CLIENT_SIDE + " " + Util.NET_SERVER_SIDE);
		if (/*PhysicsGame.turn == PhysicsGame.side*/true == true) {
			for (Tank t : PhysicsGame.tanks) {
				if (t.side == PhysicsGame.side) {
					t.launchProjectile();
				}
			}
//			PhysicsGame.turn = PhysicsGame.side == Util.NET_CLIENT_SIDE ? Util.NET_SERVER_SIDE : Util.NET_CLIENT_SIDE;
//			Util.netSendPacket(PhysicsGame.socket, new PacketNewTurn(PhysicsGame.turn));
		}
	}


}
