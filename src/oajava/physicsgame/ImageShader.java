package oajava.physicsgame;

import oajava.util.gl.Shader;

public class ImageShader extends Shader {
	
	public static final String VERTEX_SOURCE = ""
			+ "#version 330 core\r\n"
			+ "\r\n"
			+ "layout (location = 0) in vec2 pos;\r\n"
			+ "\r\n"
			+ "out vec2 texturePos;\r\n"
			+ "\r\n"
			+ "void main() {\r\n"
			+ "	gl_Position = vec4(pos * 2 - vec2(1, 1), 0, 1);\r\n"
			+ "	texturePos = pos;\r\n"
			+ "}\r\n";
	
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
			+ "}";

	public static final ImageShader shader = new ImageShader();
	
	private ImageShader() {
		super (VERTEX_SOURCE, FRAGMENT_SOURCE);
	}
	
}
