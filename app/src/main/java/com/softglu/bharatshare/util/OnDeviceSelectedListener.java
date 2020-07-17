package com.softglu.bharatshare.util;

import com.softglu.bharatshare.model.NetworkDevice;

import java.util.List;

public interface OnDeviceSelectedListener
{
    void onDeviceSelected(NetworkDevice.Connection connection, List<NetworkDevice.Connection> availableInterfaces);
}
