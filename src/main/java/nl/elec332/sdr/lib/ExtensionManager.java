package nl.elec332.sdr.lib;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.ClassPath;
import nl.elec332.sdr.lib.api.IExtensionManager;
import nl.elec332.sdr.lib.api.ISDRExtensionProvider;
import nl.elec332.sdr.lib.api.ImplementationType;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 29-3-2020
 */
enum ExtensionManager implements IExtensionManager {

    INSTANCE;

    static void load() {
    }

    ExtensionManager() {
        this.extensions = Maps.newHashMap();
        this.usedExtensions = Maps.newHashMap();
        this.extensionTypes = Collections.unmodifiableSet(this.extensions.keySet());
        this.providers = Lists.newArrayList();
        this.state = null;
        this.nativePresent = false;
        this.load_();
    }

    private final Map<Class<?>, Map<ImplementationType, Object>> extensions;
    private final Map<Class<?>, Object> usedExtensions;
    private final Set<Class<?>> extensionTypes;
    private final List<ISDRExtensionProvider> providers;
    private State state;
    private boolean nativePresent;

    @SuppressWarnings("UnstableApiUsage")
    private void load_() {
        try {
            providers.addAll(ClassPath.from(getClass().getClassLoader()).getAllClasses().stream()
                    .filter(ci -> {
                        String name = ci.getSimpleName();
                        int e = 0;
                        if (name.contains("SDR")) {
                            e++;
                        }
                        if (name.contains("Provider")) {
                            e++;
                        }
                        if (name.contains("Impl")) {
                            e++;
                        }
                        if (name.contains("Extension")) {
                            e++;
                        }
                        return e >= 2;
                    }).map(ClassPath.ClassInfo::load)
                    .filter(Objects::nonNull)
                    .map(clazz -> {
                        if (clazz == ISDRExtensionProvider.class) {
                            return null;
                        }
                        try {
                            return clazz.newInstance();
                        } catch (Exception e) {
                            System.out.println("Failed to load extension class: " + clazz.getSimpleName());
                            return null;
                        }
                    })
                    .filter(o -> o instanceof ISDRExtensionProvider)
                    .map(o -> (ISDRExtensionProvider) o)
                    .collect(Collectors.toSet()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load extension classes", e);
        }

        this.state = State.PRE_EXT;
        providers.forEach(p -> p.registerExtensions(this));
        this.state = State.REG_EXT;
        providers.forEach(p -> p.registerExtensionImplementations(this));
        this.state = State.DONE;
        providers.forEach(p -> p.afterRegister(this));

        System.out.println("-----");
        System.out.println("Using SDR extension handlers: ");
        providers.forEach(p -> System.out.println("  " + p.getClass().getCanonicalName()));
        System.out.println("Default extension map:");
        for (Class<?> c : getExtensionTypes()) {
            Object o = getImplementation(c, getBestImplementationType(c));
            if (o != null) {
                usedExtensions.put(c, o);
                System.out.println("  " + c.getSimpleName() + " : " + o.getClass().getSimpleName());
            }
        }
        System.out.println("Native implementations present: " + (nativePresent ? "Yes" : "No"));
        System.out.println("-----");
    }

    @Override
    public <T> void addLibraryExtension(Class<T> ext, T defaultImpl) {
        if (this.state == State.PRE_EXT) {
            if (this.extensions.containsKey(ext)) {
                throw new IllegalArgumentException();
            }
            Map<ImplementationType, Object> map = Maps.newEnumMap(ImplementationType.class);
            map.put(ImplementationType.JAVA_DEFAULT, defaultImpl);
            this.extensions.put(ext, map);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public <T> void registerLibraryImplementation(Class<T> ext, T impl, ImplementationType type) {
        if (this.state == State.REG_EXT) {
            if (type == ImplementationType.JAVA_DEFAULT) {
                throw new IllegalArgumentException("Default java implementation cannot be reassigned");
            }
            if (!isExtensionRegistered(ext)) {
                throw new IllegalArgumentException();
            }
            Map<ImplementationType, Object> map = Preconditions.checkNotNull(this.extensions.get(ext));
            if (map.containsKey(type)) {
                throw new IllegalArgumentException();
            }
            map.put(type, Preconditions.checkNotNull(impl));
            if (!this.nativePresent && type.isNative()) {
                this.nativePresent = true;
            }
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public <T> boolean isExtensionRegistered(Class<T> ext) {
        return this.extensions.containsKey(ext);
    }

    @Override
    public <T> boolean hasImplementationType(Class<T> ext, ImplementationType type) {
        if (!isExtensionRegistered(ext)) {
            return false;
        }
        Map<ImplementationType, Object> m = Preconditions.checkNotNull(this.extensions.get(ext));
        return m.containsKey(type) && m.get(type) != null;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getImplementation(Class<T> ext, ImplementationType type) {
        if (this.state == State.DONE) {
            if (!hasImplementationType(ext, type)) {
                return null;
            }
            return (T) Preconditions.checkNotNull(this.extensions.get(ext)).get(type);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBestImplementation(Class<T> ext) {
        return (T) this.usedExtensions.get(ext);
    }

    @Override
    public Set<Class<?>> getExtensionTypes() {
        if (this.state != State.DONE && this.state != State.REG_EXT) {
            throw new IllegalStateException();
        }
        return this.extensionTypes;
    }

    @Override
    public boolean nativeImplementationsPresent() {
        if (this.state != State.DONE) {
            throw new IllegalStateException();
        }
        return this.nativePresent;
    }

    private enum State {

        PRE_EXT, REG_EXT, DONE

    }

}
