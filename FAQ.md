### Where's the public demo link? ###

We are as yet unable to provide a public demo link. The Quake II code is GPL licensed, but the demo resources (textures, models, sounds, et al) are not, so we cannot simply upload them to a server. We are pursuing legitimate avenues to do so, though -- stay tuned.

### This mouse-look implementation is **terrible**! ###

Yes, it is. We've tried a number of different schemes for replicating mouse-look, but they all suffer from the same problem -- there's no way to recenter the mouse in the browser, so you eventually drag off the edge. This is something we will bring up on the standards lists (yes, there are security and user happiness implications, but we believe a middle-ground can be found).

In the meantime, you can at least recenter the mouse manually by right-dragging the cursor back to the center of the screen. Not perfect, but makes it playable.

### Why is the Chromium version significantly slower than `WebKit`? ###

This is related to the cost of copying frame buffers and Chromium's multi-
process architecture (see
[this document](http://www.chromium.org/developers/design-documents/multi-process-architecture) for details).  This issues will be fixed soon.

### Why is the Firefox version significantly slower than `WebKit`? ###

We think this is JavaScript performance issue. This bug has some information: https://bugzilla.mozilla.org/show_bug.cgi?id=557423

### Why does `WebKit` crash sometimes? ###

The WebGL implementation in `WebKit` is still quite new, as is its
implementation of WebSockets, among other things. We've seen different
behaviors on different nightly releases, so you can always try a new one or
roll back to see if that fixes things. Chromium seems to be more stable, but a
bit slower at present.

### Why is it so dark? ###

The original Quake II code performed gamma correction as it loaded textures on
the client. We were unable to efficiently preserve this when we switched to
WebGL, so textures are loaded with no correction. On some platforms this can
lead to too dark or too light scenes. This problem could be solved by either
performing gamma correction on the server, or moving it into the fragment
shader.

### Why's the code so ugly? ###

The original Quake II code was straight C, written in the mid-90's. The Jake2
port to Java was very direct, which preserved most of it's C-isms, including
giant bags of static methods where C functions used to be. In addition to this,
we hacked and slashed our way through the code to get it going and reasonably
optimized, but there was definitely some collateral damage along the way.

### What browser features does this rely on? ###

Just about every HTML5 buzzword you've heard for the past year or so:
  * Canvas/WebGL: For obvious reasons
  * `<audio>`: For sound
  * `<video>`: For in-game videos
  * Web Sockets: For client-server communication
  * Local Storage: For saving prefs. and saved games