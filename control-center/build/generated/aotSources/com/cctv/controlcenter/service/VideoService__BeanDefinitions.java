package com.cctv.controlcenter.service;

import com.cctv.controlcenter.repository.CameraRepository;
import com.cctv.controlcenter.repository.VideoRepository;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link VideoService}.
 */
public class VideoService__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'videoService'.
   */
  private static BeanInstanceSupplier<VideoService> getVideoServiceInstanceSupplier() {
    return BeanInstanceSupplier.<VideoService>forConstructor(VideoRepository.class, CameraRepository.class)
            .withGenerator((registeredBean, args) -> new VideoService(args.get(0), args.get(1)));
  }

  /**
   * Get the bean definition for 'videoService'.
   */
  public static BeanDefinition getVideoServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(VideoService.class);
    beanDefinition.setInstanceSupplier(getVideoServiceInstanceSupplier());
    return beanDefinition;
  }
}
