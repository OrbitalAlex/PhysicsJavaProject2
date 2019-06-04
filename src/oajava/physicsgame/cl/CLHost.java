package oajava.physicsgame.cl;

import static oajava.util.Util.ioPrintStackTrace;
import static oajava.util.Util.ioReadInputStream;
import static oajava.util.Util.ioStaticb;
import static oajava.util.Util.thrNotifyAll;
import static oajava.util.Util.thrStacktrace;
import static oajava.util.Util.thrWait;
import static org.lwjgl.opencl.CL10.CL_DEVICE_MAX_CLOCK_FREQUENCY;
import static org.lwjgl.opencl.CL10.CL_DEVICE_MAX_COMPUTE_UNITS;
import static org.lwjgl.opencl.CL10.CL_MEM_READ_WRITE;
import static org.lwjgl.opencl.CL10.CL_SUCCESS;
import static org.lwjgl.opencl.CL10.clBuildProgram;
import static org.lwjgl.opencl.CL10.clCreateCommandQueue;
import static org.lwjgl.opencl.CL10.clCreateKernel;
import static org.lwjgl.opencl.CL10.clCreateProgramWithSource;
import static org.lwjgl.opencl.CL10.clGetDeviceInfo;
import static org.lwjgl.opencl.CL10.clGetPlatformIDs;
import static org.lwjgl.opencl.CL10.nclEnqueueNDRangeKernel;
import static org.lwjgl.opencl.CL12GL.clCreateFromGLTexture;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CL10GL;
import org.lwjgl.opencl.CLCapabilities;
import org.lwjgl.opencl.KHRGLSharing;
import org.lwjgl.opengl.WGL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import oajava.util.Util;

public class CLHost {
	
	public static final long platform, program;
	
	public static final long sharegroup = 0;
	
	public static final long context;
	public static final long command_queue;
	
	public static final long device;
	public static final int max_compute_units, max_clock_frequency;
	
	private static MemoryStack stack;
	private static PointerBuffer properties;
	
	public static final String SOURCE = ""
			+ "#pragma OPENCL : EXTENSION cl_khr_fp64 : disable\r\n"
			+ "\r\n"
			+ "const sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE | CLK_ADDRESS_REPEAT | CLK_FILTER_NEAREST;\r\n"
			+ "\r\n"
			+ "float theAbs(float f) {\r\n"
			+ "	if (f < 0) return -f;\r\n"
			+ "	return f;\r\n"
			+ "}\r\n"
			+ "\r\n"
			+ "kernel void func_23582(\r\n"
			+ "image2d_t gl_img,\r\n"
			+ "__global int* theBuff,\r\n"
			+ "float4 color,\r\n"
			+ "int width,\r\n"
			+ "int offx,\r\n"
			+ "int offy,\r\n"
			+ "__global float* debug_buffer"
			+ ") {\r\n"
			+ "	//theBuff[1] += 1;\r\n"
			+ "	int id = get_global_id(0);\r\n"
			+ "	int x = get_global_id(0) % width;\r\n"
			+ "	int y = get_global_id(0) / width;\r\n"
			+ "	int2 pos = (int2) (x+offx,y+offy);\r\n"
			+ "	float4 pixel = read_imagef(gl_img, sampler, pos);\r\n"
			+ "	if (theAbs(pixel.x-color.x) < 0.01 && theAbs(pixel.y-color.y) < 0.01 && theAbs(pixel.z-color.z) < 0.01) {\r\n"
			+ "		theBuff[0] +=1 ;\r\n"
			+ "		debug_buffer[id * 3] = pixel.x;\r\n"
			+ "		debug_buffer[id * 3 + 1] = pixel.y;\r\n"
			+ "		debug_buffer[id * 3 + 2] = pixel.z;\r\n"
			+ "	}\r\n"
			+ "	//theBuff[2] += 1;\r\n"
			+ "}\r\n";
			
//	public static final boolean forceCopyPointer = ioStaticb(Kernels.class, "forceCopyPointers", OrbitSim.program, false);
	
	
	
	static {
		
		stack = MemoryStack.stackPush();
		
		System.out.println("generating platform");
		platform = platform();
		
		System.out.println("generating device");
		device = device();
		
		System.out.println("getting the max compute units");
		max_compute_units = max_compute_units();
		
		System.out.println("generating context");
		context = context();
		
		System.out.println("generating command queue");
		command_queue = command_queue();
					
		program = program();
//		if (Kernels.loadProgramBinaries && !cl_binary_file(0).exists()) Kernels.saveBinaries();
		
		max_clock_frequency = max_clock_frequency();
		
//		osEnqueueMethod((Method) jvmMethod(CLHost.class, "print_device_info"), (Object) null);
					
	}
	
//	private static long program_bl() {
//		File file = cl_binary_file(0);
//		byte[] data = new byte[(int) file.length()];
//		int len = 0;
//		try {
//			FileInputStream fis = new FileInputStream(file);
//			len = fis.read(data);
//			fis.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		ByteBuffer bb = MemoryUtil.memAlloc(len);
//		bb.put(data, 0, len);
//		bb.flip();
//		long program;
//		try (MemoryStack stack = MemoryStack.stackPush()) {
//			PointerBuffer device_list = stack.callocPointer(1);
//			IntBuffer binary_status = stack.callocInt(1), errcode_ret = stack.callocInt(1);
//			device_list.put(device).flip();
//			binary_status.put(CL_SUCCESS).flip();
//			program = CL10.clCreateProgramWithBinary(CLHost.context, device_list, bb, binary_status, errcode_ret);
//			System.out.println("program: " + program);
//			if (errcode_ret.get(0) != CL_SUCCESS) {
//				System.err.println("error loading binaries: " + errcode_ret.get(0));
//				System.exit(errcode_ret.get(0));
//			}
//		}
//		data = null;
//		MemoryUtil.memFree(bb);
//		return program;
//	}
//	
	public static void checkerr(int[] err) {
		if (err[0] != 0) Util.utlThrowException("CL Error " + err[0]);
	}
	
	public static void checkerr(int err, String id) {
		if (err != 0) Util.utlThrowException("CL Error " + err + " (" + id +")");
	}
	
	private static long program() {
//		if (Kernels.loadProgramBinaries && cl_binary_file(0).exists()) return program_bl();
		IntBuffer errors = MemoryUtil.memAllocInt(1);
//		String str = ioReadInputStream(cl_nv_mode ? Kernels.class.getResourceAsStream("/res_type_78438/nv_converter/converted.nv_cl") : Kernels.class.getResourceAsStream("/res_type_78438/res-all.cl"));
		String str = SOURCE;
		long program = clCreateProgramWithSource(context, str, errors);
		/*checkerr(new int[] {*/clBuildProgram(program, device, "", null, NULL);/*});*/
		IntBuffer prg_info = MemoryUtil.memAllocInt(1);
		CL10.clGetProgramBuildInfo(program, device, CL10.CL_PROGRAM_BUILD_STATUS, prg_info, (PointerBuffer) null);
		System.out.println("Build Status: " +prg_info.get(0));
		if (errors.get(0) != 0 || prg_info.get(0) != 0) {
			System.out.println("error building CL program " + errors.get(0) + " " + prg_info.get(0));
			PointerBuffer pb = MemoryUtil.memAllocPointer(1);
			CL10.clGetProgramBuildInfo(program, device, CL10.CL_PROGRAM_BUILD_LOG, (ByteBuffer) null, pb);
			ByteBuffer bb = MemoryUtil.memAlloc((int) pb.get(0));
			CL10.clGetProgramBuildInfo(program, device, CL10.CL_PROGRAM_BUILD_LOG, bb, pb);
			byte[] data = new byte[bb.capacity()];
			bb.get(data);
			System.err.println("Build Log:\r\n\""+new String(data)+"\"");
			System.exit(0);
			// testProgram(str)
		}
		System.out.println("program: " + program);
		return program;
	}
	
	private static long platform() {
		PointerBuffer buffer = stack.mallocPointer(1);
		int[] platformcount = new int[1];
		clGetPlatformIDs(buffer, platformcount);
		long platform = buffer.get(0);
//		properties.put(5, platform);
		return platform;
	}
	
	private static long device() {
//		 cl_context_properties properties[] = { 
//				 !! CL_GL_CONTEXT_KHR,   (cl_context_properties)wglGetCurrentContext(), 
//				 // WGL Context !! CL_WGL_HDC_KHR,      (cl_context_properties)wglGetCurrentDC(),      
//				 // WGL HDC    CL_CONTEXT_PLATFORM, 
//				 (cl_context_properties)platform,               // OpenCL platform
//				 0 !}; 
//		 }
		
		properties = stack.mallocPointer(7);
		properties.put(KHRGLSharing.CL_GL_CONTEXT_KHR);
		properties.put(WGL.wglGetCurrentContext());
		properties.put(KHRGLSharing.CL_WGL_HDC_KHR);
		properties.put(WGL.wglGetCurrentDC());
		properties.put(CL10.CL_CONTEXT_PLATFORM);
		properties.put(platform);
		properties.put(0);
		properties.flip();
		
		PointerBuffer size = stack.mallocPointer(1);
		int err = KHRGLSharing.clGetGLContextInfoKHR(properties, KHRGLSharing.CL_DEVICES_FOR_GL_CONTEXT_KHR, (PointerBuffer) null, size);
		PointerBuffer devices = stack.mallocPointer((int) size.get(0) / 8);
		err = KHRGLSharing.clGetGLContextInfoKHR(properties, KHRGLSharing.CL_DEVICES_FOR_GL_CONTEXT_KHR, devices, size);
		if (err != 0) System.err.println("Error with device: " + err + " " + devices.get(0));
		
		System.out.println("device len: " + devices.limit());
		
		long[] devicel = new long[devices.limit()];
		devices.get(devicel);
		int dev_id = 0;
		System.out.println(Arrays.toString(devicel));
		
		
		int ind = 0;
		
		for (long device : devicel) {
			String extensions;
			
			System.out.println("DEVICE " + dev_id++ + " " + device);
			System.out.println("\tmax_clock_frequency: " + device_parami(device, CL10.CL_DEVICE_MAX_CLOCK_FREQUENCY));
			System.out.println("\tmax_compute_units: " + device_parami(device, CL10.CL_DEVICE_MAX_COMPUTE_UNITS));
			System.out.println("\tmax_work_item_sizes: " + device_parami(device, CL10.CL_DEVICE_MAX_WORK_ITEM_SIZES));
			System.out.println("\tglobal_mem_size: " + device_parami(device, CL10.CL_DEVICE_GLOBAL_MEM_SIZE));
			System.out.println("\tmax_samplers: " + device_parami(device, CL10.CL_DEVICE_MAX_SAMPLERS));
			System.out.println("\tvendor: " + new String(device_parambl(device, CL10.CL_DEVICE_VENDOR)));
			System.out.println("\tname: " + new String(device_parambl(device, CL10.CL_DEVICE_NAME)));
			System.out.println("\ttype: " + new String(device_paraml(device, CL10.CL_DEVICE_TYPE)+""));
			System.out.println("\textensions: " + (extensions = new String(device_parambl(device, CL10.CL_DEVICE_EXTENSIONS))));
		}
		
		

		System.err.println("No GPU devices exist with 64-bit floating point capabilities (your hardware doesn't support this game).");
		
		return devices.get(0);
//		clGetGLContextInfoKHR(properties, CL_DEVICES_FOR_GL_CONTEXT_KHR, ! ! ! ! ! !            32 * sizeof(cl_device_id), devices, &size); 
	}
	
	private static long device_paraml(long device, int param) {
		LongBuffer param_value = stack.mallocLong(1);
		PointerBuffer size_ret = stack.mallocPointer(1);
		clGetDeviceInfo(device, param, param_value, size_ret);
		long value = param_value.get(0);
//		MemoryUtil.memFree(param_value);
		return value;
	}
	
	private static int device_parami(long device, int param) {
		IntBuffer param_value = stack.mallocInt(1);
		PointerBuffer size_ret = stack.mallocPointer(1);
		clGetDeviceInfo(device, param, param_value, size_ret);
		int value = param_value.get(0);
//		MemoryUtil.memFree(param_value);
		return value;
	}
	
	private static byte[] device_parambl(long device, int param) {
		PointerBuffer size_ret = MemoryUtil.memAllocPointer(1);
		clGetDeviceInfo(device, param, (ByteBuffer) null, size_ret);
		ByteBuffer bb = MemoryUtil.memAlloc((int) size_ret.get(0));
		clGetDeviceInfo(device, param, bb, size_ret);
		byte[] data = new byte[bb.limit()];
		bb.get(data);
//		MemoryUtil.memFree(size_ret);
//		MemoryUtil.memFree(bb);
		return data;
	}
	
	private static long context() {
		IntBuffer err = stack.mallocInt(1);
		long context = CL10.clCreateContext(properties, device, null, 0, err);
		if (err.get(0) != 0) System.err.println("Error with context: " + err.get(0) + " " + context);
		return context;
	}
	
	public static long command_queue() {
		IntBuffer errcode_ret = stack.mallocInt(1);
		long command_queue = clCreateCommandQueue(context, device, 0, errcode_ret);
		if (errcode_ret.get(0) != 0) {
			Util.utlThrowException("Error making command queue: " + errcode_ret.get(0));
		}
//		MemoryUtil.memFree(errcode_ret);
		return command_queue;
	}
	
	private static int max_compute_units() {
		ByteBuffer param_value = stack.malloc(4);
		clGetDeviceInfo(device, CL_DEVICE_MAX_COMPUTE_UNITS, param_value, null);
		int value = param_value.getInt(0);
//		MemoryUtil.memFree(param_value);
		return value;
	}
	
	private static int max_clock_frequency() {
		ByteBuffer param_value = stack.malloc(4);
		clGetDeviceInfo(device, CL_DEVICE_MAX_CLOCK_FREQUENCY, param_value, null);
		int value = param_value.getInt(0);
//		MemoryUtil.memFree(param_value);
		return value;
	}
			
	private static long clGlProperty(int property) {
		PointerBuffer buffer = stack.mallocPointer(1);
		int err = KHRGLSharing.clGetGLContextInfoKHR(properties, property, buffer, null);
		long prop = buffer.get(0);
		if (err != 0) Util.utlThrowException("Error getting property " + property + ": " + err);
		return prop;
	}
	
	
	public static void print_device_info() {
		System.out.println("max compute units: " + max_compute_units);
		System.out.println("frequency: " + max_clock_frequency);
					
		CLCapabilities caps = org.lwjgl.opencl.CL.createPlatformCapabilities(platform);
		
		System.out.println("OpenCL10: " + caps.OpenCL10);
		System.out.println("OpenCL11: " + caps.OpenCL11);
		System.out.println("OpenCL12: " + caps.OpenCL12);
		System.out.println("OpenCL20: " + caps.OpenCL20);
		System.out.println("OpenCL21: " + caps.OpenCL21);
		System.out.println("OpenCL22: " + caps.OpenCL22);
		System.out.println("OpenCL10GL: " + caps.OpenCL10GL);
		System.out.println("OpenCL12GL: " + caps.OpenCL12GL);

	}
	
	public static void touch() {}

	public static long compileProgram(char code, int func) {
		IntBuffer errors = MemoryUtil.memAllocInt(1);
		long kernel = clCreateKernel(program, "func_" + func, errors);
		
		if (errors.get(0) != 0) {
			System.out.println("error building program " + code+"_"+func + ": " + errors.get(0));
//			CLHost.testProgram(ioReadInputStream(Kernels.class.getResourceAsStream("/res_type_78438/res-" + code + ".cl")));
		}
		return kernel;
	}
	
//	private static void testProgram(String program) {
//		final int platformIndex = 0;
//		final long deviceType = CL.CL_DEVICE_TYPE_ALL;
//		final int deviceIndex = 0;
//
//		// Enable exceptions and subsequently omit error checks in this sample
//		org.jocl.CL.setExceptionsEnabled(true);
//
//		// Obtain the number of platforms
//		int numPlatformsArray[] = new int[1];
//		CL.clGetPlatformIDs(0, null, numPlatformsArray);
//		int numPlatforms = numPlatformsArray[0];
//
//		// Obtain a platform ID
//		cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
//		CL.clGetPlatformIDs(platforms.length, platforms, null);
//		cl_platform_id platform = platforms[platformIndex];
//
//		// Initialize the context properties
//		cl_context_properties contextProperties = new cl_context_properties();
//		contextProperties.addProperty(CL.CL_CONTEXT_PLATFORM, platform);
//		
//		// Obtain the number of devices for the platform
//		int numDevicesArray[] = new int[1];
//		CL.clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
//		int numDevices = numDevicesArray[0];
//		
//		// Obtain a device ID
//		cl_device_id devices[] = new cl_device_id[numDevices];
//		CL.clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
//		cl_device_id device = devices[deviceIndex];
//
//		// Create a context for the selected device
//		cl_context context = CL.clCreateContext(contextProperties, 1, new cl_device_id[] { device }, null, null, null);
//
//		cl_program prg = org.jocl.CL.clCreateProgramWithSource(context, 1, new String[] { program }, null, null);
//		org.jocl.CL.clBuildProgram(prg, 1, devices, "-Werror", null, null);
//
//	}
	
	public static long[] createGLImage(int w, int h) {
		int gltex = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, gltex);
		
//		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
//		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	
//		int[] i = new int[w * h];
		
		int[] data = new int[w * h * 4];
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
		IntBuffer errcode_ret = MemoryUtil.memAllocInt(1);
		long l = clCreateFromGLTexture(context, CL_MEM_READ_WRITE, GL_TEXTURE_2D, 0, gltex, errcode_ret);
		checkerr(errcode_ret.get(0), "CL_GL Texture");
//		MemoryUtil.memFree(errcode_ret);
		
		glBindTexture(GL_TEXTURE_2D, 0);
		
		if (l == 0) Util.utlThrowException("texture ptr is null");
		
		return new long[] {l, gltex};
	}

	public static void bind_cl(long mem) {
		CL10GL.clEnqueueAcquireGLObjects(command_queue, mem, null, null);
	}
	
	public static void bind_gl(long mem) {
		CL10GL.clEnqueueReleaseGLObjects(command_queue, mem, null, null);
	}
	
	public static class clKernel {

		private static HashMap<Thread, Integer> last_run = new HashMap<Thread, Integer>();
		
		private static PointerBuffer local1 = (PointerBuffer) BufferUtils.createPointerBuffer(1).put(1).rewind();
		private static long mem_local1 = MemoryUtil.memAddress(local1);
		
		public final long kernel;
		private PointerBuffer size, local, off;
		protected final char code;
		protected final int func;
		protected Object lock = new Object();
		protected boolean running = false;
		
		private long mem_size, mem_local, mem_off;
		
//		public static final long nullBuffer;
//		private static int[] nullBuff = new int[1];

		static {
//			int[] errcode_ret = new int[1];
//			nullBuffer = clCreateBuffer(CLHost.context, CL_MEM_READ_ONLY | CL_MEM_ALLOC_HOST_PTR,
//					nullBuff, errcode_ret);
//			if (errcode_ret[0] != CL_SUCCESS)
//				throw new IllegalStateException("null buffer error: " + errcode_ret[0]);
		}

		clKernel(char kernelCode, int code) {
			this.code = kernelCode;
			func = code;
			kernel = CLHost.compileProgram(kernelCode, code);

			size = BufferUtils.createPointerBuffer(1);
			local = BufferUtils.createPointerBuffer(1);
			off = BufferUtils.createPointerBuffer(1);

			local.put(CLHost.max_compute_units).flip();
			
//			Runtime.getRuntime().addShutdownHook(new Thread() {
//				public void run() {
//					System.out.println("Kernel " + code + " calls: ");
//					for (Thread t : calls.keySet()) {
//						System.out.println("\t"+t.getName() + " " + calls.get(t));
//					}
//				}
//			});
			
			mem_size = MemoryUtil.memAddress(size);
			mem_local = MemoryUtil.memAddress(local);
			mem_off = MemoryUtil.memAddress(off);
		}

		public clKernel(int i, long program) {
			kernel = CL10.clCreateKernel(program, "func_"+i, (int[]) null);
			this.code = 'a';
			func = i;
			
			size = BufferUtils.createPointerBuffer(1);
			local = BufferUtils.createPointerBuffer(1);
			off = BufferUtils.createPointerBuffer(1);

			local.put(CLHost.max_compute_units).flip();
			
//			Runtime.getRuntime().addShutdownHook(new Thread() {
//				public void run() {
//					System.out.println("Kernel " + code + " calls: ");
//					for (Thread t : calls.keySet()) {
//						System.out.println("\t"+t.getName() + " " + calls.get(t));
//					}
//				}
//			});
			
			mem_size = MemoryUtil.memAddress(size);
			mem_local = MemoryUtil.memAddress(local);
			mem_off = MemoryUtil.memAddress(off);
		}

		protected final long runkernel(int len) {
			return runkernel(len, 0, CLHost.command_queue);
		}
		
		protected final long runkernel(int len, int off) {
			return runkernel(len, off, CLHost.command_queue);
		}
		
		protected final long runkernel(int len, int offset, long command_queue) {
//			if (len > KERNEL_MAX_SAFE_LEN) {
//				System.err.println("Length " + len + " is greater than the MAX_SAFE_LEN defined by the user (" + KERNEL_MAX_SAFE_LEN + ").");
//				System.err.println("Stacktrace: ");
//				ioPrintStackTrace();
//				release();
//				return KERNEL_ERR_LEN_TOO_LARGE;
//			}
			final int flen = len;
			if (len == 0) {
				release();
				return 0;
			}
			long ms = System.currentTimeMillis();
			int remainder = (int) (len % local.get(0));
			len -= remainder;
			size.put(0, len);
			off.put(0, offset);
//			if (klLogDebug) System.out.println(
//					"running kernel " + kernel + " on queue " + command_queue + " with size " + size.get(0));

//			int i = CL10.clEnqueueNDRangeKernel(CLHost.command_queue, kernel, 1, null, size, local, null, null);
			int i = nclEnqueueNDRangeKernel(command_queue, kernel, 1, mem_off,
					mem_size, mem_local, 0, NULL, NULL);
			if (size.get(0)!=0) checkerr(i, "kernel " + code + "_" + func+"-1\nmax_compute_units:"+CLHost.max_compute_units+"\nSize:"+size.get(0)+"\nLen:"+len+"\nLocal:"+local.get(0)+"\nlast_run:"+last_run.get(Thread.currentThread()));
			
			if (remainder != 0) {
				off.put(0, offset + len);
				size.put(0, remainder);
				i = nclEnqueueNDRangeKernel(command_queue, kernel, 1, mem_off, mem_size,
						mem_local1, 0, NULL, NULL);
				checkerr(i, "kernel " + code + "_" + func + "-2\nSize:"+size.get(0)+"\nLen: "+flen+"\nLocal:"+local1.get(0)+"\nOff:"+off.get(0)+"\nlast_run:"+last_run.get(Thread.currentThread()));
			}
			
			last_run.put(Thread.currentThread(), func);
			release();
			
			return System.currentTimeMillis() - ms;
		}
		
		private HashMap<Thread, Integer> calls = new HashMap<Thread, Integer>();
		
		private void release() {
			thrNotifyAll(lock);
			lock = new Object();
			running = false;
		}
				
		public void enqueuetask() {
			if (running) {
				int count = 0;
				while (running) {
					thrWait(lock, 1);
					count++;
					if (count > 30) {
						Iterator<Thread> threadNames = Thread.getAllStackTraces().keySet().iterator();
						for (StackTraceElement[] e : Thread.getAllStackTraces().values()) {
							System.out.println(threadNames.next() + thrStacktrace(e));
						}
					}
	//				System.exit(0);
				}
			}
			running = true;
			logtask();
		}
		
		public void logtask() {
//			calls.put(thrCurrentThread(), calls.getOrDefault(thrCurrentThread(), 0) + 1);
		}


//		public void finish(long command_queue) {
//			clFinish(command_queue);
//		}

	}
	
	
}
