package com.unleqitq.videocall.transferclasses.call;

import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.UUID;

public record ScreenVideoData(long creation, byte[] imageData, UUID user) implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -9206928578052012556L;
	
	@NotNull
	public BufferedImage getImage() throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
		return ImageIO.read(bais);
	}
	
	@NotNull
	public static ScreenVideoData create(@NotNull RenderedImage image, @NotNull UUID user) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "png", baos);
		return new ScreenVideoData(System.currentTimeMillis(), baos.toByteArray(), user);
	}
	
}
