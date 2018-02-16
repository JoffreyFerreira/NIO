package jus.aor.nio.v3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Hashtable;

/**
 * @author morat 
 */
public class ReadCont  extends Continuation{
	private SocketChannel sock;
	private ByteBuffer lenBuf = ByteBuffer.allocate(4);
	private ByteBuffer msgBuf = null;
	private enum State{READING_LENGTH, READING_MSG}
	private State state = State.READING_LENGTH;
	
	
	
	/**
	 * @param sc
	 */
	public ReadCont(SocketChannel sc){
		super(sc);
	}
	/**
	 * @return the message
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	protected Message handleRead() throws IOException, ClassNotFoundException{
		if(state==State.READING_LENGTH) {
			int nb = sock.read(lenBuf);
			int length = bytesToInt(lenBuf);
			if(lenBuf.remaining()==0) {
				msgBuf = ByteBuffer.allocate(length);
				lenBuf.position(0);
				state = State.READING_MSG;
			}
		}
		if(state==State.READING_MSG) {
			if(msgBuf.remaining()==0) {
				sock.read(msgBuf);
				Message msg = new Message(msgBuf.array());
				msgBuf = null;
				state = State.READING_LENGTH;
				return msg;
			}
		}
		return null;
	}
}

