package com.cctv.controlcenter.web;

import com.cctv.controlcenter.service.CameraService;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link CameraViewController}.
 */
public class CameraViewController__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'cameraViewController'.
   */
  private static BeanInstanceSupplier<CameraViewController> getCameraViewControllerInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<CameraViewController>forConstructor(CameraService.class)
            .withGenerator((registeredBean, args) -> new CameraViewController(args.get(0)));
  }

  /**
   * Get the bean definition for 'cameraViewController'.
   */
  public static BeanDefinition getCameraViewControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CameraViewController.class);
    beanDefinition.setInstanceSupplier(getCameraViewControllerInstanceSupplier());
    return beanDefinition;
  }
}
