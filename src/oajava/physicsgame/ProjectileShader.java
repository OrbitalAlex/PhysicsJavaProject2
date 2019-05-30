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
			+ "\r\n"
			+ "out vec2 texturePos;\r\n"
			+ "\r\n"
			+ "void main() {\r\n"
			+ "	gl_Position = vec4((pos + vtx) * vec2(1, aspect), 0, 1);\r\n"
			+ "	texturePos = vtx + vec2(0.5, 0.5);"
			+ "}";
	
//	public static final String GEOMETRY_SOURCE = ""
//			+ "#version 330 core\r\n"
//			+ "\r\n"
//			+ "const float PI = 3.141592653589793238462643383279;\r\n"
//			+ "\r\n"
//			+ "layout (points) in;\r\n"
//			+ "layout (triangle_strip,max_vertices=6) out;\r\n"
//			+ "\r\n"
//			+ "uniform float angle;\r\n"
//			+ "uniform float aspect;\r\n"
//			+ "uniform vec2 size;\r\n"
//			+ "\r\n"
//			+ "out vec2 texturePos;\r\n"
//			+ "\r\n"
//			+ "vec2 generateVertex(vec2 in_vec, float ang) {\r\n"
//			+ "	float theAngle = angle + ang;"
//			+ "	float sinA = sin(theAngle);"
//			+ "	float cosA = cos(theAngle);"
//			+ "	return vec2(in_vec.x * cosA - in_vec.y * sinA, in_vec.y * cosA + in_vec.y * sinA) * vec2(1, aspect);\r\n"
//			+ "}\r\n"
//			+ "\r\n"
//			+ "void main() {\r\n"
//			+ "	texturePos = vec2(0, 1);\r\n"
//			+ "	gl_Position = vec4(generateVertex(gl_in[0].gl_Position.xy, 0), 0, 1);\r\n"
//			+ "	EmitVertex();\r\n"
//			+ "	texturePos = vec2(1, 1);\r\n"
//			+ "	gl_Position = vec4(generateVertex(gl_in[0].gl_Position.xy, PI/2), 0, 1);"
//			+ "	EmitVertex();\r\n"
//			+ "	texturePos = vec2(1, 0);"
//			+ "	gl_Position = vec4(generateVertex(gl_in[0].gl_Position.xy, PI), 0, 1);\r\n"
//			+ "	EmitVertex();\r\n"
//			+ "	EndPrimitive();\r\n"
//			+ "	texturePos = vec2(0, 0);"
//			+ "	gl_Position = vec4(generateVertex(gl_in[0].gl_Position.xy, 3 * PI / 2.0), 0, 1);\r\n"
//			+ "	EmitVertex();\r\n"
//			+ "	texturePos = vec2(1, 1);\r\n"
//			+ "	gl_Position = vec4(generateVertex(gl_in[0].gl_Position.xy, PI/2), 0, 1);\r\n"
//			+ "	EmitVertex();\r\n" 
//			+ "	texturePos = vec2(1, 0);\r\n"
//			+ " gl_Position = vec4(generateVertex(gl_in[0].gl_Position.xy, PI), 0, 1);\r\n"
//			+ "	EmitVertex();"
//			+ "	EndPrimitive();\r\n"
//			+ "	"
//			+ "}\r\n";
	
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
			+ "	gl_FragColor = vec4(0, 0, 0, 0.5);"
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
