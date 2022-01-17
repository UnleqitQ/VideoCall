package com.unleqitq.videocall.client;

import com.unleqitq.videocall.transferclasses.call.ResponseData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class ResponseDataDecoder extends ReplayingDecoder<ResponseData> {
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		
		ResponseData data = new ResponseData();
		data.setResponseValue(in.readInt());
		data.setRequestValue(in.readInt());
		out.add(data);
	}
	
}
