package nl.elec332.sdr.lib.api.extensions;

import nl.elec332.sdr.lib.api.filter.IFilter;

/**
 * Created by Elec332 on 18-2-2020
 */
public interface IFilterLibrary {

    IFilter createFilter(double[] filter);

}
