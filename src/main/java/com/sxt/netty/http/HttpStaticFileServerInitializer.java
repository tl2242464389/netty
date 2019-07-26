package com.sxt.netty.http;

import io.netty.channel.ChannelInitializer;  
import io.netty.channel.ChannelPipeline;  
import io.netty.channel.socket.SocketChannel;  
import io.netty.handler.codec.http.HttpObjectAggregator;  
import io.netty.handler.codec.http.HttpRequestDecoder;  
import io.netty.handler.codec.http.HttpResponseEncoder;  
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

/**
 * @description: netty 也可以做基于http协议的数据传输
 * @Date: 2019-05-30 15:22
 */
public class HttpStaticFileServerInitializer extends ChannelInitializer<SocketChannel> {  
    @Override  
    public void initChannel(SocketChannel ch) throws Exception {  
        ChannelPipeline pipeline = ch.pipeline();
        /**
         （1）ReadTimeoutHandler，用于控制读取数据的时候的超时，10表示如果10秒钟都没有数据读取了，那么就引发超时，然后关闭当前的channel
         （2）WriteTimeoutHandler，用于控制数据输出的时候的超时，构造参数1表示如果持续1秒钟都没有数据写了，那么就超时。
         （3）HttpRequestDecoder，这个handler用于从读取的数据中将http报文信息解析出来，无非就是什么requestline，header，body什么的
         （4）HttpObjectAggregator，则是用于将上面解析出来的http报文的数据组装成为封装好的HttpRequest对象
         （5）HttpResponseEncoder，用于将用户返回的httpresponse编码成为http报文格式的数据
         （6）HttpHandler，自定义的handler，用于处理接收到的http请求。
         */
        // http-request解码器,http服务器端对request解码
        pipeline.addLast("decoder", new HttpRequestDecoder());
        // 对传输文件大少进行限制
        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
        // http-response解码器,http服务器端对response编码
        pipeline.addLast("encoder", new HttpResponseEncoder());
        // 向客户端发送数据的一个Handler
        pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
        // Specify false if SSL.(如果是ssl,就指定为false)
        pipeline.addLast("handler", new HttpStaticFileServerHandler(true));
    }  
}  