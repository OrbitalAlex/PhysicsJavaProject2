package oajava.physicsgame;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

import oajava.util.Util;
import oajava.util.gl.Shader;
import oajava.util.gl.Texture;
import oajava.util.glfw.DefaultGLFW;

public class HeartShader extends Shader {
	
	public static final float SIZE = 0.075f, SPACING = 0.015f;
	
	public static final Texture texture = new Texture(Util.glApplyTransparentColor(Util.glReadImage(HeartShader.class.getResourceAsStream("/assets/heart.png")), 0xFFFFFF));
	
	public static final String VTX_SOURCE = ""
			+ "#version 330 core\r\n"
			+ "\r\n"
			+ "layout (location=0) in vec2 quadPos;\r\n"
			+ "layout (location=1) in float offset;\r\n"
			+ "layout (location=2) in vec2 texturePosIn;\r\n"
			+ "\r\n"
			+ "out vec2 texturePos;\r\n"
			+ "\r\n"
			+ "uniform float aspect = 1;\r\n"
			+ "\r\n"
			+ "void main() {\r\n"
			+ "	texturePos = texturePosIn;\r\n"
			+ "	gl_Position = vec4(quadPos.x + offset, quadPos.y, 0, 1);\r\n"
			+ "}";
	
	public static final String FRAGMENT_SOURCE = ""
			+ "#version 330 core\r\n"
			+ "\r\n"
			+ "uniform sampler2D sampler;\r\n"
			+ "\r\n"
			+ "in vec2 texturePos;\r\n"
			+ "\r\n"
			+ "out vec4 gl_FragColor;\r\n"
			+ "\r\n"
			+ "void main() {\r\n"
			+ "	gl_FragColor = texture(sampler, texturePos);\r\n"
			+ "	if (gl_FragColor.x<190.0/255.0)discard;"
			+ "}";
	
	public static final HeartShader shader = new HeartShader();
	
	
	public final int vao_left, vao_right;
		
	private HeartShader() {
		super(VTX_SOURCE, FRAGMENT_SOURCE);
		
		final float aspect = DefaultGLFW.width*1f/DefaultGLFW.height;
		
		vao_left = GL30.glGenVertexArrays();
		vao_right = GL30.glGenVertexArrays();
		
		GL30.glBindVertexArray(vao_left);
			int vbo_left = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo_left);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, new float[] {
					-1f, 1f,
					-1f, 1f-SIZE*aspect,
					-1f+SIZE, 1f-SIZE*aspect,
					-1f+SIZE, 1f,
					0, SIZE+SPACING*1, SIZE*2+SPACING*2,
					0f, 0f,
					0f, 1f,
					1f, 1f,
					1f, 0f
			}, GL15.GL_STATIC_DRAW);
			GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
			GL33.glVertexAttribDivisor(0, 0);
			
			GL20.glVertexAttribPointer(1, 1, GL11.GL_FLOAT, false, 0, 32);
			GL33.glVertexAttribDivisor(1, 1);
			
			GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 0, 44);
			GL33.glVertexAttribDivisor(2, 0);
			
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);
			
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
		
		GL30.glBindVertexArray(vao_right);
			int vbo_right = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo_right);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, new float[] {
					1f, 1f,
					1f, 1f-SIZE*aspect,
					1f-SIZE, 1f-SIZE*aspect,
					1f-SIZE, 1f,
					0, -SIZE-SPACING*1,-SIZE*2-SPACING*2,
					0f, 0f,
					0f, 1f,
					1f, 1f,
					1f, 0f
			}, GL15.GL_STATIC_DRAW);
			GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
			GL33.glVertexAttribDivisor(0, 0);
			
			GL20.glVertexAttribPointer(1, 1, GL11.GL_FLOAT, false, 0, 32);
			GL33.glVertexAttribDivisor(1, 1);
			
			GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 0, 44);
			GL33.glVertexAttribDivisor(2, 0);
			
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);
			
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
		
	}
	

}
