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
package jake2.gwt.client;

import static jake2.qcommon.Defines.CS_PLAYERSKINS;

import jake2.client.Console;
import jake2.game.Cmd;
import jake2.game.cvar_t;
import jake2.game.entity_state_t;
import jake2.qcommon.Com;
import jake2.qcommon.Cvar;
import jake2.qcommon.Defines;
import jake2.qcommon.Globals;
import jake2.qcommon.xcommand_t;
import jake2.sound.Sound;
import jake2.sound.sfx_t;
import jake2.sound.sfxcache_t;
import jake2.util.Lib;
import jake2.util.Vargs;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import com.google.gwt.core.client.JavaScriptObject;


public class GwtSound implements Sound {

private boolean hasEAX;
	
	private cvar_t s_volume;
	
	// the last 4 buffers are used for cinematics streaming
    private IntBuffer buffers = Lib.newIntBuffer(MAX_SFX + STREAM_QUEUE);

	/* (non-Javadoc)
	 * @see jake2.sound.SoundImpl#Init()
	 */
	public boolean Init() {
		
		
		// set the listerner (master) volume
		s_volume = Cvar.Get("s_volume", "0.7", Defines.CVAR_ARCHIVE);
		Cmd.AddCommand("play", new xcommand_t() {
			public void execute() {
				Play();
			}
		});
		Cmd.AddCommand("stopsound", new xcommand_t() {
			public void execute() {
				StopAllSounds();
			}
		});
		Cmd.AddCommand("soundlist", new xcommand_t() {
			public void execute() {
				SoundList();
			}
		});
		Cmd.AddCommand("soundinfo", new xcommand_t() {
			public void execute() {
				SoundInfo_f();
			}
		});

		num_sfx = 0;

		Com.Printf("sound sampling rate: 44100Hz\n");

		StopAllSounds();
		Com.Printf("------------------------------------\n");
		return true;
	}
	
	// TODO check the sfx direct buffer size
	// 2MB sfx buffer
	private ByteBuffer sfxDataBuffer = Lib.newByteBuffer(2 * 1024 * 1024);
	
	/* (non-Javadoc)
	 * @see jake2.sound.SoundImpl#RegisterSound(jake2.sound.sfx_t)
	 */
	private void initBuffer(byte[] samples, int bufferId, int freq) {
//		ByteBuffer data = sfxDataBuffer.slice();
//		data.put(samples).flip();
	}

	/* (non-Javadoc)
	 * @see jake2.sound.SoundImpl#Shutdown()
	 */
	public void Shutdown() {
		StopAllSounds();
		
		Cmd.RemoveCommand("play");
		Cmd.RemoveCommand("stopsound");
		Cmd.RemoveCommand("soundlist");
		Cmd.RemoveCommand("soundinfo");

		// free all sounds
		for (int i = 0; i < num_sfx; i++) {
			if (known_sfx[i].name == null)
				continue;
			known_sfx[i].clear();
		}
		num_sfx = 0;
	}
	
	/* (non-Javadoc)
	 * @see jake2.sound.SoundImpl#StartSound(float[], int, int, jake2.sound.sfx_t, float, float, float)
	 */
	public void StartSound(float[] origin, int entnum, int entchannel, sfx_t sfx, float fvol, float attenuation, float timeofs) {

		if (sfx == null)
			return;
			
		if (sfx.name.charAt(0) == '*')
			sfx = RegisterSexedSound(Globals.cl_entities[entnum].current, sfx.name);
		
		if (LoadSound(sfx) == null)
			return; // can't load sound

		if (attenuation != Defines.ATTN_STATIC)
			attenuation *= 0.5f;

//		PlaySound.allocate(origin, entnum, entchannel, buffers.get(sfx.bufferId), fvol, attenuation, timeofs);
		((gwtsfxcache_t)sfx.cache).audioElement.play();
	}
	
	private FloatBuffer listenerOrigin = Lib.newFloatBuffer(3);
	private FloatBuffer listenerOrientation = Lib.newFloatBuffer(6);

	/* (non-Javadoc)
	 * @see jake2.sound.SoundImpl#Update(float[], float[], float[], float[])
	 */
	public void Update(float[] origin, float[] forward, float[] right, float[] up) {
	
	}
	
	private IntBuffer eaxEnv = Lib.newIntBuffer(1);
	
	private void changeEnvironment(int env) {

	}

	/* (non-Javadoc)
	 * @see jake2.sound.SoundImpl#StopAllSounds()
	 */
	public void StopAllSounds() {
		// mute the listener (master)
	  
	}
	
	/* (non-Javadoc)
	 * @see jake2.sound.Sound#getName()
	 */
	public String getName() {
		return "HTML5Audio";
	}

	int s_registration_sequence;
	boolean s_registering;

	/* (non-Javadoc)
	 * @see jake2.sound.Sound#BeginRegistration()
	 */
	public void BeginRegistration() {
		s_registration_sequence++;
		s_registering = true;
	}

	public native static void log(String s) /*-{
       $wnd.console.log(s);	
	}-*/;
	/* (non-Javadoc)
	 * @see jake2.sound.Sound#RegisterSound(java.lang.String)
	 */
	public sfx_t RegisterSound(String name) {
//		log("Trying to load "+name);

		sfx_t sfx = FindName(name, true);
		sfx.registration_sequence = s_registration_sequence;

		if (!s_registering)
			LoadSound(sfx);
			
		return sfx;
	}

	/* (non-Javadoc)
	 * @see jake2.sound.Sound#EndRegistration()
	 */
	public void EndRegistration() {
		int i;
		sfx_t sfx;
		int size;

		// free any sounds not from this registration sequence
		for (i = 0; i < num_sfx; i++) {
			sfx = known_sfx[i];
			if (sfx.name == null)
				continue;
			if (sfx.registration_sequence != s_registration_sequence) {
				// don't need this sound
				sfx.clear();
			}
		}

		// load everything in
		for (i = 0; i < num_sfx; i++) {
			sfx = known_sfx[i];
			if (sfx.name == null)
				continue;
			LoadSound(sfx);
		}

		s_registering = false;
	}
	
	sfx_t RegisterSexedSound(entity_state_t ent, String base) {

		sfx_t sfx = null;

		// determine what model the client is using
		String model = null;
		int n = CS_PLAYERSKINS + ent.number - 1;
		if (Globals.cl.configstrings[n] != null) {
			int p = Globals.cl.configstrings[n].indexOf('\\');
			if (p >= 0) {
				p++;
				model = Globals.cl.configstrings[n].substring(p);
				//strcpy(model, p);
				p = model.indexOf('/');
				if (p > 0)
					model = model.substring(0, p);
			}
		}
		// if we can't figure it out, they're male
		if (model == null || model.length() == 0)
			model = "male";

		// see if we already know of the model specific sound
		String sexedFilename = "#players/" + model + "/" + base.substring(1);
		//Com_sprintf (sexedFilename, sizeof(sexedFilename), "#players/%s/%s", model, base+1);
		sfx = FindName(sexedFilename, false);

		if (sfx != null) return sfx;
		
		//
		// fall back strategies
		//
		// not found , so see if it exists
//		if (FileSystem.FileLength(sexedFilename.substring(1)) > 0) {
			// yes, register it
//			return RegisterSound(sexedFilename);
//		}
	    // try it with the female sound in the pak0.pak
//		if (model.equalsIgnoreCase("female")) {
//			String femaleFilename = "player/female/" + base.substring(1);
//			if (FileSystem.FileLength("sound/" + femaleFilename) > 0)
//			    return AliasName(sexedFilename, femaleFilename);
//		}
		// no chance, revert to the male sound in the pak0.pak
		String maleFilename = "player/male/" + base.substring(1);
		return AliasName(sexedFilename, maleFilename);
	}
	

	static sfx_t[] known_sfx = new sfx_t[MAX_SFX];
	static {
		for (int i = 0; i< known_sfx.length; i++)
			known_sfx[i] = new sfx_t();
	}
	static int num_sfx;

	sfx_t FindName(String name, boolean create) {
		int i;
		sfx_t sfx = null;

		if (name == null)
			Com.Error(Defines.ERR_FATAL, "S_FindName: NULL\n");
		if (name.length() == 0)
			Com.Error(Defines.ERR_FATAL, "S_FindName: empty name\n");

		if (name.length() >= Defines.MAX_QPATH)
			Com.Error(Defines.ERR_FATAL, "Sound name too long: " + name);

		// see if already loaded
		for (i = 0; i < num_sfx; i++)
			if (name.equals(known_sfx[i].name)) {
				return known_sfx[i];
			}

		if (!create)
			return null;

		// find a free sfx
		for (i = 0; i < num_sfx; i++)
			if (known_sfx[i].name == null)
				// registration_sequence < s_registration_sequence)
				break;

		if (i == num_sfx) {
			if (num_sfx == MAX_SFX)
				Com.Error(Defines.ERR_FATAL, "S_FindName: out of sfx_t");
			num_sfx++;
		}

		sfx = known_sfx[i];
		sfx.clear();
		sfx.name = name;
		sfx.registration_sequence = s_registration_sequence;
		sfx.bufferId = i;

		return sfx;
	}

	/*
	==================
	S_AliasName

	==================
	*/
	sfx_t AliasName(String aliasname, String truename)
	{
		sfx_t sfx = null;
		String s;
		int i;

		s = new String(truename);

		// find a free sfx
		for (i=0 ; i < num_sfx ; i++)
			if (known_sfx[i].name == null)
				break;

		if (i == num_sfx)
		{
			if (num_sfx == MAX_SFX)
				Com.Error(Defines.ERR_FATAL, "S_FindName: out of sfx_t");
			num_sfx++;
		}
	
		sfx = known_sfx[i];
		sfx.clear();
		sfx.name = new String(aliasname);
		sfx.registration_sequence = s_registration_sequence;
		sfx.truename = s;
		// set the AL bufferId
		sfx.bufferId = i;

		return sfx;
	}
 
	static class HTML5AudioElement extends JavaScriptObject {
		protected HTML5AudioElement() {}
		public final native void play() /*-{
			if(this.currentTime > 0) {
			  if(this.seekable && this.seekable.length && this.seekable.start) {
			  	  this.currentTime = 0;
			  } else {
			  	var x = new $wnd.Audio(this.src);
     			x.setAttribute("preload", "auto");
			  	x.play();
			  }
			}
			this.play();
			
		}-*/;
		
		public static native HTML5AudioElement create(String name) /*-{
			var x = new $wnd.Audio(name);
			x.setAttribute("preload", "auto");
			return x;
		}-*/;
	}
	static class gwtsfxcache_t extends sfxcache_t {
		public gwtsfxcache_t(int size) {
			super(size);
			// TODO Auto-generated constructor stub
		}

		public HTML5AudioElement audioElement; 
	}
	/*
	==============
	S_LoadSound
	==============
	*/
	public sfxcache_t LoadSound(sfx_t s) {
//		log("Loadsound "+s.name);
	    if (s.isCached) return s.cache;
	    if (s.name.charAt(0) == '*')
			return null;

		// see if still in memory
		gwtsfxcache_t sc = (gwtsfxcache_t) s.cache;
		if (sc != null)
			return sc;

		String name;
		// load it in
		if (s.truename != null)
			name = s.truename;
		else
			name = s.name;

		String namebuffer;
		if (name.charAt(0) == '#')
			namebuffer = name.substring(1);
		else
			namebuffer = "sound/" + name;

		sc = new gwtsfxcache_t(1);
		
		if (sc != null) {
			s.cache = sc;
			initBuffer(sc.data, s.bufferId, sc.speed);
		    s.isCached = true;
		    // free samples for GC
		    s.cache.data = null;
		}
		if(namebuffer.endsWith(".wav")) {
			namebuffer = namebuffer.substring(0, namebuffer.length()-4)+".wav.mp3";
		}
		Console.Print("Creating audio element "+namebuffer + "\r");
		sc.audioElement = HTML5AudioElement.create("baseq2/"+namebuffer);
		return sc;
	}

	/* (non-Javadoc)
	 * @see jake2.sound.Sound#StartLocalSound(java.lang.String)
	 */
	public void StartLocalSound(String sound) {
		sfx_t sfx;

		sfx = RegisterSound(sound);
		if (sfx == null) {
			Com.Printf("S_StartLocalSound: can't cache " + sound + "\n");
			return;
		}
		StartSound(null, Globals.cl.playernum + 1, 0, sfx, 1, 1, 0);		
	}

    private ShortBuffer streamBuffer = sfxDataBuffer.slice().order(ByteOrder.BIG_ENDIAN).asShortBuffer();

    /* (non-Javadoc)
	 * @see jake2.sound.Sound#RawSamples(int, int, int, int, byte[])
	 */
	public void RawSamples(int samples, int rate, int width, int channels, ByteBuffer data) {
        int format;
       
	}
    
    public void disableStreaming() {
    }
	/*
	===============================================================================

	console functions

	===============================================================================
	*/

	void Play() {
		int i;
		String name;
		sfx_t sfx;

		i = 1;
		while (i < Cmd.Argc()) {
			name = new String(Cmd.Argv(i));
			if (name.indexOf('.') == -1)
				name += ".wav";

			sfx = RegisterSound(name);
			StartSound(null, Globals.cl.playernum + 1, 0, sfx, 1.0f, 1.0f, 0.0f);
			i++;
		}
	}

	void SoundList() {
		int i;
		sfx_t sfx;
		sfxcache_t sc;
		int size, total;

		total = 0;
		for (i = 0; i < num_sfx; i++) {
			sfx = known_sfx[i];
			if (sfx.registration_sequence == 0)
				continue;
			sc = sfx.cache;
			if (sc != null) {
				size = sc.length * sc.width * (sc.stereo + 1);
				total += size;
				if (sc.loopstart >= 0)
					Com.Printf("L");
				else
					Com.Printf(" ");
				Com.Printf("(%2db) %6i : %s\n", new Vargs(3).add(sc.width * 8).add(size).add(sfx.name));
			} else {
				if (sfx.name.charAt(0) == '*')
					Com.Printf("  placeholder : " + sfx.name + "\n");
				else
					Com.Printf("  not loaded  : " + sfx.name + "\n");
			}
		}
		Com.Printf("Total resident: " + total + "\n");
	}
	
	void SoundInfo_f() {

		Com.Printf("%5d stereo\n", new Vargs(1).add(1));
		Com.Printf("%5d samples\n", new Vargs(1).add(22050));
		Com.Printf("%5d samplebits\n", new Vargs(1).add(16));
		Com.Printf("%5d speed\n", new Vargs(1).add(44100));
	}

}
