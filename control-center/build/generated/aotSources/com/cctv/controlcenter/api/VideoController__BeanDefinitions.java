package com.cctv.controlcenter.api;

import com.cctv.controlcenter.service.VideoService;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link VideoController}.
 */
public class VideoController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'videoController'.
   */
  private static BeanInstanceSupplier<VideoController> getVideoControllerInstanceSupplier() {
    return BeanInstanceSupplier.<VideoController>forConstructor(VideoService.class)
            .withGenerator((registeredBean, args) -> new VideoController(args.get(0)));
  }

  /**
   * Get the bean definition for 'videoController'.
   */
  public static BeanDefinition getVideoControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(VideoController.class);
    beanDefinition.setInstanceSupplier(getVideoControllerInstanceSupplier());
    return beanDefinition;
  }
}
