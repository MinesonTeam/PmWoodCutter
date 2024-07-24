package kz.hxncus.mc.pmwoodcutter.util;

import kz.hxncus.mc.pmwoodcutter.PmWoodCutter;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class ReflectionUtil {
    public Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes) throws NoSuchMethodException {
        Class<?>[] primitiveTypes = DataType.getPrimitive(parameterTypes);
        byte b;
        int i;
        Constructor<?>[] arrayOfConstructor = clazz.getConstructors();
        for (i = arrayOfConstructor.length, b = 0; b < i; ) {
            Constructor<?> constructor = arrayOfConstructor[b];
            if (!DataType.compare(DataType.getPrimitive(constructor.getParameterTypes()), primitiveTypes)) {
                b++;
                continue;
            }
            return constructor;
        }
        throw new NoSuchMethodException("There is no such constructor in this class with the specified parameter types");
    }

    public Constructor<?> getConstructor(String className, PackageType packageType, Class<?>... parameterTypes) throws NoSuchMethodException, ClassNotFoundException {
        return getConstructor(packageType.getClass(className), parameterTypes);
    }

    public Object instantiateObject(Class<?> clazz, Object... arguments) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        return getConstructor(clazz, DataType.getPrimitive(arguments)).newInstance(arguments);
    }

    public Object instantiateObject(String className, PackageType packageType, Object... arguments) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        return instantiateObject(packageType.getClass(className), arguments);
    }

    public Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        Class<?>[] primitiveTypes = DataType.getPrimitive(parameterTypes);
        byte b;
        int i;
        Method[] arrayOfMethod;
        for (i = (arrayOfMethod = clazz.getMethods()).length, b = 0; b < i; ) {
            Method method = arrayOfMethod[b];
            if (!method.getName()
                       .equals(methodName) || !DataType.compare(DataType.getPrimitive(method.getParameterTypes()), primitiveTypes)) {
                b++;
                continue;
            }
            return method;
        }
        throw new RuntimeException("There is no such method in this class with the specified name and parameter types");
    }

    public Method getMethod(String className, PackageType packageType, String methodName, Class... parameterTypes) throws ClassNotFoundException {
        return getMethod(packageType.getClass(className), methodName, parameterTypes);
    }

    public Object invokeMethod(Object instance, String methodName, Object... arguments) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return getMethod(instance.getClass(), methodName, DataType.getPrimitive(arguments)).invoke(instance, arguments);
    }

    public Object invokeMethod(Object instance, Class<?> clazz, String methodName, Object... arguments) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return getMethod(clazz, methodName, DataType.getPrimitive(arguments)).invoke(instance, arguments);
    }

    public Object invokeMethod(Object instance, String className, PackageType packageType, String methodName, Object... arguments) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
        return invokeMethod(instance, packageType.getClass(className), methodName, arguments);
    }

    public Field getField(Class<?> clazz, boolean declared, String fieldName) {
        Field field;
        try {
            field = declared ? clazz.getDeclaredField(fieldName) : clazz.getField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        field.setAccessible(true);
        return field;
    }

    public Field getField(String className, PackageType packageType, boolean declared, String fieldName) throws NoSuchFieldException, SecurityException, ClassNotFoundException {
        return getField(packageType.getClass(className), declared, fieldName);
    }

    public Object getValue(Object instance, Class<?> clazz, boolean declared, String fieldName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        return getField(clazz, declared, fieldName).get(instance);
    }

    public Object getValue(Object instance, String className, PackageType packageType, boolean declared, String fieldName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, ClassNotFoundException {
        return getValue(instance, packageType.getClass(className), declared, fieldName);
    }

    public Object getValue(Object instance, boolean declared, String fieldName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        return getValue(instance, instance.getClass(), declared, fieldName);
    }

    public void setValue(Object instance, Class<?> clazz, boolean declared, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        getField(clazz, declared, fieldName).set(instance, value);
    }

    public void setValue(Object instance, String className, PackageType packageType, boolean declared, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, ClassNotFoundException {
        setValue(instance, packageType.getClass(className), declared, fieldName, value);
    }

    public void setValue(Object instance, boolean declared, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        setValue(instance, instance.getClass(), declared, fieldName, value);
    }

    @Getter
    public enum PackageType {
        MINECRAFT_SERVER("net.minecraft.server." + getServerVersion()),
        CRAFTBUKKIT("org.bukkit.craftbukkit." + getServerVersion()),
        CRAFTBUKKIT_BLOCK(CRAFTBUKKIT + "block"),
        CRAFTBUKKIT_CHUNKIO(CRAFTBUKKIT + "chunkio"),
        CRAFTBUKKIT_COMMAND(CRAFTBUKKIT + "command"),
        CRAFTBUKKIT_CONVERSATIONS(CRAFTBUKKIT + "conversations"),
        CRAFTBUKKIT_ENCHANTMENS(CRAFTBUKKIT + "enchantments"),
        CRAFTBUKKIT_ENTITY(CRAFTBUKKIT + "entity"),
        CRAFTBUKKIT_EVENT(CRAFTBUKKIT + "event"),
        CRAFTBUKKIT_GENERATOR(CRAFTBUKKIT + "generator"),
        CRAFTBUKKIT_HELP(CRAFTBUKKIT + "help"),
        CRAFTBUKKIT_INVENTORY(CRAFTBUKKIT + "inventory"),
        CRAFTBUKKIT_MAP(CRAFTBUKKIT + "map"),
        CRAFTBUKKIT_METADATA(CRAFTBUKKIT + "metadata"),
        CRAFTBUKKIT_POTION(CRAFTBUKKIT + "potion"),
        CRAFTBUKKIT_PROJECTILES(CRAFTBUKKIT + "projectiles"),
        CRAFTBUKKIT_SCHEDULER(CRAFTBUKKIT + "scheduler"),
        CRAFTBUKKIT_SCOREBOARD(CRAFTBUKKIT + "scoreboard"),
        CRAFTBUKKIT_UPDATER(CRAFTBUKKIT + "updater"),
        CRAFTBUKKIT_UTIL(CRAFTBUKKIT + "util");

        private final String path;

        PackageType(String path) {
            this.path = path;
        }

        public Class<?> getClass(String className) throws ClassNotFoundException {
            return Class.forName(this + "." + className);
        }

        @Override
        public String toString() {
            return this.path;
        }

        public static String getServerVersion() {
            return PmWoodCutter.get().getServer().getClass().getPackage().getName().substring(23);
        }
    }

    @Getter
    public enum DataType {
        BYTE(byte.class, Byte.class),
        SHORT(short.class, Short.class),
        INTEGER(int.class, Integer.class),
        LONG(long.class, Long.class),
        CHARACTER(char.class, Character.class),
        FLOAT(float.class, Float.class),
        DOUBLE(double.class, Double.class),
        BOOLEAN(boolean.class, Boolean.class);

        private static final Map<Class<?>, DataType> CLASS_MAP = new HashMap<>();

        private final Class<?> primitive;

        private final Class<?> reference;

        static {
            byte b;
            int i;
            DataType[] arrayOfDataType;
            for (i = (arrayOfDataType = values()).length, b = 0; b < i; ) {
                DataType type = arrayOfDataType[b];
                CLASS_MAP.put(type.primitive, type);
                CLASS_MAP.put(type.reference, type);
                b++;
            }
        }

        DataType(Class<?> primitive, Class<?> reference) {
            this.primitive = primitive;
            this.reference = reference;
        }

        public static DataType fromClass(Class<?> clazz) {
            return CLASS_MAP.get(clazz);
        }

        public static Class<?> getPrimitive(Class<?> clazz) {
            DataType type = fromClass(clazz);
            return (type == null) ? clazz : type.getPrimitive();
        }

        public Class<?> getReference(Class<?> clazz) {
            DataType type = fromClass(clazz);
            return (type == null) ? clazz : type.getReference();
        }

        public static Class<?>[] getPrimitive(Class<?>[] classes) {
            int length = (classes == null) ? 0 : classes.length;
            Class<?>[] types = new Class[length];
            for (int index = 0; index < length; index++)
                types[index] = getPrimitive(classes[index]);
            return types;
        }

        public Class<?>[] getReference(Class<?>[] classes) {
            int length = (classes == null) ? 0 : classes.length;
            Class<?>[] types = new Class[length];
            for (int index = 0; index < length; index++)
                types[index] = getReference(classes[index]);
            return types;
        }

        public static Class<?>[] getPrimitive(Object[] objects) {
            int length = (objects == null) ? 0 : objects.length;
            Class<?>[] types = new Class[length];
            for (int index = 0; index < length; index++)
                types[index] = getPrimitive(objects[index].getClass());
            return types;
        }

        public Class<?>[] getReference(Object[] objects) {
            int length = (objects == null) ? 0 : objects.length;
            Class<?>[] types = new Class[length];
            for (int index = 0; index < length; index++)
                types[index] = getReference(objects[index].getClass());
            return types;
        }

        public static boolean compare(Class<?>[] primary, Class<?>[] secondary) {
            if (primary == null || secondary == null || primary.length != secondary.length)
                return false;
            for (int index = 0; index < primary.length; ) {
                Class<?> primaryClass = primary[index];
                Class<?> secondaryClass = secondary[index];
                if (primaryClass.equals(secondaryClass) || primaryClass.isAssignableFrom(secondaryClass)) {
                    index++;
                    continue;
                }
                return false;
            }
            return true;
        }
    }
}