package com.unleqitq.videocall.sharedclasses;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.UUID;

public class ObjectHandler {
	
	public static class ObjectOutputHandler {
		
		private final ObjectOutput out;
		
		public ObjectOutputHandler(ObjectOutput out) {
			
			this.out = out;
		}
		
		public static void writeByteArray(ObjectOutput out, byte[] bytes) throws IOException {
			out.writeInt(bytes.length);
			out.write(bytes);
		}
		
		public void writeUuid(UUID uuid) throws IOException {
			out.writeLong(uuid.getMostSignificantBits());
			out.writeLong(uuid.getLeastSignificantBits());
		}
		
		public void writeString(String string) throws IOException {
			byte[] bytes = string.getBytes();
			out.writeInt(bytes.length);
			out.write(bytes);
		}
		
		public static void writeUuid(ObjectOutput out, UUID uuid) throws IOException {
			out.writeLong(uuid.getMostSignificantBits());
			out.writeLong(uuid.getLeastSignificantBits());
		}
		
		public static void writeString(ObjectOutput out, String string) throws IOException {
			byte[] bytes = string.getBytes();
			out.writeInt(bytes.length);
			out.write(bytes);
		}
		
	}
	
	public static class ObjectInputHandler {
		
		private ObjectInput in;
		
		public ObjectInputHandler(ObjectInput in) {
			
			this.in = in;
		}
		
		public static byte[] readByteArray(ObjectInput in) throws IOException {
			int length = in.readInt();
			byte[] bytes = new byte[length];
			in.read(bytes);
			return bytes;
		}
		
		public UUID readUuid() throws IOException {
			long mostSignificant = in.readLong();
			long leastSignificant = in.readLong();
			return new UUID(mostSignificant, leastSignificant);
		}
		
		public String readString() throws IOException {
			int length = in.readInt();
			byte[] bytes = new byte[length];
			in.read(bytes);
			return new String(bytes);
		}
		
		public static UUID readUuid(ObjectInput in) throws IOException {
			long mostSignificant = in.readLong();
			long leastSignificant = in.readLong();
			return new UUID(mostSignificant, leastSignificant);
		}
		
		public static String readString(ObjectInput in) throws IOException {
			int length = in.readInt();
			byte[] bytes = new byte[length];
			in.read(bytes);
			return new String(bytes);
		}
		
	}
	
}
