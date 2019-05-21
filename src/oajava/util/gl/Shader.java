package oajava.util.gl;

import static oajava.util.Util.*;

import static org.lwjgl.opengl.GL20.*;

import java.util.Arrays;
import java.util.HashMap;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

public class Shader {

	private static int bound_program_id = 0;
	
	protected final int programId;
	protected int vertexShaderId;
	protected int geometryShaderId;
	protected int fragmentShaderId;

	public Shader() {
		programId = glCreateProgram();
		if (programId == 0) {
			throw new RuntimeException("Could not create Shader");
		}
	}

	public Shader(String vtx_source, String fg_source) {
		this();
		createVertexShader(vtx_source);
		createFragmentShader(fg_source);
		link();
	}
	
	public Shader(String vtx_source, String geom_source, String fg_source) {
		this();
		createVertexShader(vtx_source);
		createGeometryShader(geom_source);
		createFragmentShader(fg_source);
		link();
	}

	protected void createVertexShader(String shaderCode) {
		vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
	}

	protected void createGeometryShader(String shaderCode) {
		geometryShaderId = createShader(shaderCode, GL32.GL_GEOMETRY_SHADER);
	}
	
	protected void createFragmentShader(String shaderCode) {
		fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
	}

	protected int createShader(String shaderCode, int shaderType) {
		int shaderId = glCreateShader(shaderType);
		if (shaderId == 0) {
			utlThrowException("Error creating shader. Type: " + shaderType);
		}

		glShaderSource(shaderId, shaderCode);
		glCompileShader(shaderId);

		if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
			utlThrowException("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
		}

		glAttachShader(programId, shaderId);

		return shaderId;
	}

	public void link() {
		glLinkProgram(programId);
		if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
			utlThrowException("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
		}

		if (vertexShaderId != 0) {
			glDetachShader(programId, vertexShaderId);
		}
		if (geometryShaderId != 0) {
			glDetachShader(programId, geometryShaderId);
		}
		if (fragmentShaderId != 0) {
			glDetachShader(programId, fragmentShaderId);
		}

		glValidateProgram(programId);
		if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
			System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
		}
		
		bind();

	}
	
	public int getProgramId() {
		return programId;
	}

	public void bind() {
		if (bound_program_id == programId) return;
		bound_program_id = programId;
		glUseProgram(programId);
	}

	public void unbind() {
		bound_program_id = 0;
		glUseProgram(0);
	}

	public void cleanup() {
		unbind();
		if (programId != 0) {
			glDeleteProgram(programId);
		}
	}

	public int getUniform(String uniformName) {
		int uniformLocation = glGetUniformLocation(programId, uniformName);
		if (uniformLocation < 0) {
			System.err.println("couldn't find uniform " + uniformName);
		}
		return uniformLocation;
	}
	
	public void setUniform(int location, float value) {
		if (bound_program_id != programId) bind();
		if (location < 0) return;
		glUniform1f(location, value);
	}
	
	public void setUniform(int location, int value) {
		if (bound_program_id != programId) bind();
		if (location < 0) return;
		glUniform1i(location, value);
	}
	
	private static float[] fvcache2 = new float[2];
	public void setUniform(int location, Vector2f value) {
		if (bound_program_id != programId) bind();
		if (location < 0) return;
		fvcache2[0] = value.x;
		fvcache2[1] = value.y;
		glUniform2fv(location, fvcache2);
	}
	
	private static float[] fvcache3 = new float[3];
	public void setUniform(int location, Vector3f value) {
		if (bound_program_id != programId) bind();
		if (location < 0) return;
		fvcache3[0] = value.x;
		fvcache3[1] = value.y;
		fvcache3[2] = value.z;
		glUniform3fv(location, fvcache3);
	}
	
	private static float[] fvcache4 = new float[4];
	public void setUniform(int location, Vector4f value) {
		if (bound_program_id != programId) bind();
		if (location < 0) return;
		fvcache4[0] = value.x;
		fvcache4[1] = value.y;
		fvcache4[2] = value.z;
		fvcache4[3] = value.w;
		glUniform4fv(location, fvcache4);
	}
	
	private static float[] mat4data = new float[16];
	public void setUniform(int location, Matrix4f value) {
		if (bound_program_id != programId) bind();
		if (location < 0) return;
		value.get(mat4data);
		glUniformMatrix4fv(location, false, mat4data);
	}

}
