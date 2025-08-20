package com.cctv.controlcenter.service;

import com.cctv.controlcenter.repository.CameraRepository;
import com.cctv.controlcenter.repository.EventRepository;
import com.cctv.controlcenter.repository.VideoRepository;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link EventService}.
 */
public class EventService__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'eventService'.
   */
  private static BeanInstanceSupplier<EventService> getEventServiceInstanceSupplier() {
    return BeanInstanceSupplier.<EventService>forConstructor(EventRepository.class, CameraRepository.class, VideoRepository.class)
            .withGenerator((registeredBean, args) -> new EventService(args.get(0), args.get(1), args.get(2)));
  }

  /**
   * Get the bean definition for 'eventService'.
   */
  public static BeanDefinition getEventServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(EventService.class);
    beanDefinition.setInstanceSupplier(getEventServiceInstanceSupplier());
    return beanDefinition;
  }
}
