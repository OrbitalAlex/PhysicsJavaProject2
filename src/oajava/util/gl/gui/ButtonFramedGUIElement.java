package oajava.util.gl.gui;

import oajava.util.Util.thrMethod;
import oajava.util.gl.Texture;

public class ButtonFramedGUIElement extends TextureFramedGUIElement {

	public thrMethod action;
	
	public ButtonFramedGUIElement(Texture tex) {
		super(tex);
	}
	
	@Override
	public void mouseClick(float x, float y) {
		if (action != null) action.run();
	}

}
