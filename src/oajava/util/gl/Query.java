package oajava.util.gl;

import org.lwjgl.opengl.GL15;

public class Query {
	
	public int query;
	
	public Query() {
		query = GL15.glGenQueries();		
	}
	
	public void begin() {
		GL15.glBeginQuery(GL15.GL_SAMPLES_PASSED, query);
	}
	
	public void end() {
		GL15.glEndQuery(GL15.GL_SAMPLES_PASSED);
	}
	
	public int get() {
		return GL15.glGetQueryObjecti(query, GL15.GL_QUERY_RESULT);
	}

}
