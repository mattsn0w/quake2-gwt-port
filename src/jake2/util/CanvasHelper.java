package jake2.util;

public class CanvasHelper {

  public static String getCssColor(int color) {
    int a = (color >> 24) & 255;
    int r = (color >> 16) & 255;
    int g = (color >> 8) & 255;
    int b = color & 255;
    return getCssColor(r, g, b, a);
  }
  
  public static String getCssColor(float red, float green, float blue, float alpha) {
    return "rgba(" + (int) (red * 255) + "," + (int) (green * 255) + "," + (int) (blue * 255) + 
        "," + alpha+")"; 
  }

  public static String getCssColor(int red, int green, int blue, int alpha) {
    return "rgba(" + red + "," + green + "," + blue + "," + alpha / 255.0 + ")"; 
  }
}
