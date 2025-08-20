package com.cctv.controlcenter.service;

import com.cctv.controlcenter.repository.CameraRepository;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CameraService}.
 */
public class CameraService__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'cameraService'.
   */
  private static BeanInstanceSupplier<CameraService> getCameraServiceInstanceSupplier() {
    return BeanInstanceSupplier.<CameraService>forConstructor(CameraRepository.class)
            .withGenerator((registeredBean, args) -> new CameraService(args.get(0)));
  }

  /**
   * Get the bean definition for 'cameraService'.
   */
  public static BeanDefinition getCameraServiceBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CameraService.class);
    beanDefinition.setInstanceSupplier(getCameraServiceInstanceSupplier());
    return beanDefinition;
  }
}
