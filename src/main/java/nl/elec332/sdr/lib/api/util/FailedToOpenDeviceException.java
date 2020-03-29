package nl.elec332.sdr.lib.api.util;

/**
 * Created by Elec332 on 18-3-2020
 */
public class FailedToOpenDeviceException extends RuntimeException {

    public FailedToOpenDeviceException(String reason) {
        super(reason);
    }

    public FailedToOpenDeviceException(Throwable t) {
        super(t);
    }

}
