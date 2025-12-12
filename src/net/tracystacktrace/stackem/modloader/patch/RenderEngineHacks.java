package net.tracystacktrace.stackem.modloader.patch;

import net.minecraft.src.RenderEngine;

import java.lang.reflect.Field;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
public final class RenderEngineHacks {

    public static boolean textureMap_containsKey(RenderEngine renderEngine, String s) {
        try {
            //textureMap	b
            final Field field0 = RenderEngine.class.getDeclaredField(CompatibilityTools.OBFUSCATED_ENV ? "b" : "textureMap");
            field0.setAccessible(true);
            return ((Map) field0.get(renderEngine)).containsKey(s);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static int textureMap_getInt(RenderEngine renderEngine, String s) {
        try {
            //textureMap	b
            final Field field0 = RenderEngine.class.getDeclaredField(CompatibilityTools.OBFUSCATED_ENV ? "b" : "textureMap");
            field0.setAccessible(true);
            return (int) ((Map) field0.get(renderEngine)).get(s);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void textureMap_setInt(RenderEngine renderEngine, String s, int i) {
        try {
            //textureMap	b
            final Field field0 = RenderEngine.class.getDeclaredField(CompatibilityTools.OBFUSCATED_ENV ? "b" : "textureMap");
            field0.setAccessible(true);
            ((Map) field0.get(renderEngine)).put(s, i);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
