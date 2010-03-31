package jake2.util;

import java.nio.ByteBuffer;

/** Ugly hack to get gwt internal stuff into nio, see StringByteBuffer in nio */

public interface StringToByteBuffer {
	ByteBuffer stringToByteBuffer(String s);
}
