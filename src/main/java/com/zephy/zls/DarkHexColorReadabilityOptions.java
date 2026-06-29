package com.zephy.zls;

public class DarkHexColorReadabilityOptions {
    private static boolean isEnabled = true;
    private static int backgroundColorInt = 0x7FFFFFFF;
    private static float luminanceThreshold = 0.1f;

    public static void SetIsEnabled(boolean _isEnabled) {
        isEnabled = _isEnabled;
    }
    public static boolean GetIsEnabled() {
        return isEnabled;
    }

    public static void SetBackgroundColorInt(int _backgroundColorInt) {
        backgroundColorInt = _backgroundColorInt;
    }
    public static int GetBackgroundColorInt() {
        return backgroundColorInt;
    }

    public static void SetLuminanceThreshold(float _luminanceThreshold) {
        luminanceThreshold = _luminanceThreshold;
    }
    public static float GetLuminanceThreshold() {
        return luminanceThreshold;
    }
}
