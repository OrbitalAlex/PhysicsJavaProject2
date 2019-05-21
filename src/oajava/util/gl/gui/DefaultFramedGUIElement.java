package oajava.util.gl.gui;

import org.joml.Vector2f;

public abstract class DefaultFramedGUIElement implements FramedGUIElement {

	public Vector2f pos = new Vector2f();
	public Vector2f size = new Vector2f();

	@Override
	public Vector2f pos() {
		return pos;
	}

	@Override
	public Vector2f size() {
		return size;
	}

}
