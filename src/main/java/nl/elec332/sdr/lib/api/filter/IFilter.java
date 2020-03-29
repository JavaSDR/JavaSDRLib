package nl.elec332.sdr.lib.api.filter;

import nl.elec332.sdr.lib.api.util.IDoubleArray;

/**
 * Created by Elec332 on 18-2-2020
 */
public interface IFilter {

    double filter(double value);

    void filter(IDoubleArray data, int length);

    int getFilterLength();

    double[] getFilter();

}
