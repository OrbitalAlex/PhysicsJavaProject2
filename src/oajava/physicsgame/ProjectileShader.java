package oajava.physicsgame;

import org.joml.Vector2f;

import oajava.util.gl.Shader;

public class ProjectileShader extends Shader {

	public final int u_pos, u_angle, u_aspect, u_size;
	
	public static final String VERTEX_SOURCE = ""
			+ "#version 330 core\r\n"
			+ "\r\n"
			+ "layout (location=0) in vec2 vtx;\r\n"
			+ "\r\n"
			+ "in vec2 someRandomData;\r\n"
			+ "\r\n"
			+ "uniform vec2 pos;\r\n"
			+ "uniform float aspect;\r\n"
			+ "uniform float angle;\r\n"
			+ "\r\n"
			+ "out vec2 texturePos;\r\n"
			+ "\r\n"
			+ "void main() {\r\n"
			+ "	gl_Position = vec4(vec2((vtx.x * cos(angle) - vtx.y * sin(angle)) * .1 + pos.x, (vtx.y * cos(angle) + vtx.x * sin(angle)) * .1 + pos.y) * vec2(1, aspect), 0.5, 1);\r\n"
			+ "	texturePos = vtx + vec2(0.5, 0.5);"
			+ "}";
	
	public static final String FRAGMENT_SOURCE = ""
			+ "#version 330 core\r\n"
			+ "\r\n"
			+ "uniform sampler2D sampler;\r\n"
			+ "\r\n"
			+ "in vec2 texturePos;\r\n"
			+ "\r\n"
			+ "out vec4 gl_FragColor;\r\n"
			+ ""
			+ "\r\n"
			+ "void main() {\r\n"
			+ "	gl_FragColor = texture(sampler, texturePos);\r\n"
			+ "}\r\n";
	
	public static final ProjectileShader shader = new ProjectileShader();
	
	private ProjectileShader() {
		super(VERTEX_SOURCE, FRAGMENT_SOURCE);
		
		u_pos = getUniform("pos");
		u_angle = getUniform("angle");
		u_aspect = getUniform("aspect");
		u_size = getUniform("size");
		
	}
	
	public void setAspectRatio(int w, int h) {
		setUniform(u_aspect, w * 1f/h);
	}
	
	public void setAngle(float angle) {
		setUniform(u_angle, angle);
	}
	
	public void setSize(Vector2f size) {
		setUniform(u_size, size);
	}
	
	public void setPosition(Vector2f pos) {
		setUniform(u_pos, pos);
	}
	
}
