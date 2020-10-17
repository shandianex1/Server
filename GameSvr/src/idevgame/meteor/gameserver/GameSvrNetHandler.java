package idevgame.meteor.gameserver;

import java.lang.reflect.InvocationTargetException;
import java.nio.channels.ClosedChannelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import idevgame.meteor.dispatcher.Dispatcher;
import idevgame.meteor.net.IHandlerNetty4;
import idevgame.meteor.net.PackCodec.Pack;
import io.netty.channel.ChannelHandlerContext;

public class GameSvrNetHandler implements IHandlerNetty4 {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private Dispatcher dispatcher;
	public GameSvrNetHandler(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	@Override
	public void onConnect(ChannelHandlerContext ctx) {
		//被其他客户端链接到.
//		System.out.println("new client connected");
		GameSvr.Instance.OnPlayerConnect(ctx);
	}

	@Override
	public void onReceive(ChannelHandlerContext ctx, Pack req) {
		Pack rsp = null;
		try {
//			System.out.println("GameSvrNetHandler.onReceive");
			rsp = dispatcher.invoke(ctx, req.cmd, req.data);
		} 
		catch (InvocationTargetException e) {
			System.out.println("协议处理出现异常" + e);
//			logger.error("协议处理出现异常", e);
		}
		catch (Exception e2) {
//			System.out.println("协议处理出现异常2" + e2);
//			logger.error("协议处理出现异常", e2);
		}
		
		if (rsp != null) {
			ctx.channel().writeAndFlush(rsp);
		}
	}

	@Override
	public void onClose(ChannelHandlerContext ctx) {
//		System.out.println("some one disconnected");
		GameSvr.Instance.OnPlayerDisConnect(ctx);
	}

	@Override
	public void onException(ChannelHandlerContext ctx, Throwable e) {
		if (!(e instanceof ClosedChannelException)) {
			e.printStackTrace();
		}
	}
}
