package oajava.physicsgame;

import org.joml.Vector2f;

import oajava.util.gl.Shader;

public class TankShader extends Shader {

	public static final String VERTEX_SOURCE = ""
			+ "#version 330 core\r\n"
			+ "\r\n"
			+ "layout (location=0) in vec2 pos;\r\n"
			+ "\r\n"
			+ "uniform vec2 offset;\r\n"
			+ "uniform vec2 size;\r\n"
			+ "uniform float aspect;\r\n"
			+ "uniform float sin_angle;\r\n"
			+ "uniform float cos_angle;\r\n"
			+ "\r\n"
			+ "out vec2 texturePosition;\r\n"
			+ "\r\n"
			+ "void main() {\r\n"
			+ "	vec2 new_Pos = (pos + offset) * vec2(1, aspect);"
			+ "	gl_Position = vec4(new_Pos.x * cos_angle - new_Pos.y * sin_angle, new_Pos.y * cos_angle + new_Pos.y * sin_angle, 0, 1);\r\n"
			+ "	texturePosition = vec2(pos.x,1-pos.y);\r\n"
			+ "}";
	
	public static final String FRAGMENT_SOURCE = ""
			+ "#version 330 core\r\n"
			+ "\r\n"
			+ "uniform sampler2D sampler;\r\n"
			+ "\r\n"
			+ "in vec2 texturePosition;\r\n"
			+ "\r\n"
			+ "out vec4 gl_FragColor;\r\n"
			+ "\r\n"
			+ "void main() {\r\n"
			+ "	gl_FragColor = texture(sampler, texturePosition);\r\n"
			+ "}";
	
	public final int u_offset, u_size, u_aspect, u_sin_angle, u_cos_angle;
	
	public static final TankShader shader = new TankShader();
	
	private TankShader() {
		super(VERTEX_SOURCE, FRAGMENT_SOURCE);
		
		u_offset = getUniform("offset");
		u_size = getUniform("size");
		u_aspect = getUniform("aspect");
		u_sin_angle = getUniform("sin_angle");
		u_cos_angle = getUniform("cos_angle");
	}
	
	public void setOffset(Vector2f off) {
		setUniform(u_offset, off);
	}
	
	public void setSize(Vector2f size) {
		setUniform(u_size, size);
	}
	
	public void setAspectRatio(int w, int h) {
		setUniform(u_aspect, w*1f/h);
	}
	
	public void setAngleRad(float rad) {
		setUniform(u_sin_angle, (float) Math.sin(rad));
		setUniform(u_cos_angle, (float) Math.cos(rad));
	}
	
}
