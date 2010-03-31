package jake2.sys;

import jake2.qcommon.netadr_t;

import java.io.IOException;

public interface QSocket {

	int receive(netadr_t from, byte[] buf) throws IOException;

	void send(netadr_t dstSocket, byte[] data, int len) throws IOException;

	void close() throws IOException;

}
