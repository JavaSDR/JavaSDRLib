package nl.elec332.sdr.lib.api;

import nl.elec332.sdr.lib.api.source.IInputHandler;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * Created by Elec332 on 21-4-2020
 */
public interface ISourceManager {

    Collection<IInputHandler<?>> getInputHandlers();

    Collection<IInputHandler<?>> getInputHandlers(Predicate<IInputHandler<?>> filter);

}
