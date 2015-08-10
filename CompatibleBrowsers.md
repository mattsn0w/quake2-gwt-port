# Webkit Nightly Builds (Mac OS X) #

  * Download the nightly build from http://nightly.webkit.org/
  * Type this into a terminal:   `defaults write com.apple.Safari WebKitWebGLEnabled -bool YES`

# Chrome Development Branch #

  * Windows:
    * Download Chromium: http://build.chromium.org/buildbot/continuous/win/LATEST/
    * Run `chrome.exe --enable-webgl`
  * Mac:
    * Download Chromium: http://build.chromium.org/buildbot/continuous/mac/LATEST/
    * Run `./Chromium.app/Contents/MacOS/Chromium --enable-webgl`
  * Linux:
    * Download Chromium: http://build.chromium.org/buildbot/continuous/linux/LATEST/
    * Run `./chromium --enable-webgl`

# Firefox 4.02b Nightly #

  * Install the nightly build from http://ftp.mozilla.org/pub/mozilla.org/firefox/nightly/latest-trunk/
  * Enable WebGL: Open the URL about:config and set the property webgl.enabled\_for\_all\_sites to true