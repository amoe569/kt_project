package com.cctv.controlcenter.api;

import com.cctv.controlcenter.service.EventService;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link EventController}.
 */
public class EventController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'eventController'.
   */
  private static BeanInstanceSupplier<EventController> getEventControllerInstanceSupplier() {
    return BeanInstanceSupplier.<EventController>forConstructor(EventService.class)
            .withGenerator((registeredBean, args) -> new EventController(args.get(0)));
  }

  /**
   * Get the bean definition for 'eventController'.
   */
  public static BeanDefinition getEventControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(EventController.class);
    beanDefinition.setInstanceSupplier(getEventControllerInstanceSupplier());
    return beanDefinition;
  }
}
