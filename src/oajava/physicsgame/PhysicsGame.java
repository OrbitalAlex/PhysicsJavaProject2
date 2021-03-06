package oajava.physicsgame;

import static oajava.util.Util.*;

import oajava.physicsgame.net.*;

import java.awt.Font;
import java.util.Stack;

import javax.swing.JOptionPane;

import org.lwjgl.glfw.GLFW;

import oajava.util.Util;
import oajava.util.Util.NetSocketDisconnected;
import oajava.util.gl.Texture;
import oajava.util.glfw.DefaultGLFW;


/*
 * This is a school project where we are making a video game to demonstrate kinematics things.
 * 
 */
public class PhysicsGame {

	public static float ZOOM = 33f;
	
	public static final Stack<Projectile> projectiles = new Stack<Projectile>();
	
	public static Tank[] tanks = new Tank[2];
	
	public static float time_seconds;
	public static long time0 = System.currentTimeMillis();
	
	public static Terrain terrain;
	
	public static int socket;
	private static Object lock = new Object();

	public static Heart heart = new Heart();
	
	public static int side;
	public static int turn = Util.NET_SERVER_SIDE;
	
	public static final Font font = new Font("Britannic Bold", Font.BOLD, 24);
	
	public static Texture angle_texture;
	
	private static class Callbacks implements NetSocketDisconnected {

		@Override
		public void netSocketDisconnected(int arg0) {
			System.exit(0);
		}
		
	}
	
	public static void main(String[] args) {	
		Util.ioOpenSystemThreadMonitors();
		
		DefaultGLFW.title = "Water Balloon Launching Game by Alexander B and Andrew M";
		DefaultGLFW.fps = 60;
		DefaultGLFW.game_controller = new ScreenIO();
		DefaultGLFW.setupScreenshotThread();
		DefaultGLFW.runDefaultMainGameLoop();
	}

	public static void loadNetworkIO() {
		terrain = new Terrain();
		
		int option = JOptionPane.showOptionDialog(null, "Server or Client?", "Server/Client", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[] {"Client", "Server"}, "Client");
		
		switch (option) {
		case 0:
			// CLIENT
			side = Util.NET_CLIENT_SIDE;
			String ip = JOptionPane.showInputDialog("IP of Server:");
			socket = Util.netGenSocket(Util.NET_CLIENT_SIDE, ip, 42046);
			netSetCallback0(socket, new Callbacks());
			netStartSocketHandler(socket);
			tanks = (Tank[]) Util.netAskPacket(socket, new PacketSynchronizeTanks());
			time0 = ((Long) Util.netAskPacket(socket, new PacketSyncronizeTime()));
			turn = NET_CLIENT_SIDE;
			break;
		case 1:
			// SERVER
			GLFW.glfwSetWindowTitle(DefaultGLFW.window, "Water Balloon Launching Game by Alexander B and Andrew M ["+Util.netLocalHost().getHostAddress()+"]");
			side = Util.NET_SERVER_SIDE;
//			turn = NET_CLIENT_SIDE;
			socket = Util.netGenSocket(Util.NET_SERVER_SIDE, null, 42046);
			netSetCallback1(socket, (arg0) -> {
				netSetCallback0(socket, new Callbacks());
				netStartSocketHandler(arg0);
				thrNotifyAll(lock);
				time0 = System.currentTimeMillis();
			});
			netStartSocketHandler(socket);
			tanks = terrain.generateTanks(Util.NET_SERVER_SIDE);
			JOptionPane.showMessageDialog(null, "Have a client connect to this address:\r\n"+Util.netLocalHost().getHostAddress());
			
			break;
		}
	}

	public static void removeProjectile(Projectile p) {
		projectiles.remove(p);
		Util.netSendPacket(socket, new PacketProjectileDestroyed(p.initialTime));
		// send packet?
	}
	
}
