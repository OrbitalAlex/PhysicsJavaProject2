package oajava.physicsgame;

import java.util.Stack;

import oajava.util.glfw.DefaultGLFW;


/*
 * This is a school project where we are making a video game to demonstrate kinematics things.
 * 
 */
public class PhysicsGame {

	public static float ZOOM = 50f;
	
	public static final Stack<Projectile> projectiles = new Stack<Projectile>();
	
	public static float time_seocnds;
	
	public static void main(String[] args) {
		DefaultGLFW.title = "Water Balloon Launching Game by Alexander B and Andrew M";
		DefaultGLFW.game_controller = new ScreenIO();
		DefaultGLFW.setupScreenshotThread();
		DefaultGLFW.runDefaultMainGameLoop();
	}

}
