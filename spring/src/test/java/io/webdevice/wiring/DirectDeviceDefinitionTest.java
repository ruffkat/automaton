package io.webdevice.wiring;

import io.webdevice.device.DirectDeviceProvider;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

import static io.webdevice.util.Collections.mapOf;
import static io.webdevice.util.Collections.setOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.remote.DesiredCapabilities.iphone;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

public class DirectDeviceDefinitionTest
        implements DeviceDefinitionTest {
    private DeviceDefinition definition;

    @Before
    public void setUp() {
        definition = new DeviceDefinition();
    }

    // No specified capabilities

    @Test
    public void shouldBuildDefinitionWithoutCapabilitiesAndWithoutConfidential() {
        AbstractBeanDefinition actual = definition.withName("myDevice")
                .withDriver(FirefoxDriver.class)
                .build()
                .getBeanDefinition();

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(DirectDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addConstructorArgValue(FirefoxDriver.class)
                        .setInitMethodName("initialize")
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithoutCapabilitiesAndWithConfidential() {
        AbstractBeanDefinition actual = definition.withName("myDevice")
                .withDriver(FirefoxDriver.class)
                .withConfidential("accessKey")
                .build()
                .getBeanDefinition();

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(DirectDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addConstructorArgValue(FirefoxDriver.class)
                        .setInitMethodName("initialize")
                        .addPropertyValue("confidential", setOf("accessKey"))
                        .getBeanDefinition());
    }

    // Capabilities originating from bean in context

    @Test
    public void shouldBuildDefinitionWithCapabilitiesReference() {
        AbstractBeanDefinition actual = definition.withName("myDevice")
                .withDriver(FirefoxDriver.class)
                .withCapabilitiesRef("myDeviceCapabilities")
                .build()
                .getBeanDefinition();

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(DirectDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addConstructorArgValue(FirefoxDriver.class)
                        .setInitMethodName("initialize")
                        .addPropertyReference("capabilities", "myDeviceCapabilities")
                        .getBeanDefinition());
    }

    // Capabilities originating from options

    @Test
    public void shouldBuildDefinitionWithOptionsOnly() {
        AbstractBeanDefinition actual = definition.withName("myDevice")
                .withDriver(FirefoxDriver.class)
                .withOptions(FirefoxOptions.class)
                .build()
                .getBeanDefinition();

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(DirectDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addConstructorArgValue(FirefoxDriver.class)
                        .setInitMethodName("initialize")
                        .addPropertyValue("capabilities", new FirefoxOptions())
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithOptionsMergingCapabilities() {
        AbstractBeanDefinition actual = definition.withName("myDevice")
                .withDriver(FirefoxDriver.class)
                .withOptions(FirefoxOptions.class)
                .withCapability("key", "value")
                .build()
                .getBeanDefinition();

        FirefoxOptions expectedOptions = new FirefoxOptions();
        expectedOptions.setCapability("key", "value");

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(DirectDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addConstructorArgValue(FirefoxDriver.class)
                        .setInitMethodName("initialize")
                        .addPropertyValue("capabilities", expectedOptions)
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithOptionsMergingExtraCapabilities() {
        AbstractBeanDefinition actual = definition.withName("myDevice")
                .withDriver(FirefoxDriver.class)
                .withOptions(FirefoxOptions.class)
                .withExtraCapability("sauce:options")
                .withExtraOption("accessKey", "2secret4u")
                .build()
                .getBeanDefinition();

        FirefoxOptions expectedOptions = new FirefoxOptions();
        expectedOptions.setCapability("sauce:options",
                new DesiredCapabilities(mapOf("accessKey", "2secret4u")));

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(DirectDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addConstructorArgValue(FirefoxDriver.class)
                        .setInitMethodName("initialize")
                        .addPropertyValue("capabilities", expectedOptions)
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithOptionsMergingCapabilitiesAndExtraCapabilities() {
        AbstractBeanDefinition actual = definition.withName("myDevice")
                .withDriver(FirefoxDriver.class)
                .withOptions(FirefoxOptions.class)
                .withCapability("key", "value")
                .withExtraCapability("sauce:options")
                .withExtraOption("accessKey", "2secret4u")
                .build()
                .getBeanDefinition();

        FirefoxOptions expectedOptions = new FirefoxOptions();
        expectedOptions.setCapability("key", "value");
        expectedOptions.setCapability("sauce:options",
                new DesiredCapabilities(mapOf("accessKey", "2secret4u")));

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(DirectDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addConstructorArgValue(FirefoxDriver.class)
                        .setInitMethodName("initialize")
                        .addPropertyValue("capabilities", expectedOptions)
                        .getBeanDefinition());
    }

    // Capabilities originating from DesiredCapabilities.xxx()

    @Test
    public void shouldBuildDefinitionWithDesiredOnly() {
        AbstractBeanDefinition actual = definition.withName("myDevice")
                .withDriver(FirefoxDriver.class)
                .withDesired("iphone")
                .build()
                .getBeanDefinition();

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(DirectDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addConstructorArgValue(FirefoxDriver.class)
                        .setInitMethodName("initialize")
                        .addPropertyValue("capabilities", iphone())
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithDesiredMergingCapabilities() {
        AbstractBeanDefinition actual = definition.withName("myDevice")
                .withDriver(FirefoxDriver.class)
                .withDesired("iphone")
                .withCapability("key", "value")
                .build()
                .getBeanDefinition();

        DesiredCapabilities expectedCapabilities = iphone();
        expectedCapabilities.setCapability("key", "value");

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(DirectDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addConstructorArgValue(FirefoxDriver.class)
                        .setInitMethodName("initialize")
                        .addPropertyValue("capabilities", expectedCapabilities)
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithDesiredMergingExtraCapabilities() {
        AbstractBeanDefinition actual = definition.withName("myDevice")
                .withDriver(FirefoxDriver.class)
                .withDesired("iphone")
                .withExtraCapability("sauce:options")
                .withExtraOption("accessKey", "2secret4u")
                .build()
                .getBeanDefinition();

        DesiredCapabilities expectedCapabilities = iphone();
        expectedCapabilities.setCapability("sauce:options",
                new DesiredCapabilities(mapOf("accessKey", "2secret4u")));

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(DirectDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addConstructorArgValue(FirefoxDriver.class)
                        .setInitMethodName("initialize")
                        .addPropertyValue("capabilities", expectedCapabilities)
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithDesiredMergingCapabilitiesAndExtraCapabilities() {
        AbstractBeanDefinition actual = definition.withName("myDevice")
                .withDriver(FirefoxDriver.class)
                .withDesired("iphone")
                .withCapability("key", "value")
                .withExtraCapability("sauce:options")
                .withExtraOption("accessKey", "2secret4u")
                .build()
                .getBeanDefinition();

        DesiredCapabilities expectedCapabilities = iphone();
        expectedCapabilities.setCapability("key", "value");
        expectedCapabilities.setCapability("sauce:options",
                new DesiredCapabilities(mapOf("accessKey", "2secret4u")));

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(DirectDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addConstructorArgValue(FirefoxDriver.class)
                        .setInitMethodName("initialize")
                        .addPropertyValue("capabilities", expectedCapabilities)
                        .getBeanDefinition());
    }


    // Capabilities originating from Map

    @Test
    public void shouldBuildDefinitionWithMapOnly() {
        AbstractBeanDefinition actual = definition.withName("myDevice")
                .withDriver(FirefoxDriver.class)
                .withCapability("key", "value")
                .build()
                .getBeanDefinition();

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(DirectDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addConstructorArgValue(FirefoxDriver.class)
                        .setInitMethodName("initialize")
                        .addPropertyValue("capabilities", new DesiredCapabilities(mapOf("key", "value")))
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithMapMergingExtraCapabilities() {
        AbstractBeanDefinition actual = definition.withName("myDevice")
                .withDriver(FirefoxDriver.class)
                .withCapability("key", "value")
                .withExtraCapability("sauce:options")
                .withExtraOption("accessKey", "2secret4u")
                .build()
                .getBeanDefinition();

        DesiredCapabilities expectedCapabilities = new DesiredCapabilities(mapOf("key", "value"));
        expectedCapabilities.setCapability("sauce:options",
                new DesiredCapabilities(mapOf("accessKey", "2secret4u")));

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(DirectDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addConstructorArgValue(FirefoxDriver.class)
                        .setInitMethodName("initialize")
                        .addPropertyValue("capabilities", expectedCapabilities)
                        .getBeanDefinition());
    }
}
