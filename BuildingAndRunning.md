Caveat: We've been building entirely on Mac & Linux boxes, so YMMV on Windows. It shouldn't be too much work to get it going, but we haven't tried yet.

4 easy steps:
  1. Make sure Ogg Vorbis, Java and Lame are installed. Java is needed to compile the code and to convert the images. Ogg and Lame are needed to convert the Quake II sound resources for the Web.
    * On Linux, run `sudo apt-get install vorbis-tools openjdk-6-jdk` and try `sudo apt-get install lame`. If installing Lame via apt does not work, install it from the [original source](http://lame.sourceforge.net/download.php).
    * On a Mac, install [MacPorts](http://www.macports.org/) and then run `sudo port install vorbis-tools` and `sudo port install lame`.
      * 64.brian suggests to use [HomeBrew](http://mxcl.github.com/homebrew/) instead of MacPorts because it requires less resources.
  1. [Check out](http://code.google.com/p/quake2-gwt-port/source/checkout) the code and change into the project directory (`cd quake2-gwt-port`).
  1. `ant run` (will install the original Quake II demo resources, build the client and server code, then run the server).
  1. Navigate to: http://localhost:8080/GwtQuake.html (or whatever port you specified to the server).

If you run into an issue on a particular platform, the comments below may contain some help.