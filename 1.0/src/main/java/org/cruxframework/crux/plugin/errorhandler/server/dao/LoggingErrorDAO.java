/*
 * Copyright 2014 cruxframework.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cruxframework.crux.plugin.errorhandler.server.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cruxframework.crux.core.utils.FileUtils;

/**
 * @author Samuel Cardoso
 * 
 */
public class LoggingErrorDAO
{
	private static Logger logger = Logger.getLogger(LoggingErrorDAO.class.getName());
	private static File errorLogFile = null;

	public static boolean clear()
	{
		return getErrorLogFile().delete();
	}

	public static void main(String[] args) 
	{
		System.setProperty("java.io.tmpdir", "F:\\crux_compilation");

		ArrayList<Throwable> read = read();
		read.isEmpty();
	}

	public static void overwrite(ArrayList<Throwable> throwables)
	{
		ObjectOutputStream oos = null;
		FileOutputStream fout = null;
		try
		{
			fout = new FileOutputStream(getErrorLogFile());
			logger.info("Saving exception at: " + getErrorLogFile().getAbsolutePath());
			oos = new ObjectOutputStream(fout);
			oos.writeObject(throwables);
		}
		catch (IOException e)
		{
			logger.log(Level.SEVERE, "Error to write log file.", e);
		}
		finally
		{
			try
			{
				fout.close();
				oos.close();
			}
			catch (Exception e)
			{
			}
		}
	}

	public static void append(Throwable throwable)
	{
		ArrayList<Throwable> actualThrowables = read();

		if (actualThrowables == null)
		{
			actualThrowables = new ArrayList<Throwable>();
		}

		if (throwable == null)
		{
			return;
		}

		actualThrowables.add(throwable);

		overwrite(actualThrowables);
	}

	private static boolean isEmptyFile(File file)
	{
		if(!file.exists())
		{
			return true;
		}

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			if (br.readLine() == null) 
			{
				return true;
			}
		} catch (IOException e) 
		{
			e.printStackTrace();
		} finally
		{
			try 
			{
				br.close();
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		return false;
	}


	@SuppressWarnings("unchecked")
	public static ArrayList<Throwable> read()
	{
		ObjectInputStream oos = null;
		FileInputStream fins = null;
		try
		{
			File file = getErrorLogFile();
			logger.info("Reading exception from: " + file.getAbsolutePath());

			if(isEmptyFile(file))
			{
				return null;
			}
			fins = new FileInputStream(file);
			oos = new ObjectInputStream(fins);

			Object t = null;
			if((t = oos.readObject()) != null) 
			{
				return (ArrayList<Throwable>) t;
			}

			return (ArrayList<Throwable>) oos.readObject();
		}
		catch (FileNotFoundException e)
		{
			return null;
		}
		catch (IOException e)
		{
			logger.log(Level.SEVERE, "Error to read log file.", e);
		}
		catch (ClassNotFoundException e)
		{
			return null;
		}
		finally
		{
			try
			{
				fins.close();
				oos.close();
			}
			catch (Exception e)
			{
			}
		}
		return null;
	}

	private static File getErrorLogFile()
	{
		if (errorLogFile == null)
		{
			String tmpDir = FileUtils.getTempDir();
			errorLogFile = new File(tmpDir + "deferredbindingexception");

			if(isEmptyFile(errorLogFile))
			{
				try 
				{
					errorLogFile.createNewFile();
				} catch (IOException e) 
				{
					e.printStackTrace();
				}
			}

		}

		return errorLogFile;
	}
}
