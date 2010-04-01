/*
 Copyright (C) 1997-2001 Id Software, Inc.

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

 See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

 */
/* Modifications
   Copyright 2003-2004 Bytonic Software
   Copyright 2010 Google Inc.
*/
package jake2.desktop;

import jake2.client.WebSocketFactoryImpl;
import jake2.qcommon.Compatibility;
import jake2.qcommon.Cvar;
import jake2.qcommon.Globals;
import jake2.qcommon.Qcommon;
import jake2.qcommon.ResourceLoader;
import jake2.sound.DummyDriver;
import jake2.sound.S;
import jake2.sys.NET;
import jake2.sys.Sys;
import jake2.sys.Timer;
/**
 * This is the original main class from Jake2.
 */
public final class DesktopQuake {

	public static void main(String[] args) {
		main(args, false);
	}

	public static void main(String[] args, boolean wireframe) {
    
    // Initialize drivers.
    Globals.re = wireframe ? new DesktopWireframeRenderer() : new DesktopRenderer();

    ResourceLoader.impl = new ResourceLoaderImpl();
    Compatibility.impl = new CompatibilityImpl();
    S.impl = new DummyDriver();
    NET.socketFactory = new WebSocketFactoryImpl();
  
    // Parse flags.
    int argc = (args == null) ? 1 : args.length + 1;
    String[] c_args = new String[argc];
    c_args[0] = "GQuake";
    if (argc > 1) {
      System.arraycopy(args, 0, c_args, 1, argc - 1);
    }
    Qcommon.Init(c_args);

//    Cbuf.AddText("map demo1\n");
//    Cbuf.Execute();

    
    // Enable stdout.
    Globals.nostdout = Cvar.Get("nostdout", "0", 0);

    int oldtime = Timer.Milliseconds();
    int newtime;
    int time;
    while (true) {
      // find time spending rendering last frame
      newtime = Timer.Milliseconds();
      time = newtime - oldtime;

      if (time > 0)
        Qcommon.Frame(time);
      oldtime = newtime;
    }
  }
}
