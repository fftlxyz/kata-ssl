package kata.ssl.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.CharsetUtil;

import java.util.UUID;

public class ServerHandlersInit extends ChannelInitializer<SocketChannel> {

    protected void initChannel(SocketChannel socketChannel) throws Exception {

        SslHandler sslHandler = SSLHandlerProvider.getSSLHandler();

        socketChannel.pipeline().addLast(
                sslHandler,
                new HttpServerCodec(),
                new HttpObjectAggregator(10485760),
                new SimpleChannelInboundHandler<FullHttpRequest>() {

                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {

                        String data = UUID.randomUUID().toString();
                        DefaultFullHttpResponse response
                                = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                                Unpooled.copiedBuffer(data, CharsetUtil.UTF_8));
                        channelHandlerContext.writeAndFlush(response);
                        channelHandlerContext.channel().close();
                    }
                });
    }

}