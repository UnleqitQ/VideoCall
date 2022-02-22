package com.unleqitq.videocall.sharedclasses;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CombineInputStream extends InputStream {
	
	ConcurrentMap<InputStream, InputStream> streams = new ConcurrentHashMap<>();
	
	public void addStream(InputStream stream) {
		streams.put(stream, stream);
	}
	
	@Override
	public int read() throws IOException {
		int i = 0;
		for (InputStream s : streams.values()) {
			try {
				int v = s.read();
				if (v < 0) {
					s.close();
					streams.remove(s);
				}
				if (v > 127)
					v -= 256;
				i += v;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		int v = i;
		v = Math.max(Math.min(v, 127), -128);
		if (v < 0)
			v += 256;
		return v;
	}
	
}
