package com.unleqitq.videocall.sharedclasses;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface ByteSerializable {
	
	void readBuffer(ByteBuf buffer) throws IOException, ClassNotFoundException, NoSuchMethodException,
			InvocationTargetException, InstantiationException, IllegalAccessException;
	void writeBuffer(ByteBuf buffer) throws IOException;
	
}
