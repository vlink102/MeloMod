package me.vlink102.melomod.events.chatcooldownmanager.chatwindow;

import java.lang.reflect.Field;

public class FieldWrapper<T> {
    private static Field modifiersField;
    
    private Field field;
    
    static {
        try {
            modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
        } catch (NoSuchFieldException|SecurityException e) {
            e.printStackTrace();
        }
    }
    
    public FieldWrapper(String fieldName, Class<?> clazz) {
        try {
            this.field = clazz.getDeclaredField(fieldName);
            this.field.setAccessible(true);
        } catch (NoSuchFieldException|SecurityException e) {
            e.printStackTrace();
        }
    }
    
    public T get(Object obj) {
        try {
            return (T)this.field.get(obj);
        } catch (IllegalArgumentException|IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void set(Object obj, T value) {
        try {
            this.field.set(obj, value);
        } catch (IllegalArgumentException|IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    public void setFinal(Object obj, T value) {
        try {
            modifiersField.setInt(this.field, this.field.getModifiers() & 0xFFFFFFEF);
            this.field.set(obj, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
