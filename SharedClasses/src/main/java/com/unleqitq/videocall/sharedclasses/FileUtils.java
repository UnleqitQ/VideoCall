package com.unleqitq.videocall.sharedclasses;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.InflaterOutputStream;

public class FileUtils {
	
	public static void write(File folder, FileData fileData) {
		byte[] data = fileData.data();
		FileMeta meta = fileData.meta();
		File file = new File(folder, meta.getName());
		if (file.exists()) {
			int i = 1;
			do {
				file = new File(folder, meta.getName() + "_" + i + "." + meta.getType());
			} while (file.exists());
		}
		if (folder.exists())
			folder.mkdirs();
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		InflaterOutputStream ios = new InflaterOutputStream(fos);
		try {
			ios.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			ios.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
