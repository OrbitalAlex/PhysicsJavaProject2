package oajava.util.gl.gui;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import oajava.util.gl.Texture;
import oajava.util.gl.Shader;

public class GUIShaders {

	public static class ClearGUIShader extends Shader {
		
		private final int u_color;
		
		private ClearGUIShader() {
			super(
					// vertex shader
					  "#version 330 core\r\n"
					+ ""
					+ "in vec2 pos;"
					+ ""
					+ "void main() {"
					+ "	gl_Position = vec4(pos * 2 - 1, 0, 1);"
					+ "}",
					
					// fragment shader
					
					  "#version 330 core\r\n"
					+ ""
					+ "uniform vec4 color;\r\n"
					+ ""
					+ "layout (location = 0) out vec4 gl_FragColor;\r\n"
					+ ""
					+ "void main() {"
					+ "	gl_FragColor = color;"
					+ "}");
			
			u_color = getUniform("color");
			
			setUniform(u_color, new Vector4f(125/255f, 125/255f, 125/255f, 125/255f));
		}
		
		
		public void clearFramedGUI(FramedGUI gui) {
			bind();
			GUI.drawQuad();
		}
		
	}
	
	public static class RenderGUIElementShader extends Shader {
		
		private final int u_size, u_offset, u_sampler;
		
		private RenderGUIElementShader() {
			super(
					// vertex shader
					  "#version 330 core\r\n"
					+ ""
					+ "in vec2 pos;\r\n"
					+ ""
					+ "out vec2 img_pos;\r\n"
					+ ""
					+ "uniform vec2 size, offset;\r\n"
					+ ""
					+ "void main() {"
					+ "	gl_Position = vec4((pos * 2 * size + offset - 1), 0, 1);"
					+ "	img_pos = pos;"
					+ "}",
					
					// fragment shader
					
					""
					+ "#version 330 core\r\n"
					+ ""
					+ "uniform sampler2D sampler;\r\n"
					+ ""
					+ "layout (location = 0) out vec4 gl_FragColor;\r\n"
					+ ""
					+ "in vec2 img_pos;\r\n"
					+ ""
					+ "void main() {"
					+ "	gl_FragColor = texture(sampler, img_pos);"
					+ "}");
			
			u_size = getUniform("size");
			u_offset = getUniform("offset");
			u_sampler = getUniform("sampler");
			
			setUniform(u_sampler, 0);
		}
		
		public void drawElement(Texture texture, Vector2f size, Vector2f offset) {
			bind();
			GL11.glDisable(GL11.GL_BLEND);
			texture.bind(0);
			
			setUniform(u_size, size);
			setUniform(u_offset, offset);
			
			GUI.drawQuad();
			
			texture.unbind();
		}
		
	}
	
	public static final ClearGUIShader gl_clear_gui = new ClearGUIShader();
	public static final RenderGUIElementShader gl_render_gui_element = new RenderGUIElementShader();
	
	
}
