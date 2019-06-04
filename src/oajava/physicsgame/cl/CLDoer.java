package oajava.physicsgame.cl;

import java.nio.IntBuffer;
import java.util.Arrays;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import oajava.physicsgame.cl.CLHost.clKernel;
import oajava.util.gl.Texture;
import oajava.util.gl.fbo.FBO;
import oajava.util.gl.fbo.RBO;
import oajava.util.glfw.DefaultGLFW;

public class CLDoer {

	public static FBO fbo;
	public static Texture texture;
	public static long cl_img, cl_buff, cl_buff2;
	
	public static FBO original;
	public static Texture textureOriginal;
	
	public static clKernel kernel;
	
	private static float[] debugData = new float[3 * (400 * (500 - 91))];
	
	public static void init() {
		CLHost.touch();
		
		int[] w = new int[1], h = new int[1];
		GLFW.glfwGetFramebufferSize(DefaultGLFW.window, w, h);
		
		long[] img = CLHost.createGLImage(w[0], h[0]);
		cl_img = img[0];
		
		System.out.println("cl images: " + img[0] + " " + img[1]);
		
		CLHost.bind_cl(cl_img);
		CLHost.bind_gl(cl_img);

		
		System.out.println("bound gl");
		
		fbo = new FBO(texture = new Texture((int) img[1], w[0], h[0]));
		
		int[] err = new int[1];
		cl_buff = CL10.clCreateBuffer(CLHost.context, CL10.CL_MEM_READ_WRITE | CL10.CL_MEM_COPY_HOST_PTR, new int[3], err);
		
		cl_buff2 = CL10.clCreateBuffer(CLHost.context, CL10.CL_MEM_READ_WRITE | CL10.CL_MEM_USE_HOST_PTR, debugData, err);
		System.out.println("buff: " + cl_buff + " " + cl_buff2 + " " + err[0]);
		
		
		kernel = new clKernel('f', 23582);
		
		CLHost.bind_cl(cl_img);
	}
	
	// check bounds: 450,91 850,500
	
	private static IntBuffer theBuffer = MemoryUtil.memAllocInt(3);
	
	public static int checkSchool() {
		
		CL10.clEnqueueWriteBuffer(CLHost.command_queue, cl_buff, true, 0L, new int[] {0,0,0}, null, null);
		
		CL10.clSetKernelArg1p(kernel.kernel, 0, cl_img);
		CL10.clSetKernelArg1p(kernel.kernel, 1, cl_buff);
		CL10.clSetKernelArg4f(kernel.kernel, 2, 49/255f, 101/255f, 187/255f, 0);
//		CL10.clSetKernelArg4f(kernel.kernel, 2, 0, 0, 0, 0);
		CL10.clSetKernelArg1i(kernel.kernel, 3, 400);
		CL10.clSetKernelArg1i(kernel.kernel, 4, 450);
		CL10.clSetKernelArg1i(kernel.kernel, 5, 91);
		CL10.clSetKernelArg1p(kernel.kernel, 6, cl_buff2);
		
		kernel.runkernel(400 * (500 - 91));
		
		int[] in = new int[3];
		
		CL10.clEnqueueReadBuffer(CLHost.command_queue, cl_buff, true, 0L, in, (PointerBuffer) null, (PointerBuffer) null);
		
		System.out.println("read: " + Arrays.toString(in));
		

		
		return in[0];
	}
	
}
