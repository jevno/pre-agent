package com.preapm.agent.util;

import com.preapm.agent.weave.impl.AroundInterceptorCollector;

import java.io.File;
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;

public class ClassLoaderUtil {

	public static void loadJar(String path) {
		// 系统类库路径
		File libPath = new File(path);

		// 获取所有的.jar和.zip文件
		File[] jarFiles = libPath.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if(name.endsWith(".jar") || name.endsWith(".zip")){
					String jarName = name.substring(0, name.lastIndexOf("."));
					if(PreApmConfigUtil.isTargetJar(jarName)){
						return true;
					}
				}

				return false;
			}
		});

		if (jarFiles != null) {
			// 从URLClassLoader类中获取类所在文件夹的方法
			// 对于jar文件，可以理解为一个存放class文件的文件夹
			Method method = null;
			try {
				method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
			} catch (NoSuchMethodException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			boolean accessible = method.isAccessible(); // 获取方法的访问权限
			try {
				if (accessible == false) {
					method.setAccessible(true); // 设置方法的访问权限
				}
				// 获取系统类加载器
				URLClassLoader classLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
				;
				for (File file : jarFiles) {
					try {
						URL url = file.toURI().toURL();
						method.invoke(classLoader, url);
						System.out.println("读取jar文件成功" + file.getName());
					} catch (Exception e) {
						System.out.println("读取jar文件失败");
					}
				}
			} finally {
				method.setAccessible(accessible);
			}
		}
	}

	public static String getJARPath() {
		String decode = AroundInterceptorCollector.class.getProtectionDomain()
				.getCodeSource().getLocation().getFile();

		return decode.substring(0,decode.lastIndexOf("/"));
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
//		  loadJar("C:\\eclipse-workspace\\test\\target");
		System.out.println(getJARPath());

	}
}
