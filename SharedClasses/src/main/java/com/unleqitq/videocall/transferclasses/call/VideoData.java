package com.unleqitq.videocall.transferclasses.call;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.UUID;

public record VideoData(long creation, byte[] imageData, UUID user) implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -9206928578052012556L;
	
	@Nullable
	public BufferedImage getImage() throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
		return ImageIO.read(bais);
	}
	
	@NotNull
	public static VideoData create(@NotNull BufferedImage image, @NotNull UUID user) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "jpg", baos);
		if (baos.size() == 0)
			ImageIO.write(image, "png", baos);
		return new VideoData(System.currentTimeMillis(), baos.toByteArray(), user);
	}
	
	@Override
	public String toString() {
		return "VideoData{ difference=" + (System.currentTimeMillis() - creation) + ", user=" + user + ", bytesize=" + imageData.length + " }";
	}
	
}
