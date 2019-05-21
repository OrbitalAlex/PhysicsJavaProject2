package oajava.util.gl.fbo;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import oajava.util.gl.Texture;

public class FBO {

	public final int fbo_id;
	public final Texture texture;
	
	public FBO(Texture texture) {
		this.texture = texture;
		fbo_id = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo_id);
		
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, texture.id, 0);
		GL20.glDrawBuffers(new int[] {GL30.GL_COLOR_ATTACHMENT0});
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	
	public void setViewport() {		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo_id);
		if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
			System.err.println("frame buffer not ready");
		}
		GL20.glDrawBuffers(GL30.GL_COLOR_ATTACHMENT0);
		GL11.glViewport(0, 0, texture.width, texture.height);
	}
	
	public void resetViewport(int w, int h) {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, w, h);
	}
	
	public void example() {
		
		int frameBuffer = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
		
		int tex = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 1024, 768, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (int[]) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, tex, 0);
		GL20.glDrawBuffers(GL30.GL_COLOR_ATTACHMENT0);
		
	}
	
}
