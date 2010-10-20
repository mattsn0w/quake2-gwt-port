/*
 * Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.corp.websocket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class WebSocket {

	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private Listener listener;
	private ArrayList<Object> eventQueue = new ArrayList<Object>();
	public interface Listener {
		void onClose(WebSocket socket, CloseEvent event);
	    void onMessage(WebSocket socket, MessageEvent event);
	    void onOpen(WebSocket socket, OpenEvent event);
	}

	private WebSocket(String url, final String protocol) throws IOException {
	  if (!url.startsWith("ws://")) {
		  throw new RuntimeException("ws:// expected");
	  }
	  int cut = url.indexOf('/', 5);
	  if (cut == -1) {
		  cut = url.length();
	  }
	  
	  final String path = url.substring(cut);
	  String hostAndPort = url.substring(5, cut);
	  
	  cut = hostAndPort.indexOf(':');
	  final int port;
	  final String host;
	  if (cut != -1) {
	    port = Integer.parseInt(hostAndPort.substring(cut + 1));
	    host = hostAndPort.substring(0, cut);
	  } else {
		host = hostAndPort;
		port = 80;
	  }
	
	  System.out.println("Host: " + host);
	  System.out.println("Port: " + port);
	  System.out.println("Path: " + path);
	  
	  socket = new Socket(host, port);

	  inputStream = socket.getInputStream();
	  outputStream = socket.getOutputStream();
	  
      
      new Thread(new Runnable(){
		public void run() {
			try{
          println("GET /" + path + " HTTP/1.1");
          println("Upgrade: WebSocket");
          println("Connection: Upgrade");
          println("Host: " + host);
          println("Origin: http://" + host);
          println("WebSocket-Protocol: " + protocol);
          println("");
		      
          while (true) {
        	String l = readln();
        	System.out.println("Reading: " + l);
        	if (l.length() == 0) {
        	  break;
        	}
          }
	
          fire(new OpenEvent());
		  while (true) {
			 String s = readFrame();
			 fire(new MessageEvent(s));
		  }
			} catch(IOException e) {
				e.printStackTrace();
				fire(new CloseEvent());
				//close();
			}
		}}).start();
	}

	private void fire(Object event) {
		System.out.println("fireing event: " + event);
	  if (event != null) {
  	    eventQueue.add(event);
	  }
	  if (listener != null) {
		for (Object o : eventQueue) {
		  if (o instanceof MessageEvent) {
			listener.onMessage(this, (MessageEvent) o);
		  } else if (o instanceof CloseEvent) {
			  listener.onClose(this, (CloseEvent) o);
		  } else if (o instanceof OpenEvent) {
			  listener.onOpen(this, (OpenEvent) o);
		  }
		}
		eventQueue.clear();
	  }
	}
	
	private String readln() throws IOException {
	  StringBuilder sb = new StringBuilder();
	  
	  while (true) {
  	    int i = inputStream.read();
	    if (i == -1) {
		  throw new IOException("closed");
	    } else if (i == '\r') {
	    } else if (i == '\n') {
	      break;
	    } else {
	      sb.append((char) i);
	    }
	  }
	  return sb.toString();
	}
	
	
	private String readFrame() throws IOException {
		int i = inputStream.read();
		 if (i == -1) {
			  throw new IOException("closed by remote");
		  }
	  if(i != 0) {
		  System.out.println("Illegal frame start: "+ i);
	  }
	  ByteArrayOutputStream baos = new ByteArrayOutputStream();
	  while(true) {
		  i = inputStream.read();
		  if (i == -1) {
			  throw new IOException("closed by remote (in frame)");
		  }
		  if (i == 255) {
			  break;
		  }
		  baos.write(i);
	  }
	  return new String(baos.toByteArray(),"UTF-8");
	  
		}
	
	
	
	private void println(String string) throws IOException {
	  outputStream.write(string.getBytes("UTF-8"));	
	  outputStream.write('\r');
	  outputStream.write('\n');
	  if (string.length() == 0) {
		  outputStream.flush();
	  }
	}

	public void setListener(Listener listener) {
	  this.listener = listener;
	  fire(null);
	}

	public void close() throws IOException {
	  socket.close();
	}

	public static WebSocket create(String url, String string) throws IOException {
		return new WebSocket(url, string);
	}

	public void send(String s) throws IOException {
	  outputStream.write(0);
	  outputStream.write(s.getBytes("UTF-8"));
	  outputStream.write(255);
	}

}
