package nl.elec332.sdr.lib;

import com.google.common.collect.Sets;
import nl.elec332.sdr.lib.api.ISourceManager;
import nl.elec332.sdr.lib.api.source.IInputHandler;

import java.util.Collection;
import java.util.Collections;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 21-4-2020
 */
enum SourceManager implements ISourceManager {

    INSTANCE;

    static void load() {
    }

    SourceManager() {
        Set<IInputHandler<?>> list = Sets.newHashSet();
        ServiceLoader.load(IInputHandler.class).stream()
                .map(ServiceLoader.Provider::get)
                .forEach(list::add);
        allRegisteredHandlers = Collections.unmodifiableCollection(list);
    }

    private final Collection<IInputHandler<?>> allRegisteredHandlers;

    @Override
    public Collection<IInputHandler<?>> getInputHandlers() {
        return allRegisteredHandlers;
    }

    @Override
    public Collection<IInputHandler<?>> getInputHandlers(Predicate<IInputHandler<?>> filter) {
        return allRegisteredHandlers.stream()
                .filter(filter)
                .collect(Collectors.toUnmodifiableSet());
    }

}
