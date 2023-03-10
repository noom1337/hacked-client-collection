/*
 * Decompiled with CFR 0.150.
 */
package com.sun.glass.events;

import com.sun.glass.ui.Application;

public class KeyEvent {
    public static final int PRESS = 111;
    public static final int RELEASE = 112;
    public static final int TYPED = 113;
    public static final int MODIFIER_NONE = 0;
    public static final int MODIFIER_SHIFT = 1;
    public static final int MODIFIER_FUNCTION = 2;
    public static final int MODIFIER_CONTROL = 4;
    public static final int MODIFIER_OPTION = 8;
    public static final int MODIFIER_ALT = 8;
    public static final int MODIFIER_COMMAND = 16;
    public static final int MODIFIER_WINDOWS = 16;
    public static final int MODIFIER_BUTTON_PRIMARY = 32;
    public static final int MODIFIER_BUTTON_SECONDARY = 64;
    public static final int MODIFIER_BUTTON_MIDDLE = 128;
    public static final int MODIFIER_BUTTON_BACK = 256;
    public static final int MODIFIER_BUTTON_FORWARD = 512;
    public static final int KEY_LOCK_OFF = 0;
    public static final int KEY_LOCK_ON = 1;
    public static final int KEY_LOCK_UNKNOWN = -1;
    public static final int VK_UNDEFINED = 0;
    public static final int VK_ENTER = 10;
    public static final int VK_BACKSPACE = 8;
    public static final int VK_TAB = 9;
    public static final int VK_CLEAR = 12;
    public static final int VK_PAUSE = 19;
    public static final int VK_ESCAPE = 27;
    public static final int VK_SPACE = 32;
    public static final int VK_DELETE = 127;
    public static final int VK_PRINTSCREEN = 154;
    public static final int VK_INSERT = 155;
    public static final int VK_HELP = 156;
    public static final int VK_SHIFT = 16;
    public static final int VK_CONTROL = 17;
    public static final int VK_ALT = 18;
    public static final int VK_ALT_GRAPH = 65406;
    public static final int VK_WINDOWS = 524;
    public static final int VK_CONTEXT_MENU = 525;
    public static final int VK_CAPS_LOCK = 20;
    public static final int VK_NUM_LOCK = 144;
    public static final int VK_SCROLL_LOCK = 145;
    public static final int VK_COMMAND = 768;
    public static final int VK_PAGE_UP = 33;
    public static final int VK_PAGE_DOWN = 34;
    public static final int VK_END = 35;
    public static final int VK_HOME = 36;
    public static final int VK_LEFT = 37;
    public static final int VK_UP = 38;
    public static final int VK_RIGHT = 39;
    public static final int VK_DOWN = 40;
    public static final int VK_COMMA = 44;
    public static final int VK_MINUS = 45;
    public static final int VK_PERIOD = 46;
    public static final int VK_SLASH = 47;
    public static final int VK_SEMICOLON = 59;
    public static final int VK_EQUALS = 61;
    public static final int VK_OPEN_BRACKET = 91;
    public static final int VK_BACK_SLASH = 92;
    public static final int VK_CLOSE_BRACKET = 93;
    public static final int VK_MULTIPLY = 106;
    public static final int VK_ADD = 107;
    public static final int VK_SEPARATOR = 108;
    public static final int VK_SUBTRACT = 109;
    public static final int VK_DECIMAL = 110;
    public static final int VK_DIVIDE = 111;
    public static final int VK_AMPERSAND = 150;
    public static final int VK_ASTERISK = 151;
    public static final int VK_DOUBLE_QUOTE = 152;
    public static final int VK_LESS = 153;
    public static final int VK_GREATER = 160;
    public static final int VK_BRACELEFT = 161;
    public static final int VK_BRACERIGHT = 162;
    public static final int VK_BACK_QUOTE = 192;
    public static final int VK_QUOTE = 222;
    public static final int VK_AT = 512;
    public static final int VK_COLON = 513;
    public static final int VK_CIRCUMFLEX = 514;
    public static final int VK_DOLLAR = 515;
    public static final int VK_EURO_SIGN = 516;
    public static final int VK_EXCLAMATION = 517;
    public static final int VK_INV_EXCLAMATION = 518;
    public static final int VK_LEFT_PARENTHESIS = 519;
    public static final int VK_NUMBER_SIGN = 520;
    public static final int VK_PLUS = 521;
    public static final int VK_RIGHT_PARENTHESIS = 522;
    public static final int VK_UNDERSCORE = 523;
    public static final int VK_0 = 48;
    public static final int VK_1 = 49;
    public static final int VK_2 = 50;
    public static final int VK_3 = 51;
    public static final int VK_4 = 52;
    public static final int VK_5 = 53;
    public static final int VK_6 = 54;
    public static final int VK_7 = 55;
    public static final int VK_8 = 56;
    public static final int VK_9 = 57;
    public static final int VK_A = 65;
    public static final int VK_B = 66;
    public static final int VK_C = 67;
    public static final int VK_D = 68;
    public static final int VK_E = 69;
    public static final int VK_F = 70;
    public static final int VK_G = 71;
    public static final int VK_H = 72;
    public static final int VK_I = 73;
    public static final int VK_J = 74;
    public static final int VK_K = 75;
    public static final int VK_L = 76;
    public static final int VK_M = 77;
    public static final int VK_N = 78;
    public static final int VK_O = 79;
    public static final int VK_P = 80;
    public static final int VK_Q = 81;
    public static final int VK_R = 82;
    public static final int VK_S = 83;
    public static final int VK_T = 84;
    public static final int VK_U = 85;
    public static final int VK_V = 86;
    public static final int VK_W = 87;
    public static final int VK_X = 88;
    public static final int VK_Y = 89;
    public static final int VK_Z = 90;
    public static final int VK_NUMPAD0 = 96;
    public static final int VK_NUMPAD1 = 97;
    public static final int VK_NUMPAD2 = 98;
    public static final int VK_NUMPAD3 = 99;
    public static final int VK_NUMPAD4 = 100;
    public static final int VK_NUMPAD5 = 101;
    public static final int VK_NUMPAD6 = 102;
    public static final int VK_NUMPAD7 = 103;
    public static final int VK_NUMPAD8 = 104;
    public static final int VK_NUMPAD9 = 105;
    public static final int VK_F1 = 112;
    public static final int VK_F2 = 113;
    public static final int VK_F3 = 114;
    public static final int VK_F4 = 115;
    public static final int VK_F5 = 116;
    public static final int VK_F6 = 117;
    public static final int VK_F7 = 118;
    public static final int VK_F8 = 119;
    public static final int VK_F9 = 120;
    public static final int VK_F10 = 121;
    public static final int VK_F11 = 122;
    public static final int VK_F12 = 123;
    public static final int VK_F13 = 61440;
    public static final int VK_F14 = 61441;
    public static final int VK_F15 = 61442;
    public static final int VK_F16 = 61443;
    public static final int VK_F17 = 61444;
    public static final int VK_F18 = 61445;
    public static final int VK_F19 = 61446;
    public static final int VK_F20 = 61447;
    public static final int VK_F21 = 61448;
    public static final int VK_F22 = 61449;
    public static final int VK_F23 = 61450;
    public static final int VK_F24 = 61451;
    public static final int VK_DEAD_GRAVE = 128;
    public static final int VK_DEAD_ACUTE = 129;
    public static final int VK_DEAD_CIRCUMFLEX = 130;
    public static final int VK_DEAD_TILDE = 131;
    public static final int VK_DEAD_MACRON = 132;
    public static final int VK_DEAD_BREVE = 133;
    public static final int VK_DEAD_ABOVEDOT = 134;
    public static final int VK_DEAD_DIAERESIS = 135;
    public static final int VK_DEAD_ABOVERING = 136;
    public static final int VK_DEAD_DOUBLEACUTE = 137;
    public static final int VK_DEAD_CARON = 138;
    public static final int VK_DEAD_CEDILLA = 139;
    public static final int VK_DEAD_OGONEK = 140;
    public static final int VK_DEAD_IOTA = 141;
    public static final int VK_DEAD_VOICED_SOUND = 142;
    public static final int VK_DEAD_SEMIVOICED_SOUND = 143;

    public static int getKeyCodeForChar(char c) {
        return Application.getKeyCodeForChar(c);
    }

    public static final String getTypeString(int n) {
        switch (n) {
            case 111: {
                return "PRESS";
            }
            case 112: {
                return "RELEASE";
            }
            case 113: {
                return "TYPED";
            }
        }
        return "UNKNOWN";
    }
}

