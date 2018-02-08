package io.nuls.network.test;

import io.nuls.core.MicroKernelBootstrap;
import io.nuls.core.module.manager.ServiceManager;
import io.nuls.network.module.impl.NetworkModuleBootstrap;
import io.nuls.network.service.NetworkService;
import org.junit.BeforeClass;
import org.junit.Test;

public class NetworkModuleTest {

    private static NetworkModuleBootstrap networkModule;

    private static NetworkService networkService;

    @BeforeClass
    public static void init() {

        networkModule = new NetworkModuleBootstrap();
        networkModule.init();
        networkModule.start();
    }

    @Test
    public void testStart() {

    }
}
