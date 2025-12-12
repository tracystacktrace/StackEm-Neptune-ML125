package net.tracystacktrace.stackem.modloader.patch;

import net.minecraft.src.RenderEngine;
import net.tracystacktrace.stackem.tools.UnsafeInstance;

import java.util.Map;

public final class RenderEngineHacks {
    /**
     * Deobfuscated name: textureMap
     * <br>
     * Current obfuscated name: b
     */
    @SuppressWarnings("rawtypes")
    private static Map getTextureMap(RenderEngine renderEngine) {
        return UnsafeInstance.getObject(RenderEngine.class, renderEngine, CompatibilityTools.OBFUSCATED_ENV ? "d" : "textureMap");
    }

    public static boolean textureMap_containsKey(RenderEngine renderEngine, String s) {
        return getTextureMap(renderEngine).containsKey(s);
    }

    public static int textureMap_getInt(RenderEngine renderEngine, String s) {
        return (int) getTextureMap(renderEngine).get(s);
    }

    @SuppressWarnings("unchecked")
    public static void textureMap_setInt(RenderEngine renderEngine, String s, int i) {
        getTextureMap(renderEngine).put(s, i);
    }
}
