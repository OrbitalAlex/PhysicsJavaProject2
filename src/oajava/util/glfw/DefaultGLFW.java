package oajava.util.glfw;

import static oajava.util.Util.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Stack;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import oajava.util.Util;

public class DefaultGLFW {
	
	public static final float FOV = (float) Math.toRadians(60);
	
	public static int fps = 30;
	public static int width = 1280, height = 720;
	public static float zNear = 0.001f, zFar = 3000f;
	
	public static boolean isMousePressed;
	public static int mouseX, mouseY;

	
	public static long window;
	
	public static String title = "Java Game";
	
	private static int[] keys = new int[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
	
	public static GameController game_controller = GameController.defaultGC;
	
	public static boolean SETTING_MOVE_CAMERA = false;
	public static float CAMERA_SPEED = 0.3f;
	
	private static int screenshot_thread = -1;

	public static Matrix4f projectionMatrix;

	public static Matrix4f viewMatrix;
	
	public static void setupScreenshotThread() {
		screenshot_thread = thrGenMultithreadHandler(MT_METHOD_QUEUE_THREAD);
		thrMTRun(screenshot_thread);
	}
	
	public static void runDefaultMainGameLoop() {
		if (!glfwInit()) {
			System.err.println("failed to initialize GLFW.");
			System.exit(1);
		}
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		window = glfwCreateWindow(width, height, title, 0, 0);
		glfwMakeContextCurrent(window);
		glfwSetWindowPos(window, glScreenWidth() / 2 - width / 2, glScreenHeight() / 2 - height / 2);
		glfwShowWindow(window);
		
		glfwSetMouseButtonCallback(window, (long window, int button, int action, int arg0) -> {
			isMousePressed = action == GLFW_PRESS;
			if (action == GLFW_PRESS) {
				game_controller.mousePress(button);
			} else {
				game_controller.mouseRelease(button);
			}
		});
		glfwSetCursorPosCallback(window, (long window, double x, double y) -> {
			if (game_controller.mouseMoved(mouseX, mouseY, (int) x, (int) y, mousePosf(), new Vector2f((float) (x / width), (float) (y / height)))) {
				mouseX = (int) x;
				mouseY = (int) y;
				return;
			}
			mouseX = (int) x;
			mouseY = (int) y;
		});
		glfwSetKeyCallback(window, (long window, int key, int arg1, int action, int arg2) -> {

			if (action == GLFW_PRESS) {
				game_controller.keyPressed(key);
				boolean b = true;
				for (int i = 0; i < keys.length && b; i ++) {
					if (keys[i] == -1) {
						keys[i] = key;
						b = false;
					}
				}
				
			} else if (action == GLFW_RELEASE) {
				for (int i = 0; i < keys.length; i ++) {
					if (keys[i] == key) {
						keys[i] = -1;
					}
				}
				game_controller.keyReleased(key);
			}
		});
		glfwSetScrollCallback(window, (long window, double scrollx, double scrolly) -> {
			game_controller.mouseScrolled(scrollx, scrolly);
		});
		
		
		glfwPollEvents();
		
		game_controller.preOpenGLInit();
		
		GL.createCapabilities();
		glColorMask(true, true, true, true);
		glDepthMask(true);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
			
		game_controller.postOpenGLInit();

		
		int glTimer = glGenTimer();
				
		int[] windowdim0 = new int[1], windowdim1 = new int[1];
		
		while (!glfwWindowShouldClose(window)) {			
			glfwGetWindowSize(window, windowdim0, windowdim1);
			width = windowdim0[0];
			height = windowdim1[0];
			
			glViewport(0, 0, width, height);
			glDepthMask(true);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glEnable(GL_DEPTH_TEST);
			glDisable(GL_BLEND);
			
			game_controller.preRender();
			
	        
			glViewport(0, 0, width, height);
	        
	        
	        game_controller.renderElements();
	        
	        glTimerSync(glTimer, fps);
	        
	        for (int key : keys) {
				if (key != -1) {
					game_controller.whileKeyPressed(key);
				}
			}
	        
	        game_controller.postRender();
			glfwSwapBuffers(window);
			glfwPollEvents();
		}
		
		game_controller.shutdown();
		glfwTerminate();
		thrShutdown();
	}
	
	
	
	
	public static void captureScreenshot() {
		int[] pixels = new int[width * height * 3];
		glPixelStorei(GL_PACK_ALIGNMENT, 1);
	    glReadBuffer(GL_BACK);
	    glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
	    

		if (screenshot_thread != -1) {
			thrMTEnqueue(screenshot_thread, jvmMethod(DefaultGLFW.class, "processScreenshotData", int[].class, int.class, int.class), pixels, width, height);
		} else {
			processScreenshotData(pixels, width, height);
		}
	}
	
	public static void processScreenshotData(int[] pixels, int width, int height) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		image.setRGB(0, 0, width, height, pixels, 0, width);
		
		BufferedImage prc = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		for (int x = 0; x < prc.getWidth(); x ++) {
			for (int y = 0; y < prc.getHeight(); y ++) {
				int px = image.getRGB(x, y);
				prc.setRGB(x, prc.getHeight() - y - 1, ((px & 0x00FF0000) >> 16) | (px & 0x0000FF00) | ((px & 0x000000FF) << 16));
			}
		}
		
		game_controller.saveScreenshot(prc);
		
		image.flush();
		prc.flush();
		pixels = null;
	}
	


	public static Vector2f mousePosf() {
		return new Vector2f((float) mouseX / width, (float) mouseY / height);
	}

	public static boolean isKeyDown(int key) {
		return GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS;
	}
	
}
