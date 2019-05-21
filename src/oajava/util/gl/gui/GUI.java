package oajava.util.gl.gui;

import static java.awt.image.BufferedImage.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Iterator;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;

import oajava.util.gl.Shader;
import oajava.util.glfw.DefaultGLFW;

public class GUI {
	
	private static final String VERTEX_SOURCE = "#version 330 core\n\rlayout (location=0) in vec2 pos;\r\nuniform vec2 offset;\r\nuniform vec2 size;\r\nout vec2 pass_pos;\r\nvoid main() {gl_Position = vec4(pos.x * size.x + offset.x * 2 - 1, -pos.y * size.y - offset.y * 2 + 1, 0, 1);pass_pos = pos;}", FRAGMENT_SOURCE = "#version 330 core\r\nuniform sampler2D sampler;\r\nin vec2 pass_pos;out vec4 gl_FragColor;\r\nvoid main() {gl_FragColor = texture(sampler, pass_pos);}";
		
	public static final int vao, vbo, u_pos, u_size, u_sampler;
	public static final Shader shader = new Shader(VERTEX_SOURCE, FRAGMENT_SOURCE);
	
	private static final Graphics fmsg;
	
	static {
		vao = glGenVertexArrays();
		glBindVertexArray(vao);
		
		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, new float[] {0, 0, 1, 0, 1, 1, 0, 1}, GL_STATIC_DRAW);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
		
		glEnableVertexAttribArray(0);
		
		glBindVertexArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		u_pos = shader.getUniform("offset");
		u_size = shader.getUniform("size");
		u_sampler = shader.getUniform("sampler");
		
		fmsg = new BufferedImage(1, 1, TYPE_INT_RGB).getGraphics();
	}
	
	public final int pixel_height, pixel_width, texture;
	public float scale = 1;
	public boolean enableBlend = false;
//	public Vector2i offset = new Vector2i();
	public Vector2f offset = new Vector2f(), size = new Vector2f();
	
	private static int screenW, screenH;
	
	public GUI(int texture, int width, int height) {
		this.texture = texture;
		this.pixel_width = width;
		this.pixel_height = height;
		
		size.set((float) pixel_width / DefaultGLFW.width, (float) pixel_height / DefaultGLFW.height).mul(2f);
	}
	
	public GUI(BufferedImage bi) {
		int width = bi.getWidth();
		int height = bi.getHeight();
		
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

		for (int i = 0; i < width * height; i++) {
			int pixel = bi.getRGB(i % width, i / width);
			buffer.put((byte) ((pixel >> 16) & 0xFF));
			buffer.put((byte) ((pixel >> 8) & 0xFF));
			buffer.put((byte) (pixel & 0xFF));
			buffer.put((byte) ((pixel >> 24) & 0xFF));
		}

		buffer.flip();

		texture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texture);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		
		this.pixel_width = width;
		this.pixel_height = height;
		
		size.set((float) pixel_width / DefaultGLFW.width, (float) pixel_height / DefaultGLFW.height).mul(2f);
	}
	
	public static GUI textGUI(Font f, Color c, String text) {
		FontMetrics fms = fmsg.getFontMetrics(f);
		int y_off = fms.getAscent();
		Rectangle2D bounds = fms.getStringBounds(text, fmsg);
		BufferedImage bi = new BufferedImage((int) bounds.getWidth(), (int) bounds.getHeight(), TYPE_INT_ARGB);
		Graphics2D g2d = bi.createGraphics();
		g2d.setPaintMode();
		g2d.setColor(new Color(0, 0, 0, 0));
		g2d.fillRect(0, 0, bi.getWidth(), bi.getHeight());
		g2d.setColor(c);
		g2d.setFont(f);
		g2d.drawString(text, 0, y_off);
		g2d.dispose();
		return new GUI(bi);
	}
	
	public static void renderGUIs(Vector2i screen_size, boolean depthTest, GUI... guis) {
		preGlobalRender(screen_size);
		for (GUI g : guis) g.render();
		postGlobalRender(depthTest);
	}
	
	public static void renderGUIs(Vector2i screen_size, boolean depthTest, Iterable<GUI> guis) {
		preGlobalRender(screen_size);
		for (GUI g : guis) g.render();
		postGlobalRender(depthTest);
	}
	
	public static void preGlobalRender(Vector2i screen_size) {
		shader.bind();
		glDisable(GL_DEPTH_TEST);
		glDepthMask(false);
		glBindVertexArray(vao);
		screenW = screen_size.x;
		screenH = screen_size.y;
	}
	
	public void quickRenderSingle() {
		shader.bind();
		shader.setUniform(u_pos, new Vector2f((2f * offset.x - screenW) / screenW, (2f * offset.y - screenH) / screenH));
		shader.setUniform(u_size, new Vector2f((2f * pixel_width) / screenW, (2f * pixel_height) / screenH).mul(scale));
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texture);
		glBindVertexArray(vao);
		glDrawArrays(GL_QUADS, 0, 4);
		glBindVertexArray(0);
		shader.unbind();
	}
	
	public void render() {
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texture);
		shader.setUniform(u_sampler, 0);
		
//		shader.setUniform(u_pos, new Vector2f((2f * offset.x - screenW) / screenW, (2f * offset.y - screenH) / screenH));
//		shader.setUniform(u_size, new Vector2f((2f * pixel_width) / screenW, (2f * pixel_height) / screenH).mul(scale));
		shader.setUniform(u_pos, offset);
		shader.setUniform(u_size, size);
		
		if (enableBlend) {
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		}
		
		glDrawArrays(GL_QUADS, 0, 4);
		
		if (enableBlend) {
			glDisable(GL_BLEND);
		}
		
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public static void postGlobalRender(boolean depthTest) {
		if (depthTest) {
			glEnable(GL_DEPTH_TEST);
			glDepthMask(true);
		}
		glBindVertexArray(0);
	}

	public static void drawQuad() {
		glBindVertexArray(vao);
		glDrawArrays(GL_QUADS, 0, 4);
	}

	public void mouseClick(Vector2f pos) {
		
	}

	public void mouseRelease() {
		
	}
	
}
