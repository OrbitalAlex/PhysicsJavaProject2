package oajava.physicsgame;

import org.joml.Vector2f;

import oajava.util.gl.Shader;

public class TerrainShader extends Shader {

	public static final String VERTEX_SOURCE = ""
			+ "#version 330 core\r\n"
			+ "\r\n"
			+ "layout (location=0) in vec2 pos;\r\n"
			+ "\r\n"
			+ "uniform float aspect;\r\n"
			+ "\r\n"
			+ "out vec2 texturePosition;\r\n"
			+ "\r\n"
			+ "void main() {\r\n"
			+ "	gl_Position = vec4(pos * vec2(1, aspect), 0, 1);\r\n"
			+ "	texturePosition = vec2(pos.x/2+0.5,1-(pos.y/2+0.5));\r\n"
			+ "}";
	
	public static final String FRAGMENT_SOURCE = ""
			+ "#version 330 core\r\n"
			+ "\r\n"
			+ "uniform sampler2D sampler;\r\n"
			+ "uniform sampler2D depth_sampler;\r\n"
			+ "\r\n"
			+ "in vec2 texturePosition;\r\n"
			+ "\r\n"
			+ "out vec4 gl_FragColor;\r\n"
			+ "layout (depth_greater) out float gl_FragDepth;\r\n"
			+ "\r\n"
			+ "void main() {\r\n"
			+ "	gl_FragColor = texture(sampler, texturePosition);\r\n"
			+ "	gl_FragDepth = texture(depth_sampler, texturePosition).x;\r\n"
			+ "}";
	
	public final int u_aspect;
	
	public static final TerrainShader shader = new TerrainShader();
	
	private TerrainShader() {
		super(VERTEX_SOURCE, FRAGMENT_SOURCE);
		
		u_aspect = getUniform("aspect");
		setUniform(getUniform("depth_sampler"), 1);
	}
	
	public void setAspectRatio(int w, int h, int i, int j) {
		setUniform(u_aspect, w*i*1f/h/j);
	}
	
}
