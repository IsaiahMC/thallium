package thallium.fabric.gui;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import thallium.fabric.ThalliumMod;
import thallium.fabric.math.FastMathType;

public class ThalliumOptions {

    public static boolean useFastRenderer     = true;
    public static boolean useFastMath         = true;
    public static boolean optimizeAnimations  = true;
    public static boolean fastPlayerModel     = true;
    public static boolean optimizeHoppers     = true;
    public static boolean optimizeBlockTick   = true;
    public static boolean fastItemRender      = true;
    public static boolean optimizeRedstone    = true;
    public static boolean capEntityCollisions = true;
    public static boolean optimizeRaytraceAir = true;

    public static boolean animateWater = true;
    public static boolean animateLava = true;
    public static boolean animateFire = true;
    public static boolean animatePortal = true;
    public static boolean animatePrismarine = false;
    public static boolean animateTextures = true;
    public static boolean animateCmdBlock = true;

    public static EnumDirectionalRendering directionalRender = EnumDirectionalRendering.OFF;
    public static FastMathType fastMathType = FastMathType.RIVEN;
    public static EnumFogType fogType = EnumFogType.OFF;


    @SuppressWarnings("unchecked")
    public static void load() {
        long start = System.currentTimeMillis();

        try {
            FileInputStream fis = new FileInputStream(ThalliumMod.saveFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            HashMap<String,String> map = (HashMap<String, String>) ois.readObject();
            for (String str : map.keySet()) {
                String key = map.get(str);
                if (str.equals("fastRender"))
                    useFastRenderer = Boolean.valueOf(key);
                if (str.equals("fastmath"))
                    useFastMath = Boolean.valueOf(key);
                if (str.equals("optimizeAnimations"))
                    optimizeAnimations = Boolean.valueOf(key);
                if (str.equals("directionalRender"))
                    directionalRender = EnumDirectionalRendering.OFF;
                if (str.equals("fastMathType"))
                    fastMathType = FastMathType.values()[Integer.valueOf(key)];
                if (str.equals("fastPlayerModel"))
                    fastPlayerModel = Boolean.valueOf(key);
                if (str.equals("optimizeHoppers"))
                    optimizeHoppers = Boolean.valueOf(key);
                if (str.equals("fogType"))
                    fogType = EnumFogType.values()[Integer.valueOf(key)];
                if (str.equals("fastItemRender"))
                    fastItemRender = Boolean.valueOf(key);
                if (str.equals("optimizeRedstone"))
                    optimizeRedstone = Boolean.valueOf(key);
                if (str.equalsIgnoreCase("capEntityCollisions"))
                    capEntityCollisions = Boolean.valueOf(key);
                if (str.equalsIgnoreCase("animateWater"))
                    animateWater = Boolean.valueOf(key);
                if (str.equalsIgnoreCase("animateLava"))
                    animateLava = Boolean.valueOf(key);
                if (str.equalsIgnoreCase("animateFire"))
                    animateFire = Boolean.valueOf(key);
                if (str.equalsIgnoreCase("animatePortal"))
                    animatePortal = Boolean.valueOf(key);
                if (str.equalsIgnoreCase("animatePrismarine"))
                    animatePrismarine = Boolean.valueOf(key);
                if (str.equalsIgnoreCase("animateTextures"))
                    animateTextures = Boolean.valueOf(key);
                if (str.equalsIgnoreCase("animateCmdBlock"))
                    animateCmdBlock = Boolean.valueOf(key);
                if (str.equalsIgnoreCase("optimizeRaytraceAir"))
                    optimizeRaytraceAir = Boolean.valueOf(key);
            }
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        ThalliumMod.LOGGER.info("Loaded thallium options in " + (System.currentTimeMillis()-start) + "ms");
    }

    public static void save() {
        long start = System.currentTimeMillis();

        try {
            HashMap<String,String> map = new HashMap<>();
            map.put("fastRender",          "" + useFastRenderer);
            map.put("fastmath",            "" + useFastMath);
            map.put("optimizeAnimations",  "" + optimizeAnimations);
            map.put("directionalRender",   "" + directionalRender.ordinal());
            map.put("fastMathType",        "" + fastMathType.ordinal());
            map.put("fastPlayerModel",     "" + fastPlayerModel);
            map.put("optimizeHoppers",     "" + optimizeHoppers);
            map.put("fogType",             "" + fogType.ordinal());
            map.put("fastItemRender",      "" + fastItemRender);
            map.put("optimizeRedstone",    "" + optimizeRedstone);
            map.put("capEntityCollisions", "" + capEntityCollisions);
            map.put("animateWater",        "" + animateWater);
            map.put("animateLava",         "" + animateLava);
            map.put("animateFire",         "" + animateFire);
            map.put("animatePortal",       "" + animatePortal);
            map.put("animatePrismarine",   "" + animatePrismarine);
            map.put("animateTextures",     "" + animateTextures);
            map.put("animateCmdBlock",     "" + animateCmdBlock);
            map.put("optimizeRaytraceAir", "" + optimizeRaytraceAir);

            ThalliumMod.saveFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(ThalliumMod.saveFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(map);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ThalliumMod.LOGGER.info("Saved thallium options in " + (System.currentTimeMillis()-start) + "ms");
    }


}