package com.unleqitq.videocall.rootserver;

import com.unleqitq.videocall.transferclasses.access.RequestData;
import com.unleqitq.videocall.transferclasses.call.ResponseData;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ProcessingHandler extends ChannelInboundHandlerAdapter {
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		RequestData requestData = (RequestData) msg;
		ResponseData responseData = new ResponseData();
		responseData.setResponseValue(requestData.getIntValue() * 2);
		responseData.setRequestValue(requestData.getIntValue());
		ChannelFuture future = ctx.writeAndFlush(responseData);
		future.addListener(ChannelFutureListener.CLOSE);
		System.out.println(requestData);
	}
	
}
