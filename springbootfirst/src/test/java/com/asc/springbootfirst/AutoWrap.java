package com.asc.springbootfirst;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * ddhub加密
 * @author
 */
public class AutoWrap {
	private static final ByteArrayOutputStream byteCache = new ByteArrayOutputStream(
			10485760);

	private static HashMap<String, String> needDoJar = new HashMap<String, String>();

	static {
		needDoJar.put("WEB-INF/lib/asc-common-1.1.2.RC.jar",
				"WEB-INF/lib/asc-common.RC.jar");
		needDoJar.put("WEB-INF/lib/asc-common-base-0.9.0.2.jar",
				"WEB-INF/lib/asc-common-base.jar");
		needDoJar.put("WEB-INF/lib/asc-common-database-0.9.0.24.jar",
				"WEB-INF/lib/asc-common-database.jar");
//		needDoJar.put("WEB-INF/lib/asc-common-ftp-0.9.0-SNAPSHOT.jar",
//				"WEB-INF/lib/asc-common-ftp.jar");
		needDoJar.put("WEB-INF/lib/security-framework-1.0.6-SNAPSHOT.jar",
			"WEB-INF/lib/security-framework.jar");
	}
	
	private static HashMap<String, String> dir = new HashMap<String, String>();
	
	
	private static HashMap<String, String> clzs = new HashMap<String, String>();

	public static void main(String[] args) {
		if (args.length == 0)
			// args = new String[] { "E:\\IdeaProjects2018\\DDHub\\DDHUb-CODE\\branches\\DDHUB_DERBY\\target", "pro-action.war" };
			args = new String[] { "E:\\IdeaProjects2018\\DDHub\\DDHUb-CODE\\trunk\\DDHUB\\pro-action\\target", "pro-action.war" };
		String dest = null;
		String rawWarFile = null;
		if ((args.length == 0) || (args.length < 2)) {
			System.out.println("缺少参数：<目标路径> <需要加密的WAR包名称>");
			return;
		}
		dest = args[0] + "/";
		rawWarFile = args[1];
		System.out.println("目录：" + dest);
		if (!rawWarFile.equalsIgnoreCase("root.war")) {
			System.out.println("WAR：" + rawWarFile);
		} else {
			System.out.println("不能使用ROOT.war，请更改文件名!");
			return;
		}

		String classesPath = "WEB-INF/classes";
		String root = "WEB-INF/classes/com";

		ZipFile rawWar = null;
		ZipOutputStream wrapped = null;

		File jarfile = null;
		JarOutputStream jarfileStream = null;
		Manifest manifest = new Manifest();
		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION,
				"1.1");
		try {
			rawWar = new ZipFile(dest + rawWarFile);
			wrapped = new ZipOutputStream(new FileOutputStream(dest
					+ "ROOT.war"));
			jarfile = new File(dest + "spring-ddhub.jar");
			jarfileStream = new JarOutputStream(new BufferedOutputStream(
					new FileOutputStream(jarfile)), manifest);

			Enumeration enums = rawWar.entries();

			// long jarFileSize = 0L;
			while (enums.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) enums.nextElement();
				if (entry.getName().startsWith(root)) {
					String classname = entry.getName().substring(
							entry.getName().indexOf(classesPath)
									+ classesPath.length() == 0 ? 0
									: classesPath.length() + 1);
					
					if (dir.get(classname)==null) {
						dir.put(classname, classname);
					} else {
						System.err.println(classname);
						continue;
					}
					if (entry.isDirectory()) {
						JarEntry cls = new JarEntry(classname);
						jarfileStream.putNextEntry(cls);
						jarfileStream.flush();
						jarfileStream.closeEntry();
						continue;
					}
					JarEntry cls = new JarEntry(classname);
					jarfileStream.putNextEntry(cls);
					
					
					byte[] bytes = getClassFileBytes(rawWar
							.getInputStream(entry));
					ByteArrayOutputStream bo = new ByteArrayOutputStream();
					bo.write(new byte[]{110,114,119,120});
					bo.write(bytes,0,bytes.length);
					bo.close();
					// jarFileSize += newbytes.length;
					jarfileStream.write(bo.toByteArray());

					jarfileStream.flush();
					jarfileStream.closeEntry();
				} else {
					if (needDoJar.get(entry.getName()) != null) {
						File f = File.createTempFile(entry.getSize()+"", ".a");
						f.deleteOnExit();
						FileOutputStream fo = new FileOutputStream(f);
						
						byte[] inbytes = getClassFileBytes(rawWar
								.getInputStream(entry));
						fo.write(inbytes);
						fo.close();
						
						
						ZipFile jar = new ZipFile(f);
						Enumeration jarenums = jar.entries();
						while (jarenums.hasMoreElements()) {
							ZipEntry entryTemp = (ZipEntry) jarenums.nextElement();
							
							if (entryTemp.isDirectory() || !entryTemp.getName().endsWith(".class")) {
								if (dir.get(entryTemp.getName())==null) {
									dir.put(entryTemp.getName(), entryTemp.getName());
								} else {
									System.err.println(entryTemp.getName());
									continue;
								}
								
								jarfileStream.putNextEntry(entryTemp);
								jarfileStream.write(getClassFileBytes(jar.getInputStream(entryTemp)));
								jarfileStream.flush();
								jarfileStream.closeEntry();
								
								
							} else {
								
								
								if (dir.get(entryTemp.getName())==null) {
									dir.put(entryTemp.getName(), entryTemp.getName());
								} else {
									System.err.println(entryTemp.getName());
									continue;
								}
								
								String classname = entryTemp.getName();
					    		JarEntry cls = new JarEntry(classname);
					    		jarfileStream.putNextEntry(cls);
					    		
					    		byte[] bytes = getClassFileBytes(jar.getInputStream(entryTemp));
					    		ByteArrayOutputStream bo = new ByteArrayOutputStream();
								bo.write(new byte[]{110,114,119,120});
								bo.write(bytes,0,bytes.length);
								bo.close();
								// jarFileSize += newbytes.length;
								jarfileStream.write(bo.toByteArray());
					    		
					    		jarfileStream.flush();
					    		jarfileStream.closeEntry();
							}
							
						}
					  //  close(new Object[] { jar });
					   // wrapped.closeEntry();
//						getJarDoBytes(rawWar.getInputStream(entry));
						//
//						wrapped.putNextEntry(new ZipEntry(needDoJar.get(entry
//								.getName())));
//						wrapped.write(getJarDoBytes(entry.getName()));
					    
					    
					    
					    
					    
						wrapped.flush();
						wrapped.closeEntry();
					} else {
						if (dir.get(entry.getName())==null) {
							dir.put(entry.getName(), entry.getName());
						} else {
							continue;
						}
						
						wrapped.putNextEntry(new ZipEntry(entry.getName()));
						wrapped.write(getClassFileBytes(rawWar
								.getInputStream(entry)));
						wrapped.flush();
						wrapped.closeEntry();
					}
				}
			}
			jarfileStream.finish();
			jarfileStream.close();

			wrapped.putNextEntry(new ZipEntry("WEB-INF/lib/spring-ddhub.jar"));
			wrapped.write(getClassFileBytes(new FileInputStream(jarfile)));
			wrapped.flush();
			wrapped.closeEntry();

			wrapped.finish();
			// jarfile.delete();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(new Object[] { rawWar, wrapped });
		}
	}

	public static void close(Object[] arrays) {
		Object[] arrayOfObject = arrays;
		int j = arrays.length;
		for (int i = 0; i < j; i++) {
			Object stream = arrayOfObject[i];
			if (stream == null)
				continue;
			try {
				if ((stream instanceof InputStream))
					((InputStream) stream).close();
				else if ((stream instanceof OutputStream))
					((OutputStream) stream).close();
				else if ((stream instanceof ZipFile))
					((ZipFile) stream).close();
				else
					throw new RuntimeException("invalid steam");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static byte[] getClassFileBytes(InputStream input) throws Exception {
		byteCache.reset();
		BufferedInputStream buffer = new BufferedInputStream(input);
		try {
			byte[] bufferS = new byte[1024];
			int b = -1;
			while ((b = buffer.read(bufferS)) != -1)
				byteCache.write(bufferS, 0, b);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close(new Object[] { buffer });
		return byteCache.toByteArray();
	}

	public static byte[] getJarDoBytes(InputStream input) throws Exception {
		byteCache.reset();
		
		JarInputStream jis = new JarInputStream(input);
//		JarFile jarFile = new JarFile(jarName);  
		 JarEntry entry = null;
	    while (( entry = jis.getNextJarEntry())!=null) {  
	        System.out.println(entry.getName());
	    }  
		
//		BufferedInputStream buffer = new BufferedInputStream(input);
//		try {
//			byte[] bufferS = new byte[1024];
//			int b = -1;
//			while ((b = buffer.read(bufferS)) != -1)
//				byteCache.write(bufferS, 0, b);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		close(new Object[] { buffer });
		return byteCache.toByteArray();
	}

	public static InputStream getFileInputStream(URL url) throws Exception {
		if (url.getProtocol().equals("jar")) {
			JarURLConnection con = (JarURLConnection) url.openConnection();
			return con.getJarFile().getInputStream(con.getJarEntry());
		}
		if (url.getProtocol().equals("file")) {
			return new FileInputStream(url.getPath());
		}
		System.out.println("无法识别的 URL: " + url);
		Thread.sleep(1000L);
		return null;
	}
}