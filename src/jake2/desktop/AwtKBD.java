/*
Copyright (C) 2010 Copyright 2010 Google Inc.

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
package jake2.desktop;


import jake2.client.Key;
import jake2.sys.KBD;
import jake2.sys.Timer;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;



public class AwtKBD extends KBD implements KeyListener {

	public void keyPressed(KeyEvent e) {
		Do_Key_Event(XLateKey(e.getKeyCode()), true);
	}

	public void keyReleased(KeyEvent e) {
		Do_Key_Event(XLateKey(e.getKeyCode()), false);
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}


  @Override
  public void Close() {
  }

  @Override
  public void Do_Key_Event(int key, boolean down) {
    Key.Event(key, down, Timer.Milliseconds());
  }

  @Override
  public void Init() {
  }

  @Override
  public void Update() {
  }

  @Override
  public void installGrabs() {
  }

  @Override
  public void uninstallGrabs() {
  }

  private int XLateKey(int code) {
    int key = 0;

    switch(code) {
      case KeyEvent.VK_PAGE_UP: key = Key.K_PGUP; break;
      case KeyEvent.VK_PAGE_DOWN: key = Key.K_PGDN; break;
      case KeyEvent.VK_HOME: key = Key.K_HOME; break;
      case KeyEvent.VK_END: key = Key.K_END; break;
      case KeyEvent.VK_LEFT: key = Key.K_LEFTARROW; break;
      case KeyEvent.VK_RIGHT: key = Key.K_RIGHTARROW; break;
      case KeyEvent.VK_DOWN: key = Key.K_DOWNARROW; break;
      case KeyEvent.VK_UP: key = Key.K_UPARROW; break; 
      case KeyEvent.VK_ESCAPE: key = Key.K_ESCAPE; break; 
      case KeyEvent.VK_ENTER: key = Key.K_ENTER; break; 
      case KeyEvent.VK_TAB: key = Key.K_TAB; break; 
      case KeyEvent.VK_BACK_SPACE: key = Key.K_BACKSPACE; break; 
      case KeyEvent.VK_DELETE: key = Key.K_DEL; break; 
      case KeyEvent.VK_SHIFT: key = Key.K_SHIFT; break; 
      case KeyEvent.VK_CONTROL: key = Key.K_CTRL; break; 
// TODO(jgw): We probably need keycodes for these.
//      case KeyCodes.KEY_PAUSE: key = Key.K_PAUSE; break; 
//      case KeyCodes.KEY_MENU: key = Key.K_ALT; break;
//      case KeyCodes.KEY_INSERT: key = Key.K_INS; break;

// TODO(jgw): We can get keycodes for these, but they're probably not reliable,
// because the browsers so often take them over.
//      case KeyCodes.KEY_F1: key = Key.K_F1; break;
//      case KeyCodes.KEY_F2: key = Key.K_F2; break;
//      case KeyCodes.KEY_F3: key = Key.K_F3; break;
//      case KeyCodes.KEY_F4: key = Key.K_F4; break;
//      case KeyCodes.KEY_F5: key = Key.K_F5; break;
//      case KeyCodes.KEY_F6: key = Key.K_F6; break;
//      case KeyCodes.KEY_F7: key = Key.K_F7; break;
//      case KeyCodes.KEY_F8: key = Key.K_F8; break;
//      case KeyCodes.KEY_F9: key = Key.K_F9; break;
//      case KeyCodes.KEY_F10: key = Key.K_F10; break;
//      case KeyCodes.KEY_F11: key = Key.K_F11; break;
//      case KeyCodes.KEY_F12: key = Key.K_F12; break; 
      default: 
    	  key = code;
    }

    if (key > 255)
      key = 0;
    return key;
  } 
}
