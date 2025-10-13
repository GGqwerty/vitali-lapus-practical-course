package innowise.java.core;

import innowise.java.core.annotations.Autowired;
import innowise.java.core.annotations.Component;
import innowise.java.core.annotations.Scope;
import innowise.java.core.interfaces.InitializingBean;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MiniApplicationContext {
    private final Map<Class<?>, Object> singletonBeans = new HashMap<>();
    private final Set<Class<?>> componentClasses = new HashSet<>();
    private final Set<Class<?>> prototypeClasses = new HashSet<>();

    public MiniApplicationContext(String basePackage) {
        try {
            loadClasses(basePackage);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public <T> T getBean(Class<T> cls) {
        for (Map.Entry<Class<?>, Object> e : singletonBeans.entrySet()) {
            if (cls.isAssignableFrom(e.getKey())) {
                return cls.cast(e.getValue());
            }
        }
        for (Class<?> component : componentClasses) {
            if (cls.isAssignableFrom(component) && prototypeClasses.contains(component)) {
                return cls.cast(createBean(cls));
            }
        }
        throw new RuntimeException();
    }

    private void loadClasses(String basePackege) throws IOException{
        String path = basePackege.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(path);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if ("file".equals(resource.getProtocol())) {
                loadFromDir(basePackege, new File(resource.getFile()));
            } else if ("jar".equals(resource.getProtocol())) {
                loadJar(basePackege, resource);
            }
        }
    }

    private void loadFromDir(String packageName, File file) {
        if (!file.isDirectory()) {
            return;
        }
        for (File f : Objects.requireNonNull(file.listFiles())) {
            if (f.isDirectory()) {
                loadFromDir(packageName + f.getName(), f);
            } else if (f.getName().endsWith(".class")) {
                try {
                    loadClass(packageName + "." + f.getName().replace(".class", ""));
                } catch (Exception exception) {
                    throw new RuntimeException(exception);
                }
            }
        }
    }

    private void loadJar(String basedir, URL resource){
        try (JarFile jar = new JarFile(new File(resource.getPath().substring(5, resource.getPath().indexOf("!"))))) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.startsWith(basedir) && name.endsWith(".class")) {
                    loadClass(name.replace("/", ".").replace(".class", ""));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void loadClass(String className) throws ClassNotFoundException {
        Class<?> cls = Class.forName(className);
        if (cls.isAnnotationPresent(Component.class)) {
            componentClasses.add(cls);
            Scope scope = cls.getAnnotation(Scope.class);
            if (scope != null && scope.value().equals("prototype")) {
                prototypeClasses.add(cls);
            } else if (scope == null || scope.value().equals("singleton")) {
                singletonBeans.put(cls, createBean(cls));
            }
        }
    }

    private Object createBean(Class<?> cls) {
        try {
            Object instance = cls.getDeclaredConstructor().newInstance();
            for (Field field : cls.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    field.setAccessible(true);
                    field.set(instance, findInject(field.getType()));
                }
            }

            if (instance instanceof InitializingBean bean) {
                bean.afterPropertiesSet();
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object findInject(Class<?> cls) {
        for (Map.Entry<Class<?>, Object> entry : singletonBeans.entrySet()) {
            if (cls.isAssignableFrom(entry.getKey())) {
                return entry.getValue();
            }
        }

        for (Class<?> component : componentClasses) {
            if (cls.isAssignableFrom(component)) {
                if (prototypeClasses.contains(component)) {
                    return createBean(component);
                } else {
                    return singletonBeans.computeIfAbsent(cls, this::createBean);
                }
            }
        }
        throw new RuntimeException();
    }

}
