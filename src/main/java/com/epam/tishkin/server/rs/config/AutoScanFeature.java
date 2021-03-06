package com.epam.tishkin.server.rs.config;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.Populator;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ClasspathDescriptorFileFinder;
import org.glassfish.hk2.utilities.DuplicatePostProcessor;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AutoScanFeature implements Feature {

    @Inject
    ServiceLocator serviceLocator;

    @Override
    public boolean configure(FeatureContext context) {

        DynamicConfigurationService dcs =
                serviceLocator.getService(DynamicConfigurationService.class);
        Populator populator = dcs.getPopulator();
        try {
            populator.populate(
                    new ClasspathDescriptorFileFinder(this.getClass().getClassLoader()),
                    new DuplicatePostProcessor());

        } catch (IOException | MultiException ex) {
            Logger.getLogger(AutoScanFeature.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
}
