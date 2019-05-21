package oajava.util.glfw;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.joml.Vector2f;

public interface GameController {
	
	static GameController defaultGC = new GameController() {
		
		public void postOpenGLInit() {}
		public void preOpenGLInit() {}
		public void preRender() {}
		public void renderElements() {}
		public void postRender() {}
		public void shutdown() {}
		public void keyPressed(int glfw_key) {}
		public void keyReleased(int glfw_key) {}
		public void whileKeyPressed(int glfw_key) {}
		
	};
	
	public void postOpenGLInit();
	public void preOpenGLInit();
	public void preRender();
	public void renderElements();
	public void postRender();
	public void shutdown();
	public void keyPressed(int glfw_key);
	public void keyReleased(int glfw_key);
	public void whileKeyPressed(int glfw_key);
	public default void mousePress(int button) {};
	public default void mouseRelease(int button) {};
	public default boolean mouseMoved(int old_x, int old_y, int new_x, int new_y, Vector2f old, Vector2f newPos) {return false;};
	public default void mouseScrolled(double scrollx, double scrolly) {}

	
	public default void saveScreenshot(BufferedImage screenshot) {
		File folder = new File(System.getProperty("user.home")+File.separatorChar+"OAJava"+File.separatorChar+"Screenshots"+File.separatorChar+System.currentTimeMillis()+".png");
		folder.getParentFile().mkdirs();
		try {
			ImageIO.write(screenshot, "png", folder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
