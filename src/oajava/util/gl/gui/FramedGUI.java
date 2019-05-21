package oajava.util.gl.gui;

import java.awt.Font;
import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import oajava.util.gl.Texture;
import oajava.util.gl.fbo.FBO;
import oajava.util.glfw.DefaultGLFW;

public class FramedGUI extends GUI {
	
	public String title;
	public FBO fbo;
	private ArrayList<FramedGUIElement> elements = new ArrayList<FramedGUIElement>();
	public boolean isDragging = false;
	private boolean draggable = true;
	public final TextureFramedGUIElement title_element;
	
	private FramedGUI(String title, int texture, int width, int height) {
		super(texture, width, height);
		this.fbo = new FBO(new Texture(texture, width, height));
		this.title = title;
		this.enableBlend = true;
		
		fbo.setViewport();
		GUIShaders.gl_clear_gui.clearFramedGUI(this);
		fbo.resetViewport(DefaultGLFW.width, DefaultGLFW.height);
		
		title_element = new TextureFramedGUIElement(Texture.glTexture(title, new Font("Bahnschrift SemiBold", Font.BOLD, 23), new Vector4f(194/255f, 143/255f, 0f, 1f), new Vector4f(125/255f, 125/255f, 125/255f, 125/255f)));
		addElement(title_element);
	}
	
	public static FramedGUI glGenFramedGUI(String title, int width, int height) {
		int tex = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (int[]) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		return new FramedGUI(title, tex, width, height);
	}
	
	public void addElement(FramedGUIElement e) {
		elements.add(e);
		e.setGUI(this);
		fbo.setViewport();
		e.render();
		fbo.resetViewport(DefaultGLFW.width, DefaultGLFW.height);
	}
	
	public void redrawAllElements() {
		GL11.glDisable(GL11.GL_BLEND);
		fbo.setViewport();
		GUIShaders.gl_clear_gui.clearFramedGUI(this);
		for (FramedGUIElement e : elements) {
			e.render();
		}
		fbo.resetViewport(DefaultGLFW.width, DefaultGLFW.height);
	}
	
	public float titleHeight() {
		return title_element.size.y * 2;
	}
	
	@Override
	public void mouseClick(Vector2f vec2) {
		if (vec2.y <= titleHeight()) {
			if (draggable) isDragging = true;
		}
	}
	
	@Override
	public void mouseRelease() {
		isDragging = false;
	}

	public float asFloat(int i) {
		return i / (float) pixel_width;
	}
	

}
