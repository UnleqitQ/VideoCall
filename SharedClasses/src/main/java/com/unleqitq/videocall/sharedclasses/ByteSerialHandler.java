package com.unleqitq.videocall.sharedclasses;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ByteSerialHandler {
	
	public static void write(ByteBuf buffer, ByteSerializable serializable) throws IOException {
		writeString(buffer, serializable.getClass().getName());
		serializable.writeBuffer(buffer);
	}
	
	public static ByteSerializable read(ByteBuf buffer) throws IOException, ClassNotFoundException,
			NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		String name = readString(buffer);
		Class<? extends ByteSerializable> clazz = (Class<? extends ByteSerializable>) Class.forName(name);
		Constructor<? extends ByteSerializable> constructor = clazz.getDeclaredConstructor();
		constructor.setAccessible(true);
		ByteSerializable data = constructor.newInstance();
		data.readBuffer(buffer);
		return data;
	}
	
	public static void writeString(ByteBuf buffer, String string) {
		byte[] bytes = string.getBytes();
		buffer.writeShort(bytes.length);
		buffer.writeBytes(bytes);
	}
	
	public static String readString(ByteBuf buffer) {
		int length = buffer.readShort();
		byte[] bytes = new byte[length];
		buffer.readBytes(bytes);
		return new String(bytes);
	}
	
	
}
