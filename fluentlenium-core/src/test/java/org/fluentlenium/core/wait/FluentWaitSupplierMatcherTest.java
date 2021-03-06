package org.fluentlenium.core.wait;

import com.google.common.base.Suppliers;
import org.assertj.core.api.ThrowableAssert;
import org.fluentlenium.core.FluentDriver;
import org.fluentlenium.core.conditions.WebElementConditions;
import org.fluentlenium.core.domain.FluentWebElement;
import org.fluentlenium.core.search.Search;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FluentWaitSupplierMatcherTest {
    @Mock
    private Search search;

    @Mock
    private FluentDriver fluent;

    private FluentWait wait;

    @Mock
    private FluentWebElement fluentWebElement;

    @Mock
    private WebElement element;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);

        wait = new FluentWait(fluent, search);
        wait.atMost(1L, TimeUnit.MILLISECONDS);
        wait.pollingEvery(1L, TimeUnit.MILLISECONDS);

        when(fluentWebElement.conditions()).thenReturn(new WebElementConditions(fluentWebElement));
        when(fluentWebElement.getElement()).thenReturn(element);
    }

    @After
    public void after() {
        reset(search);
        reset(fluent);
        reset(fluentWebElement);
        reset(element);
    }

    @Test
    public void isEnabled() {
        final FluentWaitSupplierMatcher matcher = new FluentWaitSupplierMatcher(search, wait, Suppliers.ofInstance(fluentWebElement));
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                matcher.isEnabled();
            }
        }).isExactlyInstanceOf(TimeoutException.class);

        verify(fluentWebElement, atLeastOnce()).isEnabled();

        when(fluentWebElement.isEnabled()).thenReturn(true);
        matcher.isEnabled();

        verify(fluentWebElement, atLeastOnce()).isEnabled();

        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                matcher.not().isEnabled();
            }
        }).isExactlyInstanceOf(TimeoutException.class);
    }
}
