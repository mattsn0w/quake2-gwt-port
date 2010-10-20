/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.corp.gfx.client.canvas;

import jake2.render.LineDrawing;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.ImageElement;

/**
 * Rendering interface used to draw on a {@link CanvasElement}.
 * 
 * TODO(jgw): Because of the screwy overloaded values for strokeStyle and
 * fillStyle, there are currently no getters for these values. We could add
 * them, but would need logic to check the types before returning, which could
 * get a little tricky.
 * 
 * @see <a href="http://www.w3.org/html/wg/html5/#canvasrenderingcontext2d">W3C
 *      HTML 5 Specification</a>
 */
public class CanvasRenderingContext2D extends JavaScriptObject implements LineDrawing {

  /**
   * Specifies a pattern that repeats on both axes.
   * @see #createPattern(ImageElement, String)
   */
  public static final String REPETITION_REPEAT = "repeat";

  /**
   * Specifies a pattern that repeats on the x-axis.
   * @see #createPattern(ImageElement, String)
   */
  public static final String REPETITION_REPEAT_X = "repeat-x";

  /**
   * Specifies a pattern that repeats on the y-axis.
   * @see #createPattern(ImageElement, String)
   */
  public static final String REPETITION_REPEAT_Y = "repeat-y";

  /**
   * Specifies a pattern that repeats on both axes.
   * @see #createPattern(ImageElement, String)
   */
  public static final String REPETITION_NO_REPEAT = "no-repeat";

  /**
   * Specifies a "butt" line-cap style.
   * @see #setLineCap(String)
   */
  public static final String LINECAP_BUTT = "butt";

  /**
   * Specifies a "round" line-cap style.
   * @see #setLineCap(String)
   */
  public static final String LINECAP_ROUND = "round";

  /**
   * Specifies a "square" line-cap style.
   * @see #setLineCap(String)
   */
  public static final String LINECAP_SQUARE = "square";

  /**
   * Specifies a "round" line-join style.
   * @see #setLineJoin(String)
   */
  public static final String LINEJOIN_ROUND = "round";

  /**
   * Specifies a "bevel" line-join style.
   * @see #setLineJoin(String)
   */
  public static final String LINEJOIN_BEVEL = "bevel";

  /**
   * Specifies a "miter" line-join style.
   * @see #setLineJoin(String)
   */
  public static final String LINEJOIN_MITER = "miter";

  /**
   * A atop B. Display the source image wherever both images are opaque. Display
   * the destination image wherever the destination image is opaque but the
   * source image is transparent. Display transparency elsewhere.
   */
  public static final String COMPOSITE_SOURCE_ATOP = "source-atop";

  /**
   * A in B. Display the source image wherever both the source image and
   * destination image are opaque. Display transparency elsewhere.
   */
  public static final String COMPOSITE_SOURCE_IN = "source-in";

  /**
   * A out B. Display the source image wherever the source image is opaque and
   * the destination image is transparent. Display transparency elsewhere.
   */
  public static final String COMPOSITE_SOURCE_OUT = "source-out";

  /**
   * A over B. Display the source image wherever the source image is opaque.
   * Display the destination image elsewhere.
   */
  public static final String COMPOSITE_SOURCE_OVER = "source-over";

  /**
   * B atop A. Same as source-atop but using the destination image instead of
   * the source image and vice versa.
   */
  public static final String COMPOSITE_DESTINATION_ATOP = "destination-atop";

  /**
   * B in A. Same as source-in but using the destination image instead of the
   * source image and vice versa.
   */
  public static final String COMPOSITE_DESTINATION_IN = "destination-in";

  /**
   * B out A. Same as source-out but using the destination image instead of the
   * source image and vice versa.
   */
  public static final String COMPOSITE_DESTINATION_OUT = "destination-out";

  /**
   * B over A. Same as source-over but using the destination image instead of
   * the source image and vice versa.
   */
  public static final String COMPOSITE_DESINATION_OVER = "destination-over";

  /**
   * A plus B. Display the sum of the source image and destination image, with
   * color values approaching 1 as a limit.
   */
  public static final String COMPOSITE_LIGHTER = "lighter";

  /**
   * A (B is ignored). Display the source image instead of the destination
   * image.
   */
  public static final String COMPOSITE_COPY = "copy";

  /**
   * A xor B. Exclusive OR of the source image and destination image.
   */
  public static final String COMPOSITE_XOR = "xor";

  protected CanvasRenderingContext2D() {
  }

  /**
   * Draws an arc.
   */
  public final native void arc(float x, float y, float radius, float startAngle, float endAngle,
      boolean anticlockwise) /*-{
    this.arc(x, y, radius, startAngle, endAngle, anticlockwise);
  }-*/;

  /**
   * Draws an arc
   */
  public final native void arcTo(float x1, float y1, float x2, float y2, float radius) /*-{
    this.arcTo(x1, y1, x2, y2, radius);
  }-*/;

  /**
   * Begins a new path.
   */
  public final native void beginPath() /*-{
    this.beginPath();
  }-*/;

  /**
   * Draws a Bezier curve.
   */
  public final native void bezierCurveTo(float cp1x, float cp1y, float cp2x, float cp2y, float x,
      float y) /*-{
    this.bezierCurveTo(cp1x, cp1y, cp2x, cp2y, x, y);
  }-*/;

  /**
   * Clears a rectangle.
   */
  public final native void clearRect(float x, float y, float w, float h) /*-{
    this.clearRect(x, y, w, h);
  }-*/;

  /**
   * Creates a new clipping region from the current path.
   */
  public final native void clip() /*-{
    this.clip();
  }-*/;

  /**
   * Closes the current path.
   */
  public final native void closePath() /*-{
    this.closePath();
  }-*/;

  /**
   * Creates and image data object of the given size.
   */
  public final native ImageData createImageData(int w, int h) /*-{
    return this.createImageData(w, h);
  }-*/;
  
  /**
   * Creates an image data object of the same size as the given object.
   */
  public final native ImageData createImageData(ImageData imagedata) /*-{
    return this.createImageData(imagedata);
  }-*/;

  /**
   * Creates a linear gradient.
   */
  public final native CanvasGradient createLinearGradient(float x0, float y0, float x1, float y1) /*-{
    return this.createLinearGradient(x0, y0, x1, y1);
  }-*/;

  /**
   * Creates a pattern from another canvas.
   */
  public final native CanvasPattern createPattern(CanvasElement image, String repetition) /*-{
    return this.createPattern(image, repetition);
  }-*/;

  /**
   * Creates a pattern from an image.
   */
  public final native CanvasPattern createPattern(ImageElement image, String repetition) /*-{
    return this.createPattern(image, repetition);
  }-*/;

  /**
   * Creates a radial gradient.
   */
  public final native CanvasGradient createRadialGradient(float x0, float y0, float r0, float x1,
      float y1, float r1) /*-{
    return this.createRadialGradient(x0, y0, r0, x1, y1, r1);
  }-*/;

  /**
   * Draws an image.
   */
  public final native void drawImage(CanvasElement image, float dx, float dy) /*-{
    this.drawImage(image, dx, dy);
  }-*/;

  /**
   * Draws a scaled image.
   */
  public final native void drawImage(CanvasElement image, float dx, float dy, float dw, float dh) /*-{
    this.drawImage(image, dx, dy, dw, dh);
  }-*/;

  /**
   * Draws a scaled subset of an image.
   */
  public final native void drawImage(CanvasElement image, float sx, float sy, float sw, float sh,
      float dx, float dy, float dw, float dh) /*-{
    this.drawImage(image, sx, sy, sw, sh, dx, dy, dw, dh);
  }-*/;

  /**
   * Draws an image.
   */
  public final native void drawImage(ImageElement image, float dx, float dy) /*-{
    this.drawImage(image, dx, dy);
  }-*/;

  /**
   * Draws a scaled image.
   */
  public final native void drawImage(ImageElement image, float dx, float dy, float dw, float dh) /*-{
    this.drawImage(image, dx, dy, dw, dh);
  }-*/;

  /**
   * Draws a scaled subset of an image.
   */
  public final native void drawImage(ImageElement image, float sx, float sy, float sw, float sh,
      float dx, float dy, float dw, float dh) /*-{
    this.drawImage(image, sx, sy, sw, sh, dx, dy, dw, dh);
  }-*/;

  /**
   * Fills the current path.
   */
  public final native void fill() /*-{
    this.fill();
  }-*/;

  /**
   * Fills a rectangle.
   */
  public final native void fillRect(float x, float y, float w, float h) /*-{
    this.fillRect(x, y, w, h);
  }-*/;

  /**
   * Draw text.
   */
  public final native void fillText(String text, float x, float y) /*-{
    this.fillText(text, x, y);
  }-*/;

  /**
   * Draw text squeezed into the given max width.
   */
  public final native void fillText(String text, float x, float y, float maxWidth) /*-{
    this.fillText(text, x, y, maxWidth);
  }-*/;
  
  /**
   * Gets this context's canvas.
   */
  public final native CanvasElement getCanvas() /*-{
    return this.canvas;
  }-*/;

  /**
   * Gets this context's font.
   */
  public final native String getFont() /*-{
    return this.font;
  }-*/;
  
  /**
   * Gets the global alpha value.
   */
  public final native float getGlobalAlpha() /*-{
    return this.globalAlpha;
  }-*/;

  /**
   * Gets the global composite operation.
   */
  public final native String getGlobalCompositeOperation() /*-{
    return this.globalCompositeOperation;
  }-*/;

  /**
   * Returns an image data object for the screen area denoted by
   * sx, sy, sw and sh.
   */
  public final native ImageData getImageData(float sx, float sy, float sw, float sh) /*-{
    return this.getImageData(sx, sy, sw, sh);
  }-*/;

  
  /**
   * Gets the current line-cap style.
   */
  public final native String getLineCap() /*-{
    return this.lineCap;
  }-*/;

  /**
   * Gets the current line-join style.
   */
  public final native String getLineJoin() /*-{
    return this.lineJoin;
  }-*/;

  /**
   * Gets the current line-width.
   */
  public final native float getLineWidth() /*-{
    return this.lineWidth;
  }-*/;

  /**
   * Gets the current miter-limit.
   */
  public final native float getMiterLimit() /*-{
    return this.miterLimit;
  }-*/;

  /**
   * Gets the current shadow-blur.
   */
  public final native float getShadowBlur() /*-{
    return this.shadowBlur;
  }-*/;

  /**
   * Gets the current shadow color.
   */
  public final native String getShadowColor() /*-{
    return this.shadowColor;
  }-*/;

  /**
   * Gets the current x-shadow-offset.
   */
  public final native float getShadowOffsetX() /*-{
    return this.shadowOffsetX;
  }-*/;

  /**
   * Gets the current y-shadow-offset.
   */
  public final native float getShadowOffsetY() /*-{
    return this.shadowOffsetY;
  }-*/;
  
  /**
   * Gets the current text align.
   */
  public final native String getTextAlign() /*-{
    return this.textAlign;
  }-*/;

  /**
   * Gets the current text baseline.
   */
  public final native String getTextBaseline() /*-{
    return this.textBaseline;
  }-*/;
  
  /**
   * Draws a line.
   */
  public final native void lineTo(float x, float y) /*-{
    this.lineTo(x, y);
  }-*/;

  /**
   * Returns the metrics for the given text.
   */
  public final native TextMetrics measureText(String text) /*-{
    return this.measureText(text);
  }-*/;
  
  /**
   * Sets the current path position.
   */
  public final native void moveTo(float x, float y) /*-{
    this.moveTo(x, y);
  }-*/;

  /**
   * Write the given image data to the given screen position.
   * 
   * @param imagedata The image data to be written to the screen.
   * @param x the x-position of the image data.
   * @param y the y-position of the image data.
   */
  public final native void putImageData(ImageData imagedata, float x, float y) /*-{
    return this.putImageData(imagedata, x, y);
  }-*/;

  /**
   * Draws a quadratic curve.
   */
  public final native void quadraticCurveTo(float cpx, float cpy, float x, float y) /*-{
    this.quadraticCurveTo(cpx, cpy, x, y);
  }-*/;

  /**
   * Draws a rectangle.
   */
  public final native void rect(float x, float y, float w, float h) /*-{
    this.rect(x, y, w, h);
  }-*/;

  /**
   * Restores the context's state.
   */
  public final native void restore() /*-{
    this.restore();
  }-*/;

  /**
   * Applies rotation to the current transform.
   */
  public final native void rotate(float angle) /*-{
    this.rotate(angle);
  }-*/;

  /**
   * Saves the context's state.
   */
  public final native void save() /*-{
    this.save();
  }-*/;

  /**
   * Applies scale to the current transform.
   */
  public final native void scale(float x, float y) /*-{
    this.scale(x, y);
  }-*/;

  /**
   * Sets the fill style to a color.
   */
  public final native void setFillStyleColor(String fillStyle) /*-{
    this.fillStyle = fillStyle;
  }-*/;

  /**
   * Sets the fill style to a gradient.
   */
  public final native void setFillStyleGradient(CanvasGradient fillStyle) /*-{
    this.fillStyle = fillStyle;
  }-*/;

  /**
   * Sets the fill style to a pattern.
   */
  public final native void setFillStylePattern(CanvasPattern fillStyle) /*-{
    this.fillStyle = fillStyle;
  }-*/;

  /**
   * Sets the font.
   */
  public final native void setFont(String f) /*-{
    this.font = f;
  }-*/;

  
  /**
   * Sets the global alpha value.
   */
  public final native void setGlobalAlpha(float alpha) /*-{
    this.globalAlpha = alpha;
  }-*/;

  /**
   * Sets the global composite operation.
   */
  public final native void setGlobalCompositeOperation(String globalCompositeOperation) /*-{
    this.globalCompositeOperation = globalCompositeOperation;
  }-*/;

  /**
   * Sets the line-cap style.
   */
  public final native void setLineCap(String lineCap) /*-{
    this.lineCap = lineCap;
  }-*/;

  /**
   * Sets the line-join style.
   */
  public final native void setLineJoin(String lineJoin) /*-{
    this.lineJoin = lineJoin;
  }-*/;

  /**
   * Sets the line-width.
   */
  public final native void setLineWidth(float lineWidth) /*-{
    this.lineWidth = lineWidth;
  }-*/;

  /**
   * Sets the miter-limit.
   */
  public final native void setMiterLimit(float miterLimit) /*-{
    this.miterLimit = miterLimit;
  }-*/;

  /**
   * Sets the shadow-blur.
   */
  public final native void setShadowBlur(float shadowBlur) /*-{
    this.shadowBlur = shadowBlur;
  }-*/;

  /**
   * Sets the shadow-color.
   */
  public final native void setShadowColor(String shadowColor) /*-{
    this.shadowColor = shadowColor;
  }-*/;

  /**
   * Sets the x-shadow-offset.
   */
  public final native void setShadowOffsetX(float shadowOffsetX) /*-{
    this.shadowOffsetX = shadowOffsetX;
  }-*/;

  /**
   * Sets the y-shadow-offset.
   */
  public final native void setShadowOffsetY(float shadowOffsetY) /*-{
    this.shadowOffsetY = shadowOffsetY;
  }-*/;

  /**
   * Sets the stroke style to a color.
   */
  public final native void setStrokeStyleColor(String strokeStyle) /*-{
    this.strokeStyle = strokeStyle;
  }-*/;

  /**
   * Sets the stroke style to a gradient.
   */
  public final native void setStrokeStyleGradient(CanvasGradient strokeStyle) /*-{
    this.strokeStyle = strokeStyle;
  }-*/;

  /**
   * Sets the stroke style to a pattern.
   */
  public final native void setStrokeStylePattern(CanvasPattern strokeStyle) /*-{
    this.strokeStyle = strokeStyle;
  }-*/;

  /**
   * Sets the 2D transformation matrix.
   */
  public final native void setTransform(float m11, float m12, float m21, float m22, float dx,
      float dy) /*-{
    this.setTransform(m11, m12, m21, m22, dx, dy);
  }-*/;

  /** 
   * Sets the text alignment.
   */
  public final native void setTextAlign(String align) /*-{
    this.textAlign = align
  }-*/;

  /**
   * Sets the text baseline.
   */
  public final native void setTextBaseline(String baseline) /*-{
    this.textBaseline = baseline
  }-*/;

  /**
   * Draws the current path with the current stroke style.
   */
  public final native void stroke() /*-{
    this.stroke();
  }-*/;

  /**
   * Draws a rectangle with the current stroke style.
   */
  public final native void strokeRect(float x, float y, float w, float h) /*-{
    this.strokeRect(x, y, w, h);
  }-*/;

  /**
   * Draws the text outline.
   */
  public final native void strokeText(String text, float x, float y) /*-{
    this.strokeText(text, x, y);
  }-*/;

  /**
   * Draws the text outline, squeezing the text into the given max width by compressing the font.
   */
  public final native void strokeText(String text, float x, float y, float maxWith) /*-{
    this.strokeText(text, x, y, maxWidth);
  }-*/;
  
  /**
   * Multiplies the current transform by the given matrix.
   */
  public final native void transform(float m11, float m12, float m21, float m22, float dx, float dy) /*-{
    this.transform(m11, m12, m21, m22, dx, dy);
  }-*/;

  /**
   * Applies translation to the current transform.
   */
  public final native void translate(float x, float y) /*-{
    this.translate(x, y);
  }-*/;
}
