package com.cctv.controlcenter.api;

import com.cctv.controlcenter.service.CameraService;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CameraController}.
 */
public class CameraController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'cameraController'.
   */
  private static BeanInstanceSupplier<CameraController> getCameraControllerInstanceSupplier() {
    return BeanInstanceSupplier.<CameraController>forConstructor(CameraService.class)
            .withGenerator((registeredBean, args) -> new CameraController(args.get(0)));
  }

  /**
   * Get the bean definition for 'cameraController'.
   */
  public static BeanDefinition getCameraControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CameraController.class);
    beanDefinition.setInstanceSupplier(getCameraControllerInstanceSupplier());
    return beanDefinition;
  }
}
