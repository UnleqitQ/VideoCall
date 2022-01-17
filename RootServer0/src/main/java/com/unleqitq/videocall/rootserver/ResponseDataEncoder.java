package com.unleqitq.videocall.rootserver;

import com.unleqitq.videocall.transferclasses.call.ResponseData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ResponseDataEncoder extends MessageToByteEncoder<ResponseData> {
	
	@Override
	protected void encode(ChannelHandlerContext ctx, ResponseData msg, ByteBuf out) throws Exception {
		out.writeInt(msg.getResponseValue());
		out.writeInt(msg.getRequestValue());
	}
	
}
