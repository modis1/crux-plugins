package org.cruxframework.crux.plugin.errorhandler.launcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.tools.ajc.Main;
import org.cruxframework.crux.module.launch.ModulesLauncher;

public class SuperErrorHandlerLauncher extends ModulesLauncher
{
	private static final Log logger = LogFactory.getLog(SuperErrorHandlerLauncher.class);
	
	public static void main(String[] args) throws MalformedURLException
	{
		String classpath = changeClassPath();
		if (classpath == null)
		{
			ModulesLauncher.main(args);
		}
		else
		{
			try
            {
	            callProcess(ModulesLauncher.class, args, classpath);
            }
            catch (Exception e)
            {
    			logger.error("Error Running ModulesLauncher.", e);;
            }
		}
	}

	private static int callProcess(Class<?> clazz, String[] args, String classpath) throws IOException, InterruptedException
	{
		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
		String className = clazz.getCanonicalName();

		RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
		List<String> jvmArguments = runtimeMxBean.getInputArguments();
		
		List<String> newArgs = new ArrayList<String>();
		newArgs.add(javaBin);
		newArgs.add("-cp");
		newArgs.add(classpath);
		for (String jvmArg : jvmArguments)
		{
			newArgs.add(jvmArg);
		}
		newArgs.add(className);
		for (String arg : args)
        {
			newArgs.add(arg);
        }
		
		ProcessBuilder builder = new ProcessBuilder(newArgs);
		Process process = builder.start();
		inheritIO(process.getInputStream(), System.out);
	    inheritIO(process.getErrorStream(), System.err);
		process.waitFor();
		return process.exitValue();
	}

	private static void inheritIO(final InputStream src, final PrintStream dest)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				Scanner sc = new Scanner(src);
				while (sc.hasNextLine())
				{
					dest.println(sc.nextLine());
				}
			}
		}).start();
	}
	
	/**
	 * 
	 */
	private static String changeClassPath() 
	{
		String jarToWeavePath = getJarToWeavePath();
		String aspectsJar = getAspectsJar();
		String jarWeavedPath = jarToWeavePath.substring(0, jarToWeavePath.lastIndexOf(".jar"))+"_weaved.jar";

		File fileJarToWeave = new File(jarToWeavePath);
		File fileJarWeaved = new File(jarWeavedPath);
		try 
		{
			if (!fileJarWeaved.exists())
			{
				createWeavedJar(jarToWeavePath, aspectsJar, jarWeavedPath);
			}
			String classpath = System.getProperty("java.class.path");
			classpath = classpath.replace(fileJarToWeave.getCanonicalPath(), fileJarWeaved.getCanonicalPath());
			return classpath;
		} 
		catch (IOException e) 
		{
			logger.error("Error switching gwt-dev.jar file on classpath, to allow de mode error handling.", e);;
		}
		return null;
	}

	private static void createWeavedJar(String jarToWeavePath, String aspectsJar, String jarWeavedPath) throws IOException
    {
		logger.info("Creating instrumented gwt-dev.jar file to allow dev mode error handling...");
		String[] ajcArgs = {
	    		"-inpath", jarToWeavePath,
	    		"-aspectpath", aspectsJar,
	    		"-noExit",
	    		"-Xlint:ignore",
	    		"-outjar", jarWeavedPath,
	    		"-1.6"
	    };
	    Main.main(ajcArgs);
    }

	private static String getAspectsJar()
	{
		String aspectJar = "super-error-handler-1.0.0.jar";
		String classpath = System.getProperty("java.class.path");
		String aspectJarPath;
		int index = classpath.indexOf(aspectJar);
		if (index >=0)
		{
			aspectJarPath = classpath.substring(0, index + aspectJar.length());
		}
		else
		{
			aspectJarPath = LoggingErrorHandlerAspect.class.getProtectionDomain().getCodeSource().getLocation().getPath();//+(LoggingErrorHandlerAspect.class.getPackage().getName()).replace(".", "/");
		}
		aspectJarPath = aspectJarPath.substring(aspectJarPath.lastIndexOf(File.pathSeparatorChar)+1, aspectJarPath.length());
		return aspectJarPath;
	}

	private static String getJarToWeavePath()
	{
		String jarToWeave = "gwt-dev-2.5.1.jar";
		String classpath = System.getProperty("java.class.path");
		String jarToWeavePath = classpath.substring(0, classpath.indexOf(jarToWeave) + jarToWeave.length());
		jarToWeavePath = jarToWeavePath.substring(jarToWeavePath.lastIndexOf(File.pathSeparatorChar)+1, jarToWeavePath.length());
		return jarToWeavePath;
	}	
}
