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
import org.cruxframework.crux.tools.launcher.CruxLauncher;

public class SuperErrorHandlerLauncher extends CruxLauncher
{
	private static final Log logger = LogFactory.getLog(SuperErrorHandlerLauncher.class);
	
	public static void main(String[] args) throws MalformedURLException
	{
		String classpath = SuperErrorHandlerInstrumentator.run();
		
		if (classpath == null)
		{
			CruxLauncher.main(args);
		}
		else
		{
			try
            {
				callLauncherChangingClasspath(CruxLauncher.class, args, classpath);
            }
            catch (Exception e)
            {
    			logger.error("Error Running ModulesLauncher.", e);;
            }
		}
	}

	private static int callLauncherChangingClasspath(Class<?> clazz, String[] args, String classpath) throws IOException, InterruptedException
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
}
