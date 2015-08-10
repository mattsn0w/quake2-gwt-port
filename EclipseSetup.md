Three easy steps:
  1. First, [build the project](BuildingAndRunning.md) (this is necessary for some dependencies).
  1. Install the [Google Plugin for Eclipse](http://code.google.com/eclipse/) (not strictly necessary, but recommended).
  1. Import the project (File/Import/Existing Projects into Workspace: project files are in the root of the repo).

Manual Eclipse project setup / build path fixes:
  1. Use "quake2-gwt-port/src" as the source folder
  1. Exclude com.google.gwt.corp.emul from the build path
  1. Make sure "Google Web Toolkit" is added as a Library
  1. Add missing stuff from the lib directory to the libraries as needed
  1. Make sure the output path is "quake2-gwt-port/war/WEB-INF/classes"


To recompile:
  * Click the little red toolbox icon in your toolbar (courtesy of the Google Plugin).

To run the server:
  * Select Run/Debug Configurations.
  * Select GwtQuakeServer and click Debug.

To debug the code in [dev mode](http://code.google.com/webtoolkit/gettingstarted.html):
  * This isn't quite working at the moment, because WebKit versions that support WebGL have just been too flaky.
  * It might work in Chrome/Windows (where there is a dev mode plugin), but we haven't tried it yet.