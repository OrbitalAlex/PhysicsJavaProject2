package oajava.physicsgame;

import java.awt.image.BufferedImage;
import java.io.Serializable;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import oajava.physicsgame.net.PacketProjectileCreated;
import oajava.util.Util;
import oajava.util.Util.Angle;
import oajava.util.Util.NetPacket;
import oajava.util.gl.Texture;
import oajava.util.gl.gui.GUI;
import oajava.util.glfw.DefaultGLFW;

public class Tank implements Serializable {
	
	private static final long serialVersionUID = 5089741754986302396L;

	public static final Vector2f offset_cannon_wheel = new Vector2f(0.5f, 0f), projectileInitialPos = new Vector2f();
	
	public static final Texture body = new Texture(Util.glReadImage(Tank.class.getResourceAsStream("/assets/tank_body.bmp")));
	public static final Texture wheel = new Texture(Util.glApplyTransparentColor(Util.glReadImage(Tank.class.getResourceAsStream("/assets/tank_wheel.png"), BufferedImage.TYPE_INT_ARGB), 0xFFFFFF));
	
	public Vector2f pos;
	public Angle angle = Angle.angleRad(0);
	public int side;// NET_SERVER_SIDE or NET_CLIENT_SIDE
	public byte health = 5;
	
	public Tank(Vector2f pos, int side) {
		super();
		this.pos = pos;
		this.side = side;
	}

	public void render() {
		GL30.glBindVertexArray(GUI.vao);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
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
		Angle angle = Util.Angle.angleDeg(this.angle.angDegrees()+180f);
		Projectile projectile = new Projectile(this.pos.add(0, 0, new Vector2f()), new Vector2f(0, -9.8f), new Vector2f(0.75f, 0.268f), new Vector2f((float) (75f*angle.cos()), ((float) (75f*angle.sin()))));
		PhysicsGame.projectiles.push(projectile);
		Util.netSendPacket(PhysicsGame.socket, new PacketProjectileCreated(projectile));
	}
	
	public void mouseMoved(Vector2f new_pos) {
		new_pos.mul(1f, DefaultGLFW.width*1f/DefaultGLFW.height);
		Vector2f diff = new_pos.sub(pos.mul(1f/PhysicsGame.ZOOM, new Vector2f()), new Vector2f());
		angle = Angle.angleRad(-(float) (Math.atan(diff.y / diff.x) + (diff.x < 0 ? Math.PI : 0)));
//		System.out.println("angle: " + angle.angDegrees());
	}
	
	public static void touch() {} // load static textures
	
}
