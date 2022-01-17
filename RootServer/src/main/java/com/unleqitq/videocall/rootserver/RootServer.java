package com.unleqitq.videocall.rootserver;

import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.*;

public class RootServer {
	
	YAMLConfiguration configuration = new YAMLConfiguration();
	
	public RootServer() {
		
		try {
			reloadConfig();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(configuration.getInt("network.server.port"));
	}
	
	private void reloadConfig() throws IOException {
		File file = new File("properties.yml");
		if (file.exists()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				configuration.read(fis);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (ConfigurationException e) {
				e.printStackTrace();
			} finally {
				if (fis != null)
					fis.close();
			}
		}
		else {
			InputStream is = null;
			FileWriter writer = null;
			try {
				is = getClass().getResourceAsStream("properties.yml");
				configuration.read(is);
				file.getParentFile().mkdirs();
				file.createNewFile();
				writer = new FileWriter(file);
				configuration.write(writer);
			} catch (ConfigurationException e) {
				e.printStackTrace();
			} finally {
				if (is != null)
					is.close();
				if (writer != null)
					writer.close();
			}
		}
	}
	
	public static void main(String[] args) {
		new RootServer();
	}
	
}
