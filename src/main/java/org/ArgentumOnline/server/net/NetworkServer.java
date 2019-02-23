package org.ArgentumOnline.server.net;

import static org.ArgentumOnline.server.Constants.SERVER_PORT;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ArgentumOnline.server.Player;
import org.ArgentumOnline.server.GameServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkServer extends Thread {
	private static Logger log = LogManager.getLogger();
	
	private final GameServer server;
	
    private ClientProcessThread processThread;
	
    private ServerSocketChannel serverSocket;
    private Selector selector;
    private final static int BUFFER_SIZE = 1024;
    private ByteBuffer serverBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private Map<SocketChannel, Player> m_clientSockets = new HashMap<>();
	
	private NetworkServer(GameServer server) {
		this.server = server;
	}
	
	private static NetworkServer instance;
	public static NetworkServer startServer(GameServer server) {
		instance = new NetworkServer(server);
		instance.start();
		return instance;
	}

	private boolean running = true;
	public void run() {
		this.initServerSocket();
        (this.processThread = new ClientProcessThread()).start();
		
        while (running) {
		    // Waiting for events...
			try {
				this.selector.select(40); // ms
			    Set<SelectionKey> keys = this.selector.selectedKeys();
			    for (SelectionKey key: keys) {
			    
			    	// if isAcceptable, then a client required a connection.
			        if (key.isAcceptable()) {
			            log.info("nueva conexion");
			            acceptConnection();
			            continue;
			        }
			        
			        // if isReadable, then the server is ready to read
			        if (key.isReadable()) {
			            log.info("leer de conexion");
			            readConnection((SocketChannel) key.channel());
			            continue;
			        }
			        
			    }
			    keys.clear();
			} catch (IOException e) {
				log.fatal("ERROR on network select", e);
			} 
		}
	};
	
    private void initServerSocket() {
        try {
			this.serverSocket = ServerSocketChannel.open();
	        this.serverSocket.configureBlocking(false);
	        this.serverSocket.socket().bind(new InetSocketAddress(SERVER_PORT));
	        log.info("Escuchando en el puerto " + SERVER_PORT);
	        this.selector = Selector.open();
	        this.serverSocket.register(this.selector, SelectionKey.OP_ACCEPT);
	        
        } catch (UnknownHostException ex) {
            log.fatal("AOServer run loop", ex);
            
        } catch (java.net.BindException ex) {
            log.fatal("El puerto ya está en uso. ¿El servidor ya está corriendo?", ex);
            
        } catch (IOException ex) {
            log.fatal("AOServer run loop", ex);
		}
    }
    
    private void acceptConnection() 
    throws java.io.IOException {
        // get client socket channel.
        SocketChannel clientSocket = this.serverSocket.accept();
        // Non blocking I/O
        clientSocket.configureBlocking(false);
        // recording to the selector (reading)
        clientSocket.register(this.selector, SelectionKey.OP_READ);
        
        Player cliente = server.createClient(clientSocket);
        this.m_clientSockets.put(clientSocket, cliente);
        
        log.info("NUEVA CONEXION");
    }
    
    public void closeConnection(Player client) {
        log.info("cerrando conexion");
        this.m_clientSockets.remove(client.socketChannel);
        try {
			client.socketChannel.close();
		} catch (IOException e) {
			log.fatal("ERROR closing client connection", e);
		}
    }
    
    /** Lee datos de una conexión existente. */
    private void readConnection(SocketChannel clientSocket) 
    throws java.io.IOException {
        Player cliente = this.m_clientSockets.get(clientSocket);
        log.info("Recibiendo del cliente: " + cliente);
        // Read bytes coming from the client.
        this.serverBuffer.clear();
        try {
            clientSocket.read(this.serverBuffer);
            
            // process the message.
            this.serverBuffer.flip();
            
            if (this.serverBuffer.limit() > 0) {
            	cliente.bufferLengths.add(this.serverBuffer.limit());
            	cliente.readBuffer.put(this.serverBuffer.array());
                
                this.processThread.addClientQueue(cliente);
            }else {
            	cliente.doSALIR();
            }
            
        } catch (Exception e) {
            cliente.doSALIR();
            return;
        }       
    }

	public void shutdown() {
		this.running = false;
		this.processThread.endThread();
	}

}
