package org.fluentlenium.adapter;

import lombok.experimental.Delegate;
import org.fluentlenium.configuration.Configuration;
import org.fluentlenium.configuration.ConfigurationFactoryProvider;
import org.fluentlenium.configuration.ConfigurationRead;
import org.fluentlenium.configuration.WebDrivers;
import org.fluentlenium.core.FluentDriver;
import org.fluentlenium.core.FluentDriverControl;
import org.openqa.selenium.WebDriver;

public class FluentAdapter implements FluentDriverControl, ConfigurationRead {

    private final DriverContainer driverContainer;

    private final Configuration configuration = ConfigurationFactoryProvider.newConfiguration(getClass());

    public FluentAdapter() {
        this(new DefaultDriverContainer());
    }

    public FluentAdapter(DriverContainer driverContainer) {
        this.driverContainer = driverContainer;
    }

    public FluentAdapter(WebDriver webDriver) {
        this(new DefaultDriverContainer(), webDriver);
    }

    public FluentAdapter(DriverContainer driverContainer, WebDriver webDriver) {
        this.driverContainer = driverContainer;
        initFluent(webDriver);
    }

    @Delegate(types = ConfigurationRead.class)
    public Configuration getConfiguration() {
        return configuration;
    }

    @Delegate(types = FluentDriverControl.class)
    private FluentDriver getFluentDriver() {
        return getDriverContainer().getFluentDriver();
    }

    boolean isFluentDriverAvailable() {
        return getDriverContainer().getFluentDriver() != null;
    }

    private void setFluentDriver(FluentDriver driver) {
        getDriverContainer().setFluentDriver(driver);
    }

    protected DriverContainer getDriverContainer() {
        return driverContainer;
    }

    /**
     * Load a {@link WebDriver} into this adapter.
     *
     * @param webDriver webDriver to use.
     * @return adapter
     * @throws IllegalStateException when trying to register a different webDriver that the current one.
     */
    public void initFluent(WebDriver webDriver) {
        if (webDriver == null) {
            releaseFluent();
            return;
        }

        if (getFluentDriver() != null) {
            if (getFluentDriver().getDriver() == webDriver) {
                return;
            }
            if (getFluentDriver().getDriver() != null) {
                throw new IllegalStateException(
                        "Trying to init a WebDriver, but another one is still running");
            }
        }

        FluentDriver fluentDriver = new FluentDriver(webDriver, this);
        setFluentDriver(fluentDriver);
        fluentDriver.inject(this);
    }

    /**
     * Release the current {@link WebDriver} from this adapter.
     */
    public void releaseFluent() {
        if (getFluentDriver() != null) {
            getFluentDriver().releaseFluent();
            setFluentDriver(null);
        }
    }

    /**
     * @return A new WebDriver instance.
     * @see #getDriver()
     * @deprecated Override {@link #newWebDriver()} instead, or consider using {@link #getDriver()} and #ConfigurationRead{@link #getDriver()}
     */
    @Deprecated
    public WebDriver getDefaultDriver() {
        return WebDrivers.INSTANCE.newWebDriver(getWebDriver());
    }

    /**
     * @return A new WebDriver instance.
     * @see #getDriver()
     */
    public WebDriver newWebDriver() {
        return getDefaultDriver();
    }
}
