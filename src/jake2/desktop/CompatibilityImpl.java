
package jake2.desktop;

import jake2.qcommon.Compatibility;

public class CompatibilityImpl implements Compatibility.Impl {

	public int floatToIntBits(float f) {
		return Float.floatToIntBits(f);
	}

	public float intBitsToFloat(int i) {
		return intBitsToFloat(i);
	}

	public String createString(byte[] b, int ofs, int length) {

		return new String(b, ofs, length);
	}

	public String getOriginatingServerAddress() {
		return "127.0.0.1";
	}

	public void printStackTrace(Throwable e) {
		e.printStackTrace();
	}

	public String createString(byte[] b, String encoding) {
		return null;
	}

	public void loadClass(String name) throws ClassNotFoundException {
		Class.forName(name);
	}

	public void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch(InterruptedException e) {
			
		}
		
	}

}
