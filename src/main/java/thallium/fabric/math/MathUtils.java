package thallium.fabric.math;

import thallium.fabric.gui.ThalliumOptions;
import it.unimi.dsi.fastutil.floats.Float2FloatOpenHashMap;
import net.minecraft.util.Util;

public class MathUtils {

    private static final Float2FloatOpenHashMap SIN_TABLE = new Float2FloatOpenHashMap();
    private static final Float2FloatOpenHashMap COS_TABLE = new Float2FloatOpenHashMap();

    private static final float[] SINE_TABLE = Util.make(new float[65536], fs -> {
        for (int i = 0; i < ((float[])fs).length; ++i)
            fs[i] = (float)Math.sin((double)i * Math.PI * 2.0 / 65536.0);
    });

    static {
        for (float i = -500; i < 500; i += 1) {
            SIN_TABLE.put(i/10, (float)Math.sin(i/10));
            COS_TABLE.put(i/10, (float)Math.cos(i/10));
        }
        System.out.println(SIN_TABLE.size() + COS_TABLE.size());
    }

    public static float round(float d) {
        return ((int)(d*10)) / 10f;
    }

    public static float fastSin(float value) {
        switch (ThalliumOptions.fastMathType) {
            case DEVMASTER:
                return SIN.Devmaster.sin(value);
            case ICECORE:
                return SIN.Icecore.sin(value);
            case RIVEN:
                return SIN.Riven.sin(value);
            case IMATH:
                return SIN_TABLE.get(round(value));
            default:
                break;
        }
        return SINE_TABLE[(int)(value * 10430.378F) & 65535];
    }

    public static float fastCos(float value) {
        switch (ThalliumOptions.fastMathType) {
            case DEVMASTER:
                return SIN.Devmaster.cos(value);
            case ICECORE:
                return SIN.Icecore.cos(value);
            case RIVEN:
                return SIN.Riven.cos(value);
            case IMATH:
                return COS_TABLE.get(round(value));
            default:
                break;
        }
        return SINE_TABLE[(int)(value * 10430.378F + 16384.0F) & 65535];
    }

}