package oajava.util.gl.gui;

import org.joml.Vector2f;

public interface FramedGUIElement {
	
	public void render();
	public default void mouseClick(float x, float y) {}
	public default void keyPress(int key) {};
	public default void keyRelease(int key) {};
	public default void setGUI(FramedGUI gui) {};
	public Vector2f pos();
	public Vector2f size();
	
}
