package com.unleqitq.videocall.sharedclasses;

import com.unleqitq.videocall.transferclasses.crypt.AESKeyData;
import com.unleqitq.videocall.transferclasses.crypt.RSAKeyData;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public abstract class ServerNetworkConnection extends AbstractNetworkConnection {
	
	Server server;
	
	public ServerNetworkConnection(Server server, Socket socket) throws IOException {
		super(socket);
		this.server = server;
		inputStream = socket.getInputStream();
		outputStream = socket.getOutputStream();
		createObjectStreams();
	}
	
	@Override
	public void onAES(AESKeyData data) {
		try {
			data.getKey(server.getRsaPrivateKey());
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onRSA(RSAKeyData data) {
	
	}
	
}
