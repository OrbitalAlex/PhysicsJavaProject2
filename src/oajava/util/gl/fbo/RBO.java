package oajava.util.gl.fbo;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public class RBO {
	
	public final int rbo_id;
	
	public RBO(int format, int w, int h) {
		rbo_id = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, rbo_id);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, format, w, h);
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
	}
	
	public void bind() {
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, rbo_id);
	}
	
	public void unbind() {
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
	}
	
	

}
