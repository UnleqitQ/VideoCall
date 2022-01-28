package com.unleqitq.videocall.rootserver.managers.filetransfer;

import com.google.gson.JsonObject;
import com.unleqitq.videocall.sharedclasses.FileMeta;

import java.io.File;
import java.util.UUID;

public class FileManager {
	
	File rootFolder;
	JsonObject data = new JsonObject();
	
	public FileManager() {
		rootFolder = new File(new File("./").getAbsoluteFile().getParent(), "files");
		if (!rootFolder.exists()) {
			rootFolder.mkdirs();
		}
		
	}
	
	public FileMeta getFileData(UUID teamUuid, UUID fileUuid) {
		if (!data.has(teamUuid + "_" + fileUuid))
			return null;
		JsonObject fileData = data.get(teamUuid + "_" + fileUuid).getAsJsonObject();
		FileMeta meta = FileMeta.load(fileData);
		return meta;
	}
	
}
