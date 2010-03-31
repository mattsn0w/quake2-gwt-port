/*
 * Timer.java
 * Copyright (C) 2005
 * 
 * $Id: Timer.java,v 1.2 2005/07/01 14:20:54 hzi Exp $
 */
package jake2.sys;

import jake2.qcommon.Globals;

public abstract class Timer {

  private static long base = System.currentTimeMillis();

	public static int Milliseconds() {
    long time = System.currentTimeMillis();
    long delta = time - base;
    if (delta < 0) {
      delta += Long.MAX_VALUE + 1;
    }

    return Globals.curtime = (int)(delta);
	}
}
