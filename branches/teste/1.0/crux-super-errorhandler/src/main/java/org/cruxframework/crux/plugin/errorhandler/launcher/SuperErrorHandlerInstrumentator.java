package org.cruxframework.crux.plugin.errorhandler.launcher;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.tools.ajc.Main;
import org.cruxframework.crux.tools.launcher.CruxLauncher;

public class SuperErrorHandlerInstrumentator extends CruxLauncher
{
	private static final Log logger = LogFactory.getLog(SuperErrorHandlerInstrumentator.class);
	
	private static final String GWT_DEV_VERSION = "2.5.1";
	private static final String GWT_DEV_NAME = "gwt-dev";
	private static final String WEAVED_NAME = "weaved";
	private static final String ASPECT_JAR_FILE = "super-error-handler-1.0.0.jar";
	
	public static void main(String[] args) 
	{
		run();
	}
	
	public static String run() 
	{
		logger.info("Instrumentation GWT jar...");
		String classpath = instrumentateGWTJar(GWT_DEV_VERSION, GWT_DEV_NAME, WEAVED_NAME, true);
		logger.info("All done!");
		return classpath;
	}
	
	private static String getAspectsJar()
	{
		String classpath = System.getProperty("java.class.path");
		String aspectJarPath;
		int index = classpath.indexOf(ASPECT_JAR_FILE);
		if (index >=0)
		{
			aspectJarPath = classpath.substring(0, index + ASPECT_JAR_FILE.length());
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
		String jarToWeave = GWT_DEV_NAME+"-"+GWT_DEV_VERSION+".jar";
		String classpath = System.getProperty("java.class.path");
		String jarToWeavePath = classpath.substring(0, classpath.indexOf(jarToWeave) + jarToWeave.length());
		jarToWeavePath = jarToWeavePath.substring(jarToWeavePath.lastIndexOf(File.pathSeparatorChar)+1, jarToWeavePath.length());
		return jarToWeavePath;
	}
	
	public static String instrumentateGWTJar(String gwtDevVersion, String gwtDevName, String weavedName, boolean overrideGWTJar) 
	{
		String jarToWeavePath = getJarToWeavePath();
		String aspectsJar = getAspectsJar();
		
		String jarWeavedPath = jarToWeavePath.replace(gwtDevVersion + ".jar", weavedName+"-"+gwtDevVersion+".jar");;

		File fileJarToWeave = new File(jarToWeavePath);
		File fileJarWeaved = new File(jarWeavedPath);
		try 
		{
			if (!fileJarWeaved.exists())
			{
				createWeavedJar(jarToWeavePath, aspectsJar, jarWeavedPath);
				
				if(overrideGWTJar)
				{
					String backupFile = new String(jarToWeavePath).replace(gwtDevVersion + ".jar", "backup-"+gwtDevVersion+".jar"); 
					File fileBackup = new File(backupFile);
					FileUtils.copyFile(fileJarToWeave, fileBackup);
					FileUtils.copyFile(fileJarWeaved, fileJarToWeave);
					FileUtils.forceDelete(fileJarWeaved);
				}
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
		logger.info("Creating instrumented jar file to allow dev mode error handling...");
		String[] ajcArgs = {
	    		"-inpath", jarToWeavePath,
	    		"-aspectpath", aspectsJar,
	    		"-noExit",
	    		"-Xlint:ignore",
	    		"-preserveAllLocals",
	    		"-time",
	    		"-verbose",
	    		"-showWeaveInfo",
	    		"-outjar", jarWeavedPath,
	    		"-1.6"
	    };
	    Main.main(ajcArgs);
    }
}
