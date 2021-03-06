package org.fluentlenium.adapter;

import com.google.common.base.Supplier;
import static org.assertj.core.api.Assertions.assertThat;
import org.fluentlenium.adapter.util.SharedDriverStrategy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.WebDriver;

import java.util.LinkedHashSet;
import java.util.Set;


public class SharedWebDriverContainerTest implements Supplier<WebDriver> {

    private SharedWebDriverContainer.Impl container;

    @Before
    public void before() {
        container = new SharedWebDriverContainer.Impl();
    }

    @After
    public void after() {
        container.quitAll();
        assertThat(container.getAllDrivers()).isEmpty();
    }

    @Test
    public void getOrCreateDriver_with_same_test_names_creates_one_instance() {
        SharedWebDriver driver = container.getOrCreateDriver(this, Object.class, "test", SharedDriverStrategy.PER_METHOD);

        assertThat(container.getAllDrivers()).containsOnly(driver);
        assertThat(container.getTestClassDrivers(Object.class)).containsOnly(driver);

        SharedWebDriver driver2 = container.getOrCreateDriver(this, Object.class, "test", SharedDriverStrategy.PER_METHOD);

        assertThat(driver).isEqualTo(driver2);
        assertThat(container.getAllDrivers()).containsOnly(driver);
        assertThat(container.getTestClassDrivers(Object.class)).containsOnly(driver);

        container.quit(driver);

        assertThat(container.getAllDrivers()).isEmpty();
        assertThat(container.getTestClassDrivers(Object.class)).isEmpty();
    }

    @Test
    public void getOrCreateDriver_with_different_test_names_creates_distinct_instances() {
        SharedWebDriver driver = container.getOrCreateDriver(this, Object.class, "test", SharedDriverStrategy.PER_METHOD);

        assertThat(container.getAllDrivers()).containsOnly(driver);
        assertThat(container.getTestClassDrivers(Object.class)).containsOnly(driver);

        SharedWebDriver driver2 = container.getOrCreateDriver(this, Object.class, "otherTest", SharedDriverStrategy.PER_METHOD);

        assertThat(driver).isNotEqualTo(driver2);
        assertThat(container.getAllDrivers()).containsOnly(driver, driver2);
        assertThat(container.getTestClassDrivers(Object.class)).containsOnly(driver, driver2);

        assertThat(container.getAllDrivers().size()).isEqualTo(2);
        container.quit(driver);
        assertThat(container.getAllDrivers().size()).isEqualTo(1);
        assertThat(container.getAllDrivers().get(0)).isEqualTo(driver2);
        container.quit(driver2);

        assertThat(container.getAllDrivers()).isEmpty();
        assertThat(container.getTestClassDrivers(Object.class)).isEmpty();
    }

    @Test
    public void getOrCreateDriver_with_different_test_classes_creates_distinct_instances() {
        SharedWebDriver driver = container.getOrCreateDriver(this, Object.class, "test", SharedDriverStrategy.PER_METHOD);

        assertThat(container.getAllDrivers()).containsOnly(driver);
        assertThat(container.getTestClassDrivers(Object.class)).containsOnly(driver);
        assertThat(container.getTestClassDrivers(String.class)).isEmpty();

        SharedWebDriver driver2 = container.getOrCreateDriver(this, String.class, "test", SharedDriverStrategy.PER_METHOD);

        assertThat(driver).isNotEqualTo(driver2);
        assertThat(container.getAllDrivers()).containsOnly(driver, driver2);
        assertThat(container.getTestClassDrivers(Object.class)).containsOnly(driver);
        assertThat(container.getTestClassDrivers(String.class)).containsOnly(driver2);

        assertThat(container.getAllDrivers().size()).isEqualTo(2);
        container.quit(driver);
        assertThat(container.getAllDrivers().size()).isEqualTo(1);
        assertThat(container.getAllDrivers().get(0)).isEqualTo(driver2);
        container.quit(driver2);

        assertThat(container.getAllDrivers()).isEmpty();
        assertThat(container.getTestClassDrivers(Object.class)).isEmpty();
        assertThat(container.getTestClassDrivers(String.class)).isEmpty();
    }

    @Test
    public void getOrCreateDriver_with_different_test_names_and_strategy_per_class_creates_one_instance() {
        SharedWebDriver driver = container.getOrCreateDriver(this, Object.class, "test", SharedDriverStrategy.PER_CLASS);

        assertThat(container.getAllDrivers()).containsOnly(driver);
        assertThat(container.getTestClassDrivers(Object.class)).containsOnly(driver);

        SharedWebDriver driver2 = container.getOrCreateDriver(this, Object.class, "otherTest", SharedDriverStrategy.PER_CLASS);

        assertThat(driver).isEqualTo(driver2);
        assertThat(container.getAllDrivers()).containsOnly(driver);
        assertThat(container.getTestClassDrivers(Object.class)).containsOnly(driver);

        container.quit(driver);

        assertThat(container.getAllDrivers()).isEmpty();
        assertThat(container.getTestClassDrivers(Object.class)).isEmpty();
    }

    @Test
    public void getOrCreateDriver_with_different_test_names_and_different_test_class_and_strategy_per_class_creates_distinct_instance() {
        SharedWebDriver driver = container.getOrCreateDriver(this, Object.class, "test", SharedDriverStrategy.PER_CLASS);

        assertThat(container.getAllDrivers()).containsOnly(driver);
        assertThat(container.getTestClassDrivers(Object.class)).containsOnly(driver);
        assertThat(container.getTestClassDrivers(String.class)).isEmpty();

        SharedWebDriver driver2 = container.getOrCreateDriver(this, String.class, "otherTest", SharedDriverStrategy.PER_CLASS);

        assertThat(driver).isNotEqualTo(driver2);
        assertThat(container.getAllDrivers()).containsOnly(driver, driver2);
        assertThat(container.getTestClassDrivers(Object.class)).containsOnly(driver);
        assertThat(container.getTestClassDrivers(String.class)).containsOnly(driver2);

        assertThat(container.getAllDrivers().size()).isEqualTo(2);
        container.quit(driver2);
        assertThat(container.getAllDrivers().size()).isEqualTo(1);
        assertThat(container.getAllDrivers().get(0)).isEqualTo(driver);
        container.quit(driver);

        assertThat(container.getAllDrivers()).isEmpty();
        assertThat(container.getTestClassDrivers(Object.class)).isEmpty();
        assertThat(container.getTestClassDrivers(String.class)).isEmpty();
    }

    @Test
    public void getOrCreateDriver_with_different_test_names_and_different_test_class_and_strategy_once_creates_one_instance() {
        SharedWebDriver driver = container.getOrCreateDriver(this, Object.class, "test", SharedDriverStrategy.ONCE);

        assertThat(container.getAllDrivers()).containsOnly(driver);
        assertThat(container.getTestClassDrivers(Object.class)).isEmpty();
        assertThat(container.getTestClassDrivers(String.class)).isEmpty();

        SharedWebDriver driver2 = container.getOrCreateDriver(this, String.class, "otherTest", SharedDriverStrategy.ONCE);

        assertThat(driver).isEqualTo(driver2);
        assertThat(container.getAllDrivers()).containsOnly(driver);
        assertThat(container.getTestClassDrivers(Object.class)).isEmpty();
        assertThat(container.getTestClassDrivers(String.class)).isEmpty();

        container.quit(driver);

        assertThat(container.getAllDrivers()).isEmpty();
        assertThat(container.getTestClassDrivers(Object.class)).isEmpty();
        assertThat(container.getTestClassDrivers(String.class)).isEmpty();
    }

    @Test
    public void quitAll_should_quit_all_drivers() {
        SharedWebDriver driver = container.getOrCreateDriver(this, Object.class, "test", SharedDriverStrategy.PER_METHOD);
        SharedWebDriver driver2 = container.getOrCreateDriver(this, String.class, "test", SharedDriverStrategy.PER_METHOD);

        SharedWebDriver driver3 = container.getOrCreateDriver(this, Object.class, "test", SharedDriverStrategy.PER_CLASS);
        SharedWebDriver driver4 = container.getOrCreateDriver(this, String.class, "otherTest", SharedDriverStrategy.PER_CLASS);

        SharedWebDriver driver5 = container.getOrCreateDriver(this, Object.class, "test", SharedDriverStrategy.ONCE);
        SharedWebDriver driver6 = container.getOrCreateDriver(this, String.class, "otherTest", SharedDriverStrategy.ONCE);

        Set<SharedWebDriver> drivers = new LinkedHashSet<>();
        drivers.add(driver);
        drivers.add(driver2);
        drivers.add(driver3);
        drivers.add(driver4);
        drivers.add(driver5);
        drivers.add(driver6);

        assertThat(container.getAllDrivers()).containsOnly(drivers.toArray(new SharedWebDriver[drivers.size()]));
        assertThat(container.getTestClassDrivers(Object.class)).isNotEmpty();
        assertThat(container.getTestClassDrivers(String.class)).isNotEmpty();

        container.quitAll();

        assertThat(container.getAllDrivers()).isEmpty();
        assertThat(container.getTestClassDrivers(Object.class)).isEmpty();
        assertThat(container.getTestClassDrivers(String.class)).isEmpty();
    }

    @Test
    public void testSharedDriverBean() {
        WebDriver webDriver = get();
        Class<Object> testClass = Object.class;
        String testName = "test";
        SharedDriverStrategy strategy = SharedDriverStrategy.PER_METHOD;

        SharedWebDriver sharedWebDriver = new SharedWebDriver(webDriver, testClass, testName, strategy);

        assertThat(sharedWebDriver.getDriver()).isSameAs(webDriver);
        assertThat(sharedWebDriver.getWrappedDriver()).isSameAs(webDriver);
        assertThat(sharedWebDriver.getTestClass()).isSameAs(testClass);
        assertThat(sharedWebDriver.getTestName()).isSameAs(testName);
        assertThat(sharedWebDriver.getSharedDriverStrategy()).isSameAs(strategy);

        assertThat(sharedWebDriver.toString()).contains(webDriver.toString());
    }

    @Override
    public WebDriver get() {
        return Mockito.mock(WebDriver.class);
    }
}
