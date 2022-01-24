package com.unleqitq.videocall.client;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings ("ALL")
public class ReflectionSession {
	
	Object instance;
	
	public ReflectionSession(Object instance) {
		this.instance = instance;
	}
	
	public ReflectionSession setField(String fieldName, Object value) {
		try {
			Field field = instance.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(instance, value);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
		}
		
		return this;
	}
	
	public <T> T getField(String fieldName, Class<T> type) {
		try {
			Field declaredField = instance.getClass().getDeclaredField(fieldName);
			declaredField.setAccessible(true);
			
			return (T) declaredField.get(instance);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public <A extends Annotation> Method getMethodWithAnnotation(Class<A> type) {
		Method[] declaredMethods = instance.getClass().getDeclaredMethods();
		for (Method declaredMethod : declaredMethods) {
			A annotation = declaredMethod.getAnnotation(type);
			if (annotation != null) {
				return declaredMethod;
			}
		}
		
		return null;
	}
	
	public Method getMethod(String name) {
		Method[] declaredMethods = instance.getClass().getDeclaredMethods();
		for (Method declaredMethod : declaredMethods) {
			if (declaredMethod.getName().equals(name)) {
				return declaredMethod;
			}
		}
		
		return null;
	}
	
}
