package com.unleqitq.videocall.rootserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.*;


public class RootServer {
	
	YAMLConfiguration configuration = new YAMLConfiguration();
	ServerBootstrap bootstrap;
	EventLoopGroup bossGroup;
	EventLoopGroup workerGroup;
	
	public RootServer() {
		reloadConfig();
		try {
			initNetwork();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void reloadConfig() {
		File file = new File(new File("./").getAbsoluteFile().getParent(), "properties.yml");
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
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
		else {
			InputStream is = null;
			FileWriter writer = null;
			try {
				is = getClass().getClassLoader().getResourceAsStream("properties.yml");
				configuration.read(is);
				file.getParentFile().mkdirs();
				file.createNewFile();
				writer = new FileWriter(file);
				configuration.write(writer);
			} catch (ConfigurationException | IOException e) {
				e.printStackTrace();
			} finally {
				if (is != null)
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				if (writer != null)
					try {
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
	}
	
	private void initNetwork() throws Exception {
		workerGroup = new NioEventLoopGroup();
		bossGroup = new NioEventLoopGroup();
		try {
			bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(
					new ChannelInitializer<>() {
						
						@Override
						protected void initChannel(Channel ch) throws Exception {
							
							ch.pipeline().addLast(new RequestDecoder(), new ResponseDataEncoder(),
									new ProcessingHandler());
						}
					}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
			
			ChannelFuture f = bootstrap.bind(configuration.getInt("network.server.port")).sync();
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) {
		new RootServer();
	}
	
}
