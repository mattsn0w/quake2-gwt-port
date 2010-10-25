package jake2.buf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class DataReader {

  public static DataReader allocate(int capacity) {
    return wrapper(ByteBuffer.allocate(capacity));
  }

  public static DataReader wrap(byte[] array) {
    return wrap(array, 0, array.length);
  }

  public static DataReader wrap(final byte[] array, final int offset,
      final int length) {
    return wrapper(ByteBuffer.wrap(array, offset, length));
  }

  private static DataReader wrapper(final ByteBuffer buf) {
    buf.order(ByteOrder.LITTLE_ENDIAN);
    return new DataReader() {
      @Override
      public byte get() {
        return buf.get();
      }

      @Override
      public void get(byte[] dst) {
        buf.get(dst);
      }

      @Override
      public void get(byte[] dst, int ofs, int len) {
        buf.get(dst, ofs, len);
      }

      @Override
      public float getFloat() {
        return buf.getFloat();
      }

      @Override
      public int getInt() {
        return buf.getInt();
      }

      @Override
      public short getShort() {
        return buf.getShort();
      }

      @Override
      public int limit() {
        return buf.limit();
      }

      @Override
      public void limit(int limit) {
        buf.limit(limit);
      }

      @Override
      public int position() {
        return position();
      }

      @Override
      public void position(int pos) {
        buf.position(pos);
      }

      @Override
      public DataReader slice() {
        return wrapper(buf.slice());
      }

      @Override
      public void mark() {
        buf.mark();
      }

      @Override
      public void reset() {
        buf.reset();
      }

      @Override
      public void clear() {
        buf.clear();
      }
    };
  }

  DataReader() {
  }

  public abstract byte get();
  public abstract void get(byte[] dst);
  public abstract void get(byte[] dst, int ofs, int len);
  public abstract float getFloat();
  public abstract int getInt();
  public abstract short getShort();

  public abstract int limit();
  public abstract void limit(int limit);
  public abstract int position();
  public abstract void position(int pos);
  public abstract void mark();
  public abstract void reset();
  public abstract void clear();

  public abstract DataReader slice();
}
