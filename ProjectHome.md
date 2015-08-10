_Quake II and the Quake logo are trademarks of id Software._


# Quake II GWT Port #

The Quake II GWT port brings the 3d gaming experience of [Quake II](http://www.idsoftware.com/games/quake/quake2/) to the browser.

In the port, we use WebGL, the Canvas API, HTML 5 `<audio>` elements, the local storage API, and WebSockets to demonstrate the possibilities of pure web applications in modern browsers such as Safari and Chrome.

The port is based on the Jake2 project, compiled to Javascript using the Google Web Toolkit (GWT).  Jake 2 is a Java port of the original Quake II source code, which was open sourced by id software.

To make the Jake 2 code work with GWT, we have

  * Created a new [WebGL](https://cvs.khronos.org/svn/repos/registry/trunk/public/webgl/doc/spec/WebGL-spec.html) based renderer
  * Ported the network layer for multiplayer games from UDP to the [WebSocket API](http://dev.w3.org/html5/websockets/)
  * Made all resource loading calls asynchronous
  * Created a GWT implementation of Java nio buffers based on WebGL arrays (to be ported to [ECMAScript Typed Arrays](http://people.mozilla.com/~vladimir/jsvec/TypedArray-spec.html))
  * Implemented a simple file system emulation for saving games and preferences using the [Web Storage API](http://dev.w3.org/html5/webstorage/)

## News ##

  * We have ported this to PlayN (mainly to reuse the fullscreen and mouse lock API) and have added a client side  content downloader, which means the demo now runs in a browser without any additonal setup.
    * Source code: https://code.google.com/p/quake2-playn-port/
    * Demo: http://quake2playn.appspot.com
  * We just got this **working in Firefox 4 nightly builds**, thanks to Barak and Benoit from Mozilla. See CompatibleBrowsers for instructions.
  * Some recent Chromium lockup and sound issues should be fixed now, too.

## Links ##
  * [Compatible browsers](CompatibleBrowsers.md)
  * [Building and running the code](BuildingAndRunning.md)
  * [FAQ](FAQ.md)
  * [Discussion group](http://groups.google.com/group/quake2-gwt-port)

## Thanks ##
  * id Software for building a [great game](http://www.idsoftware.com/games/quake/quake2/) and having the foresight to open-source it.
  * [Bytonic Software](http://bytonic.de/) for doing the Java port, and proving it could be just as fast as the native one.
  * [Vladimir Vukićević](http://blog.vlad1.com/) for his pioneering work on Canvas3D and WebGL.

## More background ##
  * [From Ray](http://timepedia.blogspot.com/2010/04/gwtquake-taking-web-to-next-level.html)
  * [From Joel](http://blog.j15r.com/2010/04/quake-ii-in-html5-what-does-this-really.html)

## Video ##
<a href='http://www.youtube.com/watch?feature=player_embedded&v=fyfu4OwjUEI' target='_blank'><img src='http://img.youtube.com/vi/fyfu4OwjUEI/0.jpg' width='425' height=344 /></a>