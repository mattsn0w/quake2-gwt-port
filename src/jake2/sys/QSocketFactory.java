package jake2.sys;

import java.net.InetSocketAddress;

public interface QSocketFactory {

	QSocket bind(String ip, int port);

}
