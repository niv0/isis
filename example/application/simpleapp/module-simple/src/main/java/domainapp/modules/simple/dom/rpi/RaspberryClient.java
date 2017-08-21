package domainapp.modules.simple.dom.rpi;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.core.runtime.system.context.IsisContext;

import domainapp.modules.simple.dom.impl.SimpleObject;
import lombok.extern.slf4j.Slf4j;

@DomainService(nature = NatureOfService.DOMAIN)
@Slf4j
public class RaspberryClient implements GpioPinListenerDigital {

    private GpioController gpio = null;
    private GpioPinDigitalInput pinPushKey;

    @PostConstruct
    public void initializeRpiGpioDevices() {
        if (gpio == null) {
            if (!System.getProperty("os.name").startsWith("Linux")) {
                log.warn("Recognized operating system: " + System.getProperty("os.name") + ",RPI GPIO deactivated");
                return;
            }

            gpio = GpioFactory.getInstance();
            if (gpio == null) {
                log.warn("RPI GPIO controller initialization failed!");
                return;
            }
            log.info("RPI GPIO controller initialized");
        }

        pinPushKey = gpio
                .provisionDigitalInputPin(RaspiPin.getPinByAddress(16), "GPIO_16_PUSH_KEY", PinPullResistance.PULL_UP);
        // hornPushedKeyListener = new HornPushKeyListener(this);
        pinPushKey.addListener(this);
    }

    @PreDestroy
    public void shutdown() {
        if (gpio == null) {
            return;
        }
        log.info("Shutting down RPI GPIO controller");
        gpio.shutdown();
    }

    @Override public void handleGpioPinDigitalStateChangeEvent(final GpioPinDigitalStateChangeEvent gpioPinDigitalStateChangeEvent) {
        log.debug("Push key on GPIO 16, pressed");
        IsisContext.getSessionFactory().doInSession(() -> {
            // just a test to check if we can access to persistence layer when GPIO event handler fired
            log.info("Found devices" + repositoryService.allInstances(SimpleObject.class).size());
        });
    }

    @Inject RepositoryService repositoryService;

}