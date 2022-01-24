package com.unleqitq.videocall.client;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings ("ALL")
public class ReflectionClassSession {
	
	Class instance;
	
	public ReflectionClassSession(Class instance) {
		this.instance = instance;
	}
	
	public ReflectionClassSession setField(String fieldName, Object value) {
		try {
			Field field = instance.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(instance, value);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
		}
		
		return this;
	}
	
	public <T> T getField(String fieldName, Class<T> type) {
		try {
			Field declaredField = instance.getDeclaredField(fieldName);
			declaredField.setAccessible(true);
			
			return (T) declaredField.get(instance);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public <A extends Annotation> Method getMethodWithAnnotation(Class<A> type) {
		Method[] declaredMethods = instance.getDeclaredMethods();
		for (Method declaredMethod : declaredMethods) {
			A annotation = declaredMethod.getAnnotation(type);
			if (annotation != null) {
				return declaredMethod;
			}
		}
		
		return null;
	}
	
	public Method getMethod(String name) {
		Method[] declaredMethods = instance.getDeclaredMethods();
		for (Method declaredMethod : declaredMethods) {
			if (declaredMethod.getName().equals(name)) {
				declaredMethod.setAccessible(true);
				return declaredMethod;
			}
		}
		
		return null;
	}
	
}
