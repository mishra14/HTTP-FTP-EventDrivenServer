package edu.upenn.cis.cis455.webserver;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.upenn.cis.cis455.http.HttpHandler;
import edu.upenn.cis.cis455.http.HttpRequest;

public class HttpServer {
	
  private static final Logger logger = Logger.getLogger(HttpServer.class);
  private static final int ARGS_LENGTH = 2;
  private static final String ENCODING = "UTF-8";
  private static int port;
  private static String homeDirectory;
  private static boolean run;
  public static void main(String args[])
  {
	if(args.length !=ARGS_LENGTH)
	{
		logger.warn("Invalid number of arguments\nAnkit Mishra\nmankit");
		System.exit(1);
	}
	try
	{
		port=Integer.valueOf(args[0]);
		homeDirectory=args[1].trim();
		if(!(new File(homeDirectory).exists()))
		{
			logger.warn("Invalid home directory - "+homeDirectory+"\nAnkit Mishra\nmankit");
			System.exit(1);
		}
	}
	catch(NumberFormatException e)
	{
		logger.warn("Invalid port number\nAnkit Mishra\nmankit");
		System.exit(1);
	}
	
	
	if(homeDirectory.endsWith("/"))
	{
		homeDirectory=new String(homeDirectory.substring(0,homeDirectory.length()-1));
	}	
	run = true;
    ServerSocketChannel daemonSocketChannel = null;
    Selector selector = null;
    logger.info("Starting event driven http server at http://localhost:"+port);
    try {
    	//get inet address
        InetSocketAddress serverAddress = new InetSocketAddress(port);
        // open server socket channel
    	daemonSocketChannel = ServerSocketChannel.open();
    	//make it non blocking
		daemonSocketChannel.configureBlocking(false);
		daemonSocketChannel.socket().bind(serverAddress);
		selector = Selector.open();
		daemonSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		
	} catch (IOException e) {
		logger.error("IOException while starting the server...Exiting",e);
		run=false;
	}
    
    while(run)
    {
    	try 
    	{
			while(selector.select()>0)
			{
				Set<SelectionKey> keySet= selector.selectedKeys();
				Iterator<SelectionKey> iterator = keySet.iterator();
				while(iterator.hasNext())
				{
					SelectionKey key = iterator.next();
					if(key.isAcceptable())
					{
						logger.info("New connection accepted");
						ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
						SocketChannel socketChannel = serverSocketChannel.accept();
						socketChannel.configureBlocking(false);
						socketChannel.register(selector,SelectionKey.OP_READ);
					}
					else if(key.isReadable())
					{
						logger.info("In coming request on a client socket");
						SocketChannel socketChannel = (SocketChannel)key.channel();
						socketChannel.configureBlocking(false);
						//read request
						ByteBuffer requestBuffer = ByteBuffer.allocate(5000);
						socketChannel.read(requestBuffer);
						requestBuffer.flip(); 	//reset buffer to start
						Charset charset = Charset.forName(ENCODING);	
						CharsetDecoder decoder = charset.newDecoder();
						CharBuffer decodedCharBuffer = decoder.decode(requestBuffer);
						logger.info(decodedCharBuffer.toString());
						//parse request into HttpRequest Object
						HttpRequest httpRequest = new HttpRequest(decodedCharBuffer.toString());
						logger.info(httpRequest.toString());
						//Handle request
				    	ByteBuffer response = ByteBuffer.wrap(HttpHandler.handleRequest(httpRequest));
				    	//write response to the socket channel
						socketChannel.write(response);
						response.clear();
						//close socket channel
						socketChannel.close();
						if(!run)	//check if a shut down was requested
						{
						    logger.warn("System shutting down ");
						    System.exit(1);
						}
					}
					iterator.remove();
				}
			}
		} catch (IOException e) {
			logger.error("IOException while reading from selector",e);

		} catch (Exception e) {
			logger.error("Exception in main ",e);
    	}
    }
    
  }

public static int getPort() {
	return port;
}

public static String getHomeDirectory() {
	return homeDirectory;
}

public static boolean isRun() {
	return run;
}

public static void setRun(boolean run) {
	HttpServer.run = run;
}
  
  
}

