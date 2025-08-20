package com.cctv.controlcenter.web;

import com.cctv.controlcenter.service.CameraService;
import com.cctv.controlcenter.service.EventService;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link EventWebController}.
 */
public class EventWebController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'eventWebController'.
   */
  private static BeanInstanceSupplier<EventWebController> getEventWebControllerInstanceSupplier() {
    return BeanInstanceSupplier.<EventWebController>forConstructor(EventService.class, CameraService.class)
            .withGenerator((registeredBean, args) -> new EventWebController(args.get(0), args.get(1)));
  }

  /**
   * Get the bean definition for 'eventWebController'.
   */
  public static BeanDefinition getEventWebControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(EventWebController.class);
    beanDefinition.setInstanceSupplier(getEventWebControllerInstanceSupplier());
    return beanDefinition;
  }
}
