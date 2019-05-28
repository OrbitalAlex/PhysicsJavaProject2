package oajava.physicsgame;

import static oajava.util.Util.*;

import oajava.util.Util;
import oajava.util.gl.Texture;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import oajava.util.glfw.DefaultGLFW;

public class Terrain {

	public Vector2f tankPos0 = new Vector2f(-30f, -11f);
	public Vector2f tankPos1 = new Vector2f(30f, -25f);
	
	private static final int vao;
	public static final Texture texture = new Texture(Util.glReadImage(Terrain.class.getResourceAsStream("/assets/terrain.bmp"))); // TODO initialize the texture
	
	static {
		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
		
			int vbo = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, new float[] {-1f, -1f, -1f, 1f, 1f, 1f, 1f, -1f}, GL15.GL_STATIC_DRAW);
			GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
			
			GL20.glEnableVertexAttribArray(0);
			
		GL30.glBindVertexArray(0);
		
	}
	
	public void render() {
		TerrainShader.shader.bind();
		texture.bind(0);
		GL30.glBindVertexArray(vao);
		TerrainShader.shader.setAspectRatio(DefaultGLFW.width, DefaultGLFW.height, texture.getHeight(), texture.getWidth());
		GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);
		
	}

	public Tank[] generateTanks(int side) {
		return new Tank[] {
				new Tank(tankPos0, NET_CLIENT_SIDE),
				new Tank(tankPos1, NET_SERVER_SIDE)
		};
	}
	
}
