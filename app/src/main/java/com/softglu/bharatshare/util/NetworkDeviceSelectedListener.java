package com.softglu.bharatshare.util;

import com.softglu.bharatshare.model.NetworkDevice;

public interface NetworkDeviceSelectedListener
{
    boolean onNetworkDeviceSelected(NetworkDevice networkDevice, NetworkDevice.Connection connection);

    boolean isListenerEffective();
}
