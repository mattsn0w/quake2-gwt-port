package jake2.tools;

import java.io.File;

/**
 * "Umbrella" for the Downloader, Unpacker etc.
 */
public class Installer {
  public static void main(String args[]) throws Throwable {
    Downloader.main(args);
    Unpak.main(new String[] {
        "raw" + File.separator + "baseq2",
        "war" + File.separator + "baseq2"
    });
  }
}
