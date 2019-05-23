package oajava.physicsgame;

import java.util.Stack;

import javax.swing.JOptionPane;

import oajava.util.Util;
import oajava.util.glfw.DefaultGLFW;


/*
 * This is a school project where we are making a video game to demonstrate kinematics things.
 * 
 */
public class PhysicsGame {

	public static float ZOOM = 50f;
	
	public static final Stack<Projectile> projectiles = new Stack<Projectile>();
	
	public static Tank[] tanks = new Tank[2];
	
	public static float time_seocnds;
	
	public static Terrain terrain;
	
	public static int socket;
	private static Object lock = new Object();
	
	public static void main(String[] args) {
		int option = JOptionPane.showOptionDialog(null, "Server or Client?", "Server/Client", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[] {"Client", "Server"}, "Client");
		
		switch (option) {
		case 0:
			// CLIENT
			String ip = JOptionPane.showInputDialog("IP of Server:");
			socket = Util.netGenSocket(Util.NET_CLIENT_SIDE, ip, 42046);
			break;
		case 1:
			// SERVER
			socket = Util.netGenSocket(Util.NET_SERVER_SIDE, null, 42046);
			Util.netSetCallback1(socket, (arg0) -> {
				Util.netStartSocketHandler(arg0);
				Util.thrNotifyAll(lock);
			});
			Util.netStartSocketHandler(socket);
			JOptionPane.showMessageDialog(null, "Have a client connect to this address:\r\n"+Util.netLocalHost().getHostAddress());
			
			Util.thrWait(lock);
			
			tanks = terrain.generateTanks(Util.NET_SERVER_SIDE);
			
			break;
		}
		
		
		
		DefaultGLFW.title = "Water Balloon Launching Game by Alexander B and Andrew M";
		DefaultGLFW.game_controller = new ScreenIO();
		DefaultGLFW.setupScreenshotThread();
		DefaultGLFW.runDefaultMainGameLoop();
	}

}
