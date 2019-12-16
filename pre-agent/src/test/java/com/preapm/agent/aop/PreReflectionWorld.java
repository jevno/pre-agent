package com.preapm.agent.aop;

import org.aspectj.bridge.AbortException;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.util.LangUtil;
import org.aspectj.weaver.*;
import org.aspectj.weaver.reflect.AnnotationFinder;
import org.aspectj.weaver.reflect.IReflectionWorld;
import org.aspectj.weaver.reflect.ReflectionBasedReferenceTypeDelegateFactory;
import org.aspectj.weaver.reflect.ReflectionWorld;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PreReflectionWorld extends World implements IReflectionWorld {


    private static Map<WeakClassLoaderReference, ReflectionWorld> rworlds = Collections.synchronizedMap(new HashMap<WeakClassLoaderReference, ReflectionWorld>());

    private WeakClassLoaderReference classLoaderReference;
    private AnnotationFinder annotationFinder;
    private boolean mustUseOneFourDelegates = false; // for testing
//    private Map<String,Class<?>> inProgressResolutionClasses = new HashMap<String,Class<?>>();
    private Map<String,Object> inProgressResolutionClasses = new HashMap<String,Object>();

    public static PreReflectionWorld getReflectionWorldFor(WeakClassLoaderReference classLoaderReference) {
        return new PreReflectionWorld(classLoaderReference);
    }

    public static void cleanUpWorlds() {
        synchronized (rworlds) {
            rworlds.clear();
        }
    }

    private PreReflectionWorld() {
    }

    public PreReflectionWorld(WeakClassLoaderReference classloaderRef) {
        this.setMessageHandler(new PreReflectionWorld.ExceptionBasedMessageHandler());
        setBehaveInJava5Way(LangUtil.is15VMOrGreater());
        classLoaderReference = classloaderRef;
        annotationFinder = makeAnnotationFinderIfAny(classLoaderReference.getClassLoader(), this);
    }

    public PreReflectionWorld(ClassLoader aClassLoader) {
        super();
        this.setMessageHandler(new PreReflectionWorld.ExceptionBasedMessageHandler());
        setBehaveInJava5Way(LangUtil.is15VMOrGreater());
        classLoaderReference = new WeakClassLoaderReference(aClassLoader);
        annotationFinder = makeAnnotationFinderIfAny(classLoaderReference.getClassLoader(), this);
    }

    public PreReflectionWorld(boolean forceUseOf14Delegates, ClassLoader aClassLoader) {
        this(aClassLoader);
        this.mustUseOneFourDelegates = forceUseOf14Delegates;
        if (forceUseOf14Delegates) {
            // Dont use 1.4 delegates and yet allow autoboxing
            this.setBehaveInJava5Way(false);
        }
    }

    public static AnnotationFinder makeAnnotationFinderIfAny(ClassLoader loader, World world) {
        AnnotationFinder annotationFinder = null;
        try {
            if (LangUtil.is15VMOrGreater()) {
                Class<?> java15AnnotationFinder = Class.forName("org.aspectj.weaver.reflect.Java15AnnotationFinder");
                annotationFinder = (AnnotationFinder) java15AnnotationFinder.newInstance();
                annotationFinder.setClassLoader(loader);
                annotationFinder.setWorld(world);
            }
        } catch (ClassNotFoundException ex) {
            // must be on 1.4 or earlier
        } catch (IllegalAccessException ex) {
            // not so good
            throw new BCException("AspectJ internal error", ex);
        } catch (InstantiationException ex) {
            throw new BCException("AspectJ internal error", ex);
        }
        return annotationFinder;
    }

    public ClassLoader getClassLoader() {
        return classLoaderReference.getClassLoader();
    }

    public AnnotationFinder getAnnotationFinder() {
        return annotationFinder;
    }

    public ResolvedType resolve(Class aClass) {
        return resolve(this, aClass);
    }

    public static ResolvedType resolve(World world, Class<?> aClass) {
        // classes that represent arrays return a class name that is the
        // signature of the array type, ho-hum...
        String className = aClass.getName();
        if (aClass.isArray()) {
            return world.resolve(UnresolvedType.forSignature(className.replace('.', '/')));
        } else {
            return world.resolve(className);
        }
    }


    public ResolvedType resolveUsingClass(Class<?> clazz) {
        String signature = UnresolvedType.forName(clazz.getName()).getSignature();
        try {
            inProgressResolutionClasses.put(signature, clazz.getName());
            return resolve(clazz.getName());
        } finally {
            inProgressResolutionClasses.remove(signature);
        }
    }

    public ResolvedType resolveUsingClass(String className) {
        String signature = UnresolvedType.forName(className).getSignature();
        try {
            inProgressResolutionClasses.put(signature, className);
            return resolve(className);
        } finally {
            inProgressResolutionClasses.remove(signature);
        }
    }

    protected ReferenceTypeDelegate resolveDelegate(ReferenceType ty) {
        ReferenceTypeDelegate result;
        if (mustUseOneFourDelegates) {
            result = ReflectionBasedReferenceTypeDelegateFactory.create14Delegate(ty, this, classLoaderReference.getClassLoader());
        } else {
            result = ReflectionBasedReferenceTypeDelegateFactory.createDelegate(ty, this, classLoaderReference.getClassLoader());
        }
        if (result == null && inProgressResolutionClasses.size() != 0) {
            // Is it a class that cannot be loaded (i.e. it was generated) but we already know about?
            Class<?> clazz = (Class<?>)inProgressResolutionClasses.get(ty.getSignature());
//            String clazz = (String)inProgressResolutionClasses.get(ty.getSignature());
            if (clazz != null) {
                result = ReflectionBasedReferenceTypeDelegateFactory.createDelegate(ty,this,clazz);
            }
        }
        return result;
    }

    public static class ReflectionWorldException extends RuntimeException {

        private static final long serialVersionUID = -3432261918302793005L;

        public ReflectionWorldException(String message) {
            super(message);
        }
    }

    private static class ExceptionBasedMessageHandler implements IMessageHandler {

        public boolean handleMessage(IMessage message) throws AbortException {
            throw new ReflectionWorld.ReflectionWorldException(message.toString());
        }

        public boolean isIgnoring(org.aspectj.bridge.IMessage.Kind kind) {
            if (kind == IMessage.INFO) {
                return true;
            } else {
                return false;
            }
        }

        public void dontIgnore(org.aspectj.bridge.IMessage.Kind kind) {
        }

        public void ignore(org.aspectj.bridge.IMessage.Kind kind) {
        }

    }

    public IWeavingSupport getWeavingSupport() {
        return null;
    }

    public boolean isLoadtimeWeaving() {
        return true;
    }

}
