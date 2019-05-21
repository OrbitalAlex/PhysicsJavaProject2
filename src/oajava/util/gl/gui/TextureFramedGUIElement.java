package oajava.util.gl.gui;

import oajava.util.gl.Texture;

public class TextureFramedGUIElement extends DefaultFramedGUIElement {

	public final Texture texture;
	
	public boolean doResize = true;
	
	public TextureFramedGUIElement(Texture tex) {
		texture = tex;
	}
	
	@Override
	public void setGUI(FramedGUI gui) {
		if (doResize) {
			size.set(texture.width / (float) gui.pixel_width, texture.height / (float) gui.pixel_height);
		}
	}
	
	@Override
	public void render() {
		GUIShaders.gl_render_gui_element.drawElement(texture, size, pos);
	}

}
