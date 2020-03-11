package com.focess.pathfinder.core.util;

import com.focess.pathfinder.goal.Goal;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class NMSManager {

    public static final Class<?> CraftEntity;
    // basic class
    public static final Class<?> CraftServer;
    public static final Class<?> CraftWorld;

    // NBT Builder
    public static final Class<?> EntityPlayer;
    // basic
    private static final Map<Class<?>, Map<String, Field>> loadedFields = new HashMap<>();
    private static final Map<Class<?>, Map<String, Method>> loadedMethods = new HashMap<>();

    private static final Map<String, Class<?>> loadedNMSClasses = new HashMap<>();
    public static final Class<?> MinecraftServer;

    public static final Class<?> NBTTagCompound;
    private static int versionInt = -1;
    private static String versionString;
    public static final Class<?> World;
    public static final Class<?> WorldServer;
    private static String versionStringAsClassName;


    private static final Method getHandle;

    public static final Class<?> PathfinderGoalSelector;

    public static final Class<?> EntityInsentient;

    public static final Class<?> PathfinderGoal;

    private static final String[] pathfinderGoalMethodNames;

    public static final Field PathfinderGoalsField;

    public static final Field PathfinderGoalItema;

    public static final Field PathfinderGoalItemb;

    public static final Method PathfinderGoalMutex;

    public static final Method PathfinderGoalSelectorAdd;

    public static final Method PathfinderGoalSelectorRemove;

    public static final Class<Enum<?>> Control;

    public static final Method PathfinderGoalMutexGetter;

    static {
        Field PathfinderGoalsField1 = null;
        Field PathfinderGoalItema1 = null;
        Field PathfinderGoalItemb1 = null;
        Method PathfinderGoalMutex1 = null;
        Method PathfinderGoalSelectorAdd1 = null;
        Method PathfinderGoalSelectorRemove1 = null;
        Method PathfinderGoalMutexGetter1 = null;
        World = NMSManager.getNMSClass("World");
        MinecraftServer = NMSManager.getNMSClass("MinecraftServer");
        WorldServer = NMSManager.getNMSClass("WorldServer");
        CraftWorld = NMSManager.getCraftClass("CraftWorld");
        CraftServer = NMSManager.getCraftClass("CraftServer");
        EntityPlayer = NMSManager.getNMSClass("EntityPlayer");
        CraftEntity = NMSManager.getCraftClass("entity.CraftEntity");
        getHandle = NMSManager.getMethod(CraftEntity, "getHandle");
        NBTTagCompound = NMSManager.getNMSClass("NBTTagCompound");
        PathfinderGoalSelector = NMSManager.getNMSClass("PathfinderGoalSelector");
        EntityInsentient = NMSManager.getNMSClass("EntityInsentient");
        PathfinderGoal = NMSManager.getNMSClass("PathfinderGoal");
        Control = (Class<Enum<?>>) NMSManager.getNMSClass("PathfinderGoal$Type");
        pathfinderGoalMethodNames = new String[6];
        int point = 0;
        for (Method method : PathfinderGoal.getDeclaredMethods())
            if (!method.getReturnType().equals(boolean.class) && !method.getReturnType().equals(Void.TYPE))
                PathfinderGoalMutexGetter1 = method;
                else if (method.getParameterCount() == 0 && method.getName().length() == 1 && 5 > point) {
                pathfinderGoalMethodNames[point++] = method.getName();
            } else if (method.getParameterCount() == 1)
                PathfinderGoalMutex1 = method;
            else if (method.getParameterCount() == 0 && method.getReturnType().equals(boolean.class))
                pathfinderGoalMethodNames[5] = method.getName();
        PathfinderGoalMutex = PathfinderGoalMutex1;
        PathfinderGoalMutexGetter = PathfinderGoalMutexGetter1;
        Arrays.sort(pathfinderGoalMethodNames,0,5);
        for (Method method : PathfinderGoalSelector.getDeclaredMethods())
            if (method.getParameterCount() == 2 && Arrays.equals(method.getParameterTypes(), new Class<?>[]{int.class, PathfinderGoal}))
                PathfinderGoalSelectorAdd1 = method;
            else if (method.getParameterCount() == 1 && Arrays.equals(method.getParameterTypes(),new Class<?>[]{PathfinderGoal}))
                PathfinderGoalSelectorRemove1 = method;
        PathfinderGoalSelectorAdd = PathfinderGoalSelectorAdd1;
        PathfinderGoalSelectorRemove = PathfinderGoalSelectorRemove1;
        try {
            if (getVersionInt() > 13) {
                PathfinderGoalItema1 = NMSManager.getField(NMSManager.getNMSClass("PathfinderGoalWrapped"), "a");
                PathfinderGoalItemb1 = NMSManager.getField(NMSManager.getNMSClass("PathfinderGoalWrapped"), "b");
                PathfinderGoalsField1 = PathfinderGoal.getDeclaredField("d");
            } else {
                Class<?> PathfinderGoalItem;
                try {
                    PathfinderGoalItem = Class.forName("net.minecraft.server." + NMSManager.getVersionString() + "PathfinderGoalSelector$PathfinderGoalSelectorItem");
                } catch (Exception e) {
                    PathfinderGoalItem = Class.forName("net.minecraft.server." + NMSManager.getVersionString() + "PathfinderGoalSelectorItem");
                }
                PathfinderGoalItema1 = NMSManager.getField(PathfinderGoalItem, "a");
                PathfinderGoalItemb1 = NMSManager.getField(PathfinderGoalItem, "b");
                PathfinderGoalsField1 = PathfinderGoal.getDeclaredField("b");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        PathfinderGoalsField = PathfinderGoalsField1;
        PathfinderGoalItema = PathfinderGoalItema1;
        PathfinderGoalItemb = PathfinderGoalItemb1;
    }

    public static Object getConnection(final Player player) {
        final Method getHandleMethod = NMSManager.getMethod(player.getClass(), "getHandle");

        if (getHandleMethod != null)
            try {
                final Object nmsPlayer = getHandleMethod.invoke(player);
                final Field playerConField = NMSManager.getField(nmsPlayer.getClass(), "playerConnection");
                return playerConField.get(nmsPlayer);
            } catch (final Exception e) {
                e.printStackTrace();
            }

        return null;
    }

    public static Constructor<?> getConstructor(final Class<?> clazz, final Class<?>[] params) {
        try {
            return clazz.getConstructor(params);
        } catch (final NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class<?> getCraftClass(final String nmsClassName) {
        if (NMSManager.loadedNMSClasses.containsKey(nmsClassName))
            return NMSManager.loadedNMSClasses.get(nmsClassName);
        final String clazzName = "org.bukkit.craftbukkit." + NMSManager.getVersionString() + nmsClassName;
        final Class<?> clazz;
        try {
            clazz = Class.forName(clazzName);
        } catch (final Throwable t) {
            t.printStackTrace();
            return NMSManager.loadedNMSClasses.put(nmsClassName, null);
        }
        NMSManager.loadedNMSClasses.put(nmsClassName, clazz);
        return clazz;
    }

    public static Field getField(final Class<?> clazz, final String fieldName) {
        if (!NMSManager.loadedFields.containsKey(clazz))
            NMSManager.loadedFields.put(clazz, new HashMap<String, Field>());
        final Map<String, Field> fields = NMSManager.loadedFields.get(clazz);
        if (fields.containsKey(fieldName))
            return fields.get(fieldName);
        try {
            final Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            fields.put(fieldName, field);
            NMSManager.loadedFields.put(clazz, fields);
            return field;
        } catch (final Exception e) {
            e.printStackTrace();
            fields.put(fieldName, null);
            NMSManager.loadedFields.put(clazz, fields);
        }
        return null;
    }

    public static Method getMethod(final Class<?> clazz, final String methodName, final Class<?>... params) {
        if (!NMSManager.loadedMethods.containsKey(clazz))
            NMSManager.loadedMethods.put(clazz, new HashMap<String, Method>());
        final Map<String, Method> methods = NMSManager.loadedMethods.get(clazz);
        if (methods.containsKey(methodName) && Arrays.equals(methods.get(methodName).getParameterTypes(), params))
            return methods.get(methodName);
        try {
            final Method method = clazz.getDeclaredMethod(methodName, params);
            method.setAccessible(true);
            methods.put(methodName, method);
            NMSManager.loadedMethods.put(clazz, methods);
            return method;
        } catch (final Exception e) {
            e.printStackTrace();
            methods.put(methodName, null);
            NMSManager.loadedMethods.put(clazz, methods);
        }
        return null;
    }

    public static Class<?> getNMSClass(final String nmsClassName) {
        return getNMSClass(nmsClassName, false);
    }

    public static Class<?> getNMSClass(final String nmsClassName, boolean flag) {
        if (NMSManager.loadedNMSClasses.containsKey(nmsClassName))
            return NMSManager.loadedNMSClasses.get(nmsClassName);
        final String clazzName = "net.minecraft.server." + NMSManager.getVersionString() + nmsClassName;
        final Class<?> clazz;
        try {
            clazz = Class.forName(clazzName);
        } catch (final Exception e) {
            if (!flag)
                e.printStackTrace();
            return NMSManager.loadedNMSClasses.put(nmsClassName, null);
        }
        NMSManager.loadedNMSClasses.put(nmsClassName, clazz);
        return clazz;
    }

    public static int getVersionInt() {
        if (NMSManager.versionInt == -1) {
            final String v = Bukkit.getServer().getClass().getPackage().getName();
            NMSManager.versionInt = Integer.parseInt(v.substring(v.lastIndexOf('.') + 1).split("_")[1]);
        }
        return NMSManager.versionInt;
    }

    public static String getVersionString() {
        if (NMSManager.versionString == null) {
            final String name = Bukkit.getServer().getClass().getPackage().getName();
            NMSManager.versionString = name.substring(name.lastIndexOf('.') + 1) + ".";
        }
        return NMSManager.versionString;
    }

    public static String getVersionStringAsClassName() {
        if (NMSManager.versionStringAsClassName == null)
            versionStringAsClassName = ("net.minecraft.server." + NMSManager.getVersionString()).replace(".", "/");
        return NMSManager.versionStringAsClassName;
    }

    public static boolean isHighVersion() {
        return getVersionInt() > 13;
    }

    public static Object getNMSEntity(org.bukkit.entity.Entity entity) {
        try {
            return getHandle.invoke(entity);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String[] getPathfinderGoalMethodNames() {
        return Arrays.copyOf(pathfinderGoalMethodNames, 6);
    }

    public static Object toNMSControls(EnumSet<Goal.Control> controls) {
        if (controls.size() == 0)
            try {
                return EnumSet.class.getDeclaredMethod("noneOf", Class.class).invoke(null,Control);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        List<Enum> enums = Lists.newArrayList();
        Enum<?> first = null;
        boolean flag = false;
        for (Goal.Control control:controls)
            if (!flag) {
                flag = true;
                first = toNMSControl(control);
            }
            else
                enums.add(toNMSControl(control));
        try {
            return EnumSet.class
                    .getDeclaredMethod("of", Enum.class, Enum[].class).invoke(null,first,enums.toArray(new Enum[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Enum<?> toNMSControl(Goal.Control control) {
        for (Enum<?> c:Control.getEnumConstants())
            if (c.name().equals(control.name()))
                return c;
        return null;
    }

    public static EnumSet<Goal.Control> toFocessControls(Collection<?> collection) {
        EnumSet<Goal.Control> controls = EnumSet.noneOf(Goal.Control.class);
        for (Object obj:collection)
            controls.add(toFocessControl(obj));
        return controls;
    }

    private static Goal.Control toFocessControl(Object obj) {
        try {
            return Goal.Control.valueOf((String) Enum.class.getDeclaredMethod("name").invoke(obj));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}