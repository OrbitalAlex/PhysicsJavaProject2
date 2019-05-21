package oajava.util.gl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import oajava.util.gl.gui.GUI;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

public class Texture {
	
	public final int id;
	public final int width, height;
	
	private int active_tex;
	
	private static final Graphics fmsg = fmsg();
	
	private static Graphics fmsg() {
		try {
			Field f = GUI.class.getDeclaredField("fmsg");
			f.setAccessible(true);
			return ((Graphics) f.get(null));
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Texture(BufferedImage bi) {
		width = bi.getWidth();
		height = bi.getHeight();
		
		id = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, id);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		
		int[] pixels = new int[bi.getWidth() * bi.getHeight()];
		pixels = bi.getRGB(0, 0, bi.getWidth(), bi.getHeight(), pixels, 0, bi.getWidth());
		
			ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

			for (int i = 0; i < pixels.length; i++) {
				int pixel = pixels[i];
				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) (pixel & 0xFF));
				buffer.put((byte) ((pixel >> 24) & 0xFF));
			}

			buffer.flip();
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		
		glBindTexture(GL_TEXTURE_2D, 0);
		
	}
	
	public void generateMipmap() {
		bind();
		glGenerateMipmap(GL_TEXTURE_2D);
		unbind();
	}
	
	public Texture(int id, int w, int h) {
		this.id = id;
		this.width = w;
		this.height = h;
	}
	
	public void bind(int id) {
		active_tex = id;
		glActiveTexture(GL_TEXTURE0 + id);
		bind();
	}
	
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	public void unbind() {
		if (active_tex != -1) glActiveTexture(GL_TEXTURE0 + active_tex);
		glBindTexture(GL_TEXTURE_2D, 0);
		active_tex = -1;
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public static Texture glTexture(String s, Font f, Vector4f color, Vector4f background) {
		Rectangle2D bounds = fmsg.getFontMetrics(f).getStringBounds(s, fmsg);
		BufferedImage img = new BufferedImage((int) Math.ceil(bounds.getWidth())+4, (int) Math.ceil(bounds.getHeight())+4, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setPaintMode();
		g.setColor(new Color(background.x, background.y, background.z, background.w));
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		g.setFont(f);
		g.setColor(new Color(color.x, color.y, color.z, color.w));
		g.drawString(s, 2, (int) -bounds.getY());
		return new Texture(img);
	}

	public void free() {
		GL11.glDeleteTextures(id);
	}
	
}
