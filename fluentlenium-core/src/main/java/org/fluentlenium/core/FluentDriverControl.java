package org.fluentlenium.core;

import org.fluentlenium.configuration.Configuration;
import org.openqa.selenium.internal.WrapsDriver;

import java.util.concurrent.TimeUnit;

/**
 * Control of the Fluent WebDriver
 */
public interface FluentDriverControl extends FluentControl, WrapsDriver {
    /**
     * Define the default url that will be used in the test and in the relative pages
     *
     * @param baseUrl base URL
     * @return Fluent element
     * @deprecated Use {@link Configuration#getDefaultBaseUrl()}
     */
    @Deprecated
    FluentDriverControl withDefaultUrl(String baseUrl);

    /**
     * Define an implicit time to wait for a page to be loaded
     *
     * @param l        timeout value
     * @param timeUnit time unit for wait
     * @return Fluent element
     * @deprecated Use {@link Configuration#getPageLoadTimeout()}
     */
    @Deprecated
    FluentDriverControl withDefaultPageWait(long l, TimeUnit timeUnit);

    /**
     * Define an implicit time to wait when searching an element
     *
     * @param l        timeout value
     * @param timeUnit time unit for wait
     * @return Fluent element
     * @deprecated Use {@link Configuration#getImplicitlyWait()}
     */
    @Deprecated
    FluentDriverControl withDefaultSearchWait(long l, TimeUnit timeUnit);
}
