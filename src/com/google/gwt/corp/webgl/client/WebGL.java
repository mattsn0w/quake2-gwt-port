/*
 * Copyright 2010 Google Inc.
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
package com.google.gwt.corp.webgl.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

/**
 * WARNING: This is work in progress. 
 * 
 * <ul>
 * <li>This does not work in any currently shipping browser.</li>
 * <li>The API may change significantly along with the WebGL spec evolving
 *     (the first public draft is planned for 2010).</li>
 * <li>Several methods may be missing -- I have just added them as needed.
 * </ul>
 * 
 * GWT wrapper for a WebGL context. In contrast to WebGL JS API, all identifiers are prefixed with 
 * gl or GL_ for consistency with other Java OpenGL APIs here. So in theory, at some point this 
 * class could implement one of the Java GL interfaces (ideally, an update of JSR 239 covering 
 * OpenGL ES 2.0).
 *
 * TODO(haustein) Auto-generate from the IDL file...
 *
 * @author Stefan Haustein
 */
public class WebGL extends JavaScriptObject {

  public static final int GL_ACTIVE_TEXTURE = 34016;
  public static final int GL_ADD = 260;
  public static final int GL_ADD_SIGNED = 34164;
  public static final int GL_ALIASED_LINE_WIDTH_RANGE = 33902;
  public static final int GL_ALIASED_POINT_SIZE_RANGE = 33901;
  public static final int GL_ALPHA = 6406;
  public static final int GL_ALPHA_BITS = 3413;
  public static final int GL_ALPHA_SCALE = 3356;
  public static final int GL_ALPHA_TEST = 3008;
  public static final int GL_ALPHA_TEST_FUNC = 3009;
  public static final int GL_ALPHA_TEST_REF = 3010;
  public static final int GL_ALWAYS = 519;
  public static final int GL_AMBIENT = 4608;
  public static final int GL_AMBIENT_AND_DIFFUSE = 5634;
  public static final int GL_AND = 5377;
  public static final int GL_AND_INVERTED = 5380;
  public static final int GL_AND_REVERSE = 5378;
  public static final int GL_ARRAY_BUFFER = 34962;
  public static final int GL_ARRAY_BUFFER_BINDING = 34964;
  public static final int GL_BACK = 1029;
  public static final int GL_BLEND = 3042;
  public static final int GL_BLEND_DST = 3040;
  public static final int GL_BLEND_SRC = 3041;
  public static final int GL_BLUE_BITS = 3412;
  public static final int GL_BUFFER_ACCESS = 35003;
  public static final int GL_BUFFER_SIZE = 34660;
  public static final int GL_BUFFER_USAGE = 34661;
  public static final int GL_BYTE = 5120;
  public static final int GL_CCW = 2305;
  public static final int GL_CLAMP_TO_EDGE = 33071;
  public static final int GL_CLEAR = 5376;
  public static final int GL_CLIENT_ACTIVE_TEXTURE = 34017;
  public static final int GL_CLIP_PLANE0 = 12288;
  public static final int GL_CLIP_PLANE1 = 12289;
  public static final int GL_CLIP_PLANE2 = 12290;
  public static final int GL_CLIP_PLANE3 = 12291;
  public static final int GL_CLIP_PLANE4 = 12292;
  public static final int GL_CLIP_PLANE5 = 12293;
  public static final int GL_COLOR_ARRAY = 32886;
  public static final int GL_COLOR_ARRAY_BUFFER_BINDING = 34968;
  public static final int GL_COLOR_ARRAY_SIZE = 32897;
  public static final int GL_COLOR_ARRAY_STRIDE = 32899;
  public static final int GL_COLOR_ARRAY_TYPE = 32898;
  public static final int GL_COLOR_BUFFER_BIT = 16384;
  public static final int GL_COLOR_CLEAR_VALUE = 3106;
  public static final int GL_COLOR_LOGIC_OP = 3058;
  public static final int GL_COLOR_MATERIAL = 2903;
  public static final int GL_COLOR_WRITEMASK = 3107;
  public static final int GL_COMBINE = 34160;
  public static final int GL_COMBINE_ALPHA = 34162;
  public static final int GL_COMBINE_RGB = 34161;
  public static final int GL_COMPRESSED_TEXTURE_FORMATS = 34467;
  public static final int GL_CONSTANT = 34166;
  public static final int GL_CONSTANT_ATTENUATION = 4615;
  public static final int GL_COORD_REPLACE_OES = 34914;
  public static final int GL_COPY = 5379;
  public static final int GL_COPY_INVERTED = 5388;
  public static final int GL_CULL_FACE = 2884;
  public static final int GL_CULL_FACE_MODE = 2885;
  public static final int GL_CURRENT_COLOR = 2816;
  public static final int GL_CURRENT_NORMAL = 2818;
  public static final int GL_CURRENT_TEXTURE_COORDS = 2819;
  public static final int GL_CW = 2304;
  public static final int GL_DECAL = 8449;
  public static final int GL_DECR = 7683;
  public static final int GL_DEPTH_BITS = 3414;
  public static final int GL_DEPTH_BUFFER_BIT = 256;
  public static final int GL_DEPTH_CLEAR_VALUE = 2931;
  public static final int GL_DEPTH_FUNC = 2932;
  public static final int GL_DEPTH_RANGE = 2928;
  public static final int GL_DEPTH_TEST = 2929;
  public static final int GL_DEPTH_WRITEMASK = 2930;
  public static final int GL_DIFFUSE = 4609;
  public static final int GL_DITHER = 3024;
  public static final int GL_DONT_CARE = 4352;
  public static final int GL_DOT3_RGB = 34478;
  public static final int GL_DOT3_RGBA = 34479;
  public static final int GL_DST_ALPHA = 772;
  public static final int GL_DST_COLOR = 774;
  public static final int GL_DYNAMIC_DRAW = 35048;
  public static final int GL_ELEMENT_ARRAY_BUFFER = 34963;
  public static final int GL_ELEMENT_ARRAY_BUFFER_BINDING = 34965;
  public static final int GL_EMISSION = 5632;
  public static final int GL_EQUAL = 514;
  public static final int GL_EQUIV = 5385;
  public static final int GL_EXP = 2048;
  public static final int GL_EXP2 = 2049;
  public static final int GL_EXTENSIONS = 7939;
  public static final int GL_FALSE = 0;
  public static final int GL_FASTEST = 4353;
  public static final int GL_FLAT = 7424;
  public static final int GL_FLOAT = 5126;
  public static final int GL_FOG = 2912;
  public static final int GL_FOG_COLOR = 2918;
  public static final int GL_FOG_DENSITY = 2914;
  public static final int GL_FOG_END = 2916;
  public static final int GL_FOG_HINT = 3156;
  public static final int GL_FOG_MODE = 2917;
  public static final int GL_FOG_START = 2915;
  public static final int GL_FRONT = 1028;
  public static final int GL_FRONT_AND_BACK = 1032;
  public static final int GL_FRONT_FACE = 2886;
  public static final int GL_GENERATE_MIPMAP = 33169;
  public static final int GL_GENERATE_MIPMAP_HINT = 33170;
  public static final int GL_GEQUAL = 518;
  public static final int GL_GREATER = 516;
  public static final int GL_GREEN_BITS = 3411;
  public static final int GL_IMPLEMENTATION_COLOR_READ_FORMAT_OES = 35739;
  public static final int GL_IMPLEMENTATION_COLOR_READ_TYPE_OES = 35738;
  public static final int GL_INCR = 7682;
  public static final int GL_INT = 0x1404;
  public static final int GL_INTERPOLATE = 34165;
  public static final int GL_INVALID_ENUM = 1280;
  public static final int GL_INVALID_OPERATION = 1282;
  public static final int GL_INVALID_VALUE = 1281;
  public static final int GL_INVERT = 5386;
  public static final int GL_KEEP = 7680;
  public static final int GL_LEQUAL = 515;
  public static final int GL_LESS = 513;
  public static final int GL_LIGHT_MODEL_AMBIENT = 2899;
  public static final int GL_LIGHT_MODEL_TWO_SIDE = 2898;
  public static final int GL_LIGHT0 = 16384;
  public static final int GL_LIGHT1 = 16385;
  public static final int GL_LIGHT2 = 16386;
  public static final int GL_LIGHT3 = 16387;
  public static final int GL_LIGHT4 = 16388;
  public static final int GL_LIGHT5 = 16389;
  public static final int GL_LIGHT6 = 16390;
  public static final int GL_LIGHT7 = 16391;
  public static final int GL_LIGHTING = 2896;
  public static final int GL_LINE_LOOP = 2;
  public static final int GL_LINE_SMOOTH = 2848;
  public static final int GL_LINE_SMOOTH_HINT = 3154;
  public static final int GL_LINE_STRIP = 3;
  public static final int GL_LINE_WIDTH = 2849;
  public static final int GL_LINEAR = 9729;
  public static final int GL_LINEAR_ATTENUATION = 4616;
  public static final int GL_LINEAR_MIPMAP_LINEAR = 9987;
  public static final int GL_LINEAR_MIPMAP_NEAREST = 9985;
  public static final int GL_LINES = 1;
  public static final int GL_LOGIC_OP_MODE = 3056;
  public static final int GL_LUMINANCE = 6409;
  public static final int GL_LUMINANCE_ALPHA = 6410;
  public static final int GL_MATRIX_MODE = 2976;
  public static final int GL_MAX_CLIP_PLANES = 3378;
  public static final int GL_MAX_ELEMENTS_INDICES = 33001;
  public static final int GL_MAX_ELEMENTS_VERTICES = 33000;
  public static final int GL_MAX_LIGHTS = 3377;
  public static final int GL_MAX_MODELVIEW_STACK_DEPTH = 3382;
  public static final int GL_MAX_PROJECTION_STACK_DEPTH = 3384;
  public static final int GL_MAX_TEXTURE_SIZE = 3379;
  public static final int GL_MAX_TEXTURE_STACK_DEPTH = 3385;
  public static final int GL_MAX_TEXTURE_UNITS = 34018;
  public static final int GL_MAX_VIEWPORT_DIMS = 3386;
  public static final int GL_MODELVIEW = 5888;
  public static final int GL_MODELVIEW_MATRIX = 2982;
  public static final int GL_MODELVIEW_MATRIX_FLOAT_AS_INT_BITS_OES = 35213;
  public static final int GL_MODELVIEW_STACK_DEPTH = 2979;
  public static final int GL_MODULATE = 8448;
  public static final int GL_MULTISAMPLE = 32925;
  public static final int GL_NAND = 5390;
  public static final int GL_NEAREST = 9728;
  public static final int GL_NEAREST_MIPMAP_LINEAR = 9986;
  public static final int GL_NEAREST_MIPMAP_NEAREST = 9984;
  public static final int GL_NEVER = 512;
  public static final int GL_NICEST = 4354;
  public static final int GL_NO_ERROR = 0;
  public static final int GL_NOOP = 5381;
  public static final int GL_NOR = 5384;
  public static final int GL_NORMAL_ARRAY = 32885;
  public static final int GL_NORMAL_ARRAY_BUFFER_BINDING = 34967;
  public static final int GL_NORMAL_ARRAY_STRIDE = 32895;
  public static final int GL_NORMAL_ARRAY_TYPE = 32894;
  public static final int GL_NORMALIZE = 2977;
  public static final int GL_NOTEQUAL = 517;
  public static final int GL_NUM_COMPRESSED_TEXTURE_FORMATS = 34466;
  public static final int GL_OES_byte_coordinates = 1;
  public static final int GL_OES_compressed_paletted_texture = 1;
  public static final int GL_OES_fixed_point = 1;
  public static final int GL_OES_matrix_get = 1;
  public static final int GL_OES_point_size_array = 1;
  public static final int GL_OES_point_sprite = 1;
  public static final int GL_OES_read_format = 1;
  public static final int GL_OES_single_precision = 1;
  public static final int GL_OES_VERSION_1_0 = 1;
  public static final int GL_OES_VERSION_1_1 = 1;
  public static final int GL_ONE = 1;
  public static final int GL_ONE_MINUS_DST_ALPHA = 773;
  public static final int GL_ONE_MINUS_DST_COLOR = 775;
  public static final int GL_ONE_MINUS_SRC_ALPHA = 771;
  public static final int GL_ONE_MINUS_SRC_COLOR = 769;
  public static final int GL_OPERAND0_ALPHA = 34200;
  public static final int GL_OPERAND0_RGB = 34192;
  public static final int GL_OPERAND1_ALPHA = 34201;
  public static final int GL_OPERAND1_RGB = 34193;
  public static final int GL_OPERAND2_ALPHA = 34202;
  public static final int GL_OPERAND2_RGB = 34194;
  public static final int GL_OR = 5383;
  public static final int GL_OR_INVERTED = 5389;
  public static final int GL_OR_REVERSE = 5387;
  public static final int GL_OUT_OF_MEMORY = 1285;
  public static final int GL_PACK_ALIGNMENT = 3333;
  public static final int GL_PALETTE4_R5_G6_B5_OES = 35730;
  public static final int GL_PALETTE4_RGB5_A1_OES = 35732;
  public static final int GL_PALETTE4_RGB8_OES = 35728;
  public static final int GL_PALETTE4_RGBA4_OES = 35731;
  public static final int GL_PALETTE4_RGBA8_OES = 35729;
  public static final int GL_PALETTE8_R5_G6_B5_OES = 35735;
  public static final int GL_PALETTE8_RGB5_A1_OES = 35737;
  public static final int GL_PALETTE8_RGB8_OES = 35733;
  public static final int GL_PALETTE8_RGBA4_OES = 35736;
  public static final int GL_PALETTE8_RGBA8_OES = 35734;
  public static final int GL_PERSPECTIVE_CORRECTION_HINT = 3152;
  public static final int GL_POINT_DISTANCE_ATTENUATION = 33065;
  public static final int GL_POINT_FADE_THRESHOLD_SIZE = 33064;
  public static final int GL_POINT_SIZE = 2833;
  public static final int GL_POINT_SIZE_ARRAY_BUFFER_BINDING_OES = 35743;
  public static final int GL_POINT_SIZE_ARRAY_OES = 35740;
  public static final int GL_POINT_SIZE_ARRAY_STRIDE_OES = 35211;
  public static final int GL_POINT_SIZE_ARRAY_TYPE_OES = 35210;
  public static final int GL_POINT_SIZE_MAX = 33063;
  public static final int GL_POINT_SIZE_MIN = 33062;
  public static final int GL_POINT_SMOOTH = 2832;
  public static final int GL_POINT_SMOOTH_HINT = 3153;
  public static final int GL_POINT_SPRITE_OES = 34913;
  public static final int GL_POINTS = 0;
  public static final int GL_POLYGON_OFFSET_FACTOR = 32824;
  public static final int GL_POLYGON_OFFSET_FILL = 32823;
  public static final int GL_POLYGON_OFFSET_UNITS = 10752;
  public static final int GL_POLYGON_SMOOTH_HINT = 3155;
  public static final int GL_POSITION = 4611;
  public static final int GL_PREVIOUS = 34168;
  public static final int GL_PRIMARY_COLOR = 34167;
  public static final int GL_PROJECTION = 5889;
  public static final int GL_PROJECTION_MATRIX = 2983;
  public static final int GL_PROJECTION_MATRIX_FLOAT_AS_INT_BITS_OES = 35214;
  public static final int GL_PROJECTION_STACK_DEPTH = 2980;
  public static final int GL_QUADRATIC_ATTENUATION = 4617;
  public static final int GL_RED_BITS = 3410;
  public static final int GL_RENDERER = 7937;
  public static final int GL_REPEAT = 10497;
  public static final int GL_REPLACE = 7681;
  public static final int GL_RESCALE_NORMAL = 32826;
  public static final int GL_RGB = 6407;
  public static final int GL_RGB_SCALE = 34163;
  public static final int GL_RGBA = 6408;
  public static final int GL_SAMPLE_ALPHA_TO_COVERAGE = 32926;
  public static final int GL_SAMPLE_ALPHA_TO_ONE = 32927;
  public static final int GL_SAMPLE_BUFFERS = 32936;
  public static final int GL_SAMPLE_COVERAGE = 32928;
  public static final int GL_SAMPLE_COVERAGE_INVERT = 32939;
  public static final int GL_SAMPLE_COVERAGE_VALUE = 32938;
  public static final int GL_SAMPLES = 32937;
  public static final int GL_SCISSOR_BOX = 3088;
  public static final int GL_SCISSOR_TEST = 3089;
  public static final int GL_SET = 5391;
  public static final int GL_SHADE_MODEL = 2900;
  public static final int GL_SHININESS = 5633;
  public static final int GL_SHORT = 5122;
  public static final int GL_SMOOTH = 7425;
  public static final int GL_SMOOTH_LINE_WIDTH_RANGE = 2850;
  public static final int GL_SMOOTH_POINT_SIZE_RANGE = 2834;
  public static final int GL_SPECULAR = 4610;
  public static final int GL_SPOT_CUTOFF = 4614;
  public static final int GL_SPOT_DIRECTION = 4612;
  public static final int GL_SPOT_EXPONENT = 4613;
  public static final int GL_SRC_ALPHA = 770;
  public static final int GL_SRC_ALPHA_SATURATE = 776;
  public static final int GL_SRC_COLOR = 768;
  public static final int GL_SRC0_ALPHA = 34184;
  public static final int GL_SRC0_RGB = 34176;
  public static final int GL_SRC1_ALPHA = 34185;
  public static final int GL_SRC1_RGB = 34177;
  public static final int GL_SRC2_ALPHA = 34186;
  public static final int GL_SRC2_RGB = 34178;
  public static final int GL_STACK_OVERFLOW = 1283;
  public static final int GL_STACK_UNDERFLOW = 1284;
  public static final int GL_STATIC_DRAW = 35044;
  public static final int GL_STENCIL_BITS = 3415;
  public static final int GL_STENCIL_BUFFER_BIT = 1024;
  public static final int GL_STENCIL_CLEAR_VALUE = 2961;
  public static final int GL_STENCIL_FAIL = 2964;
  public static final int GL_STENCIL_FUNC = 2962;
  public static final int GL_STENCIL_PASS_DEPTH_FAIL = 2965;
  public static final int GL_STENCIL_PASS_DEPTH_PASS = 2966;
  public static final int GL_STENCIL_REF = 2967;
  public static final int GL_STENCIL_TEST = 2960;
  public static final int GL_STENCIL_VALUE_MASK = 2963;
  public static final int GL_STENCIL_WRITEMASK = 2968;
  public static final int GL_STREAM_DRAW                    = 0x88E0;
  public static final int GL_SUBPIXEL_BITS = 3408;
  public static final int GL_SUBTRACT = 34023;
  public static final int GL_TEXTURE = 5890;
  public static final int GL_TEXTURE_2D = 3553;
  public static final int GL_TEXTURE_BINDING_2D = 32873;
  public static final int GL_TEXTURE_COORD_ARRAY = 32888;
  public static final int GL_TEXTURE_COORD_ARRAY_BUFFER_BINDING = 34970;
  public static final int GL_TEXTURE_COORD_ARRAY_SIZE = 32904;
  public static final int GL_TEXTURE_COORD_ARRAY_STRIDE = 32906;
  public static final int GL_TEXTURE_COORD_ARRAY_TYPE = 32905;
  public static final int GL_TEXTURE_ENV = 8960;
  public static final int GL_TEXTURE_ENV_COLOR = 8705;
  public static final int GL_TEXTURE_ENV_MODE = 8704;
  public static final int GL_TEXTURE_MAG_FILTER = 10240;
  public static final int GL_TEXTURE_MATRIX = 2984;
  public static final int GL_TEXTURE_MATRIX_FLOAT_AS_INT_BITS_OES = 35215;
  public static final int GL_TEXTURE_MIN_FILTER = 10241;
  public static final int GL_TEXTURE_STACK_DEPTH = 2981;
  public static final int GL_TEXTURE_WRAP_S = 10242;
  public static final int GL_TEXTURE_WRAP_T = 10243;
  public static final int GL_TEXTURE0 = 33984;
  public static final int GL_TEXTURE1 = 33985;
  public static final int GL_TEXTURE10 = 33994;
  public static final int GL_TEXTURE11 = 33995;
  public static final int GL_TEXTURE12 = 33996;
  public static final int GL_TEXTURE13 = 33997;
  public static final int GL_TEXTURE14 = 33998;
  public static final int GL_TEXTURE15 = 33999;
  public static final int GL_TEXTURE16 = 34000;
  public static final int GL_TEXTURE17 = 34001;
  public static final int GL_TEXTURE18 = 34002;
  public static final int GL_TEXTURE19 = 34003;
  public static final int GL_TEXTURE2 = 33986;
  public static final int GL_TEXTURE20 = 34004;
  public static final int GL_TEXTURE21 = 34005;
  public static final int GL_TEXTURE22 = 34006;
  public static final int GL_TEXTURE23 = 34007;
  public static final int GL_TEXTURE24 = 34008;
  public static final int GL_TEXTURE25 = 34009;
  public static final int GL_TEXTURE26 = 34010;
  public static final int GL_TEXTURE27 = 34011;
  public static final int GL_TEXTURE28 = 34012;
  public static final int GL_TEXTURE29 = 34013;
  public static final int GL_TEXTURE3 = 33987;
  public static final int GL_TEXTURE30 = 34014;
  public static final int GL_TEXTURE31 = 34015;
  public static final int GL_TEXTURE4 = 33988;
  public static final int GL_TEXTURE5 = 33989;
  public static final int GL_TEXTURE6 = 33990;
  public static final int GL_TEXTURE7 = 33991;
  public static final int GL_TEXTURE8 = 33992;
  public static final int GL_TEXTURE9 = 33993;
  public static final int GL_TRIANGLE_FAN = 6;
  public static final int GL_TRIANGLE_STRIP = 5;
  public static final int GL_TRIANGLES = 4;
  public static final int GL_TRUE = 1;
  public static final int GL_UNPACK_ALIGNMENT = 3317;
  public static final int GL_UNSIGNED_BYTE = 5121;
  public static final int GL_UNSIGNED_SHORT = 5123;
  public static final int GL_UNSIGNED_SHORT_4_4_4_4 = 32819;
  public static final int GL_UNSIGNED_SHORT_5_5_5_1 = 32820;
  public static final int GL_UNSIGNED_SHORT_5_6_5 = 33635;
  public static final int GL_VENDOR = 7936;
  public static final int GL_VERSION = 7938;
  public static final int GL_VERTEX_ARRAY = 32884;
  public static final int GL_VERTEX_ARRAY_BUFFER_BINDING = 34966;
  public static final int GL_VERTEX_ARRAY_SIZE = 32890;
  public static final int GL_VERTEX_ARRAY_STRIDE = 32892;
  public static final int GL_VERTEX_ARRAY_TYPE = 32891;
  public static final int GL_VIEWPORT = 2978;
  public static final int GL_WRITE_ONLY = 35001;
  public static final int GL_XOR = 5382;
  public static final int GL_ZERO = 0;

  // 2.0 constants

  public static final int GL_VERTEX_SHADER = 35633;
  public static final int GL_FRAGMENT_SHADER = 35632;
  public static final int GL_COMPILE_STATUS = 35713;
  public static final int GL_LINK_STATUS = 35714;

  protected WebGL() {
  }

  /**
   * Returns a WebGL context for the given canvas element. Returns null if no 3d context is 
   * available.
   */
  public static native WebGL getContext(Element canvas, JavaScriptObject options) /*-{
    var names = ["webgl", "moz-webgl", "webkit-webgl", "webkit-3d"];
    for (var i = 0; i < names.length; i++) {
      var ctx = canvas.getContext(names[i], options);
      if (ctx != null) {
        return ctx;
      }
    }
    return null;
  }-*/;

  public native final void glActiveTexture(int texture)  /*-{
    this.activeTexture(texture);
  }-*/;

  public final native void glAttachShader(Program program, Shader shader) /*-{
    this.attachShader(program, shader);
  }-*/;

  public final native void glBindAttribLocation(Program program, int i, String name) /*-{
    this.bindAttribLocation(program, i, name);
  }-*/;
  
  public native final void glBindBuffer(int type, Buffer buf) /*-{
    this.bindBuffer(type, buf);
   }-*/;

  public final native void glBindTexture(int t, Texture i) /*-{
    this.bindTexture(t, i);
  }-*/;

  public native final void glBufferData(int target, WebGLArray data, int usage) /*-{
    this.bufferData(target, data, usage);
  }-*/;

  public native final Buffer glCreateBuffer()  /*-{
    return this.createBuffer();
  }-*/;

  public native final Texture glCreateTexture()  /*-{
    return this.createTexture();
  }-*/;

  
  public final native void glBlendFunc(int a, int b) /*-{
    this.blendFunc(a, b);
  }-*/;

  public native final void glClear(int mask) /*-{
    this.clear(mask);
  }-*/;
  
  public final native void glClearColor(float f, float g, float h, float i) /*-{
    this.clearColor(f, g, h, i);
  }-*/;

  public final native void glCompileShader(Shader shader) /*-{
    this.compileShader(shader);
  }-*/;
  
  public final native Program glCreateProgram() /*-{
    return this.createProgram();
  }-*/;

  public native final Shader glCreateShader(int type) /*-{
    return this.createShader(type);
  }-*/;

  public final native void glCullFace(int c)/*-{
    this.cullFace(c);
  }-*/;
  
  public native final void glDeleteBuffer(Buffer buffer) /*-{
    this.deleteBuffer(buffer);
   }-*/;

  public native final void glDeleteProgram(Program program)/*-{
    this.deleteProgram(program);
  }-*/;

  public native final int glDeleteShader(Shader shader) /*-{
    return this.deleteShader(shader);
  }-*/;

  public final native void glDeleteTexture(Texture texture) /*-{
    this.deleteTexture(texture);
  }-*/;
  
  public final native void glDisable(int i)/*-{
    this.disable(i);
  }-*/;

  public final native void glDisableClientState(int i)/*-{
    this.disableClientState(i);
  }-*/;
  
  public native final void glDisableVertexAttribArray(int i) /*-{
    this.disableVertexAttribArray(i);
   }-*/;
  
  public final native void glDrawArrays(int t, int i, int j) /*-{
    this.drawArrays(t, i, j);
  }-*/;

  public native final void glDrawElements(int elements, int length, int type, 
     int offset) /*-{
   this.drawElements(elements, length, type, offset);
  }-*/;

  public native final void glEnable(int i) /*-{
    this.enable(i);
  }-*/;
  
  public final native void glEnableVertexAttribArray(int i) /*-{
    this.enableVertexAttribArray(i);
  }-*/;

  public final native void glFrontFace(int f) /*-{
    this.frontFace(f);
  }-*/;
  
  public native final int glGetAttribLocation(Program program, String name) /*-{
    return this.getAttribLocation(program, name);
 }-*/;

  public final native int glGetError() /*-{
    return this.getError();
  }-*/;
  
  public native final int glGetProgramInfoLog(Program programObject)/*-{
    return this.getProgramInfoLog(programObject);
  }-*/;

  // TODO(haustein) Find a reasonable naming scheme for return parameter types here and for 
  // getShaderParameter (appending a single lower case character is used for parameter types
  // already).
  public native final int glGetProgramParameteri(Program program, int param) /*-{
    return this.getProgramParameter(program, param);
  }-*/;
  
  public native final boolean glGetProgramParameterb(Program program, int param) /*-{
    return this.getProgramParameter(program, param);
  }-*/;
  
  public native final int glGetShaderInfoLog(Shader shader)/*-{
    return this.getShaderInfoLog(shader);
  }-*/;

  public native final boolean glGetShaderParameterb(Shader shader, int id) /*-{
    return this.getShaderParameter(shader, id);
  }-*/;

  public native final int glGetShaderParameteri(Shader shader, int id) /*-{
    return this.getShaderParameter(shader, id);
  }-*/;
  
  public native final int glGetUniformLocation(Program program, String name) /*-{
    return this.getUniformLocation(program, name);
  }-*/;
 
  public native final void glHint(int h, int i) /*-{
    this.hint(h, i);
  }-*/;

  public final native void glLinkProgram(Program programObject) /*-{
    this.linkProgram(programObject);
  }-*/;

  public native final void glScissor(int i, int j, int width, int height) /*-{
    this.scissor(i, j, width, height);
  }-*/;

  public final native void glShaderSource(Shader shader, String shaderSource) /*-{
    this.shaderSource(shader, shaderSource);
  }-*/;

  /**
   * Canvas, image and video elements should work here.
   */
  public native final void glTexImage2D(int target, int texture, Element image)  /*-{
    this.texImage2D(target, texture, image);
  }-*/;

  /**
   * Canvas, image and video elements should work here.
   */
  public native final void glTexImage2D(int target, int texture, Element image, boolean flip)  /*-{
    this.texImage2D(target, texture, image, flip);
  }-*/;

  
  public native final void glTexImage2D (int target, int level, int internalformat, 
      int width, int height, int border, int format, int type, WebGLArray<?> pixels) /*-{
    this.texImage2D(target, level, internalformat, width, height, border, format, type, pixels);
  }-*/;
  
  public final native void glTexParameterf(int i, int j, float k) /*-{
    this.texParameterf(i, j, k);
  }-*/;

  public native final void glUniformMatrix4fv(int location, boolean transpose, 
      WebGLArray<?> data) /*-{
    this.uniformMatrix4fv(location, transpose, data); 
  }-*/;

  public native final void glUniform1i(int loc, int i) /*-{
    this.uniform1i(loc, i);
  }-*/;

  public final native void glUseProgram(Program programObject) /*-{
    this.useProgram(programObject);
  }-*/;

  public native final void glVertexAttribPointer(int index, int size, int elementType,
      boolean normalized, int stride, int offset) /*-{
    this.vertexAttribPointer(index, size, elementType, normalized, stride, offset); 
  }-*/;

  public native final void glVertexAttrib4f(int index, 
      float x, float y, float z, float w) /*-{
    this.vertexAttrib4f(index, x, y, z, w);
  }-*/;

  public native final void glViewport(int x, int y, int w, int h) /*-{
    this.viewport(x, y, w, h);
  }-*/;

  public native final void glGenerateMipmap(int type) /*-{
    this.generateMipmap(type);
  }-*/;

  public static class Texture extends JavaScriptObject {
    protected Texture() {
    }
  }

  public static class Buffer extends JavaScriptObject {
    protected Buffer() {
    }
  }

  public static class Program extends JavaScriptObject {
    protected Program() {
    }
  }

  public static class Shader extends JavaScriptObject {
    protected Shader() {
    }
  }

  public native final String glGetString(int id) /*-{
	return this.getString(id);
  }-*/;

  public native final void glTexParameteri(int target, int pname, int param) /*-{
  	this.texParameteri(target, pname, param);
  }-*/;


  public native final void glTexSubImage2D(int target, int level, int xoffset, int yoffset, 
      int width, int height, int format, int type, WebGLArray<?> pixels) /*-{
    this.texSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
  }-*/;
  
  public native final void glTexSubImage2D(int target, int level, int xoffset, int yoffset, 
      JavaScriptObject image, boolean optFlipY, boolean optAsPremultipliedAlpha)  /*-{
    this.texSubImage2D(target, level, xoffset, yoffset, image, optFlipY, optAsPremultipliedAlpha);
  }-*/;

  public native final void glDepthFunc(int func) /*-{
  	this.depthFunc(func);
  }-*/;
  
  public native final void glDepthMask(boolean flag) /*-{
  	this.depthMask(flag);
  }-*/;
 
  public native final void glDepthRange(float zNear, float zFar) /*-{
  	this.depthRange(zNear, zFar);
  }-*/;

  public native final void glFinish() /*-{
    this.finish();	
  }-*/;

  public native final void glPixelStorei(int i, int j) /*-{
  	this.pixelStorei(i, j);
  }-*/;

  public native final void glBufferSubData(int target, int offset, WebGLArray<?> data) /*-{
	this.bufferSubData(target, offset, data);
  }-*/;
}
