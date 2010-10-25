package jake2.buf;

import com.google.gwt.corp.compatibility.Numbers;

public class StringDataReader extends DataReader {

  private String s;
  private int position, limit;
  private int mark;

  public StringDataReader(String s) {
    this.s = s;
  }

  public StringDataReader(String s, int position, int limit) {
    this(s);
    this.position = position;
    this.limit = limit;
  }

  @Override
  public void clear() {
    position = 0;
  }

  @Override
  public byte get() {
    return get(position++);
  }

  @Override
  public void get(byte[] dst) {
    get(dst, 0, dst.length);
  }

  @Override
  public void get(byte[] dst, int ofs, int len) {
    for (int i = 0; i < len; ++i) {
      dst[ofs + i] = get();
    }
  }

  public byte get(int index) {
    return get(s, index);
  }

  public final float getFloat() {
    return Numbers.intBitsToFloat(getInt());
  }

  public final float getFloat(int index) {
    return Numbers.intBitsToFloat(getInt(index));
  }

  public final int getInt() {
    int newPosition = position + 4;
    int result = loadInt(position);
    position = newPosition;
    return result;
  }

  public final int getInt(int index) {
    return loadInt(index);
  }

  public final short getShort() {
    int newPosition = position + 2;
    short result = loadShort(position);
    position = newPosition;
    return result;
  }

  public final short getShort(int index) {
    return loadShort(index);
  }

  @Override
  public int limit() {
    return limit;
  }

  @Override
  public void limit(int limit) {
    this.limit = limit;
  }

  @Override
  public void mark() {
    mark = position;
  }

  @Override
  public int position() {
    return position;
  }

  @Override
  public void position(int pos) {
    this.position = pos;
  }

  @Override
  public void reset() {
    position = mark;
  }

  @Override
  public DataReader slice() {
    // TODO(jgw): I don't think this is right, but might work for our purposes.
    return new StringDataReader(s, position, limit);
  }

  protected final int loadInt(int baseOffset) {
    int bytes = 0;
    for (int i = 3; i >= 0; i--) {
      bytes = bytes << 8;
      bytes = bytes | (get(baseOffset + i) & 0xFF);
    }
    return bytes;
  }

  protected final short loadShort(int baseOffset) {
    short bytes = 0;
    bytes = (short) (get(baseOffset + 1) << 8);
    bytes |= (get(baseOffset) & 0xFF);
    return bytes;
  }

  private native byte get(String s, int i) /*-{
    var x = s.charCodeAt(i) & 0xff;
    if (x > 127) x -= 256;
    return x;
  }-*/;
}
