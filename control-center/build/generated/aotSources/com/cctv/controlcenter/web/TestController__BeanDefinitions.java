package com.cctv.controlcenter.web;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link TestController}.
 */
public class TestController__BeanDefinitions {
  /**
   * Get the bean definition for 'testController'.
   */
  public static BeanDefinition getTestControllerBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(TestController.class);
    beanDefinition.setInstanceSupplier(TestController::new);
    return beanDefinition;
  }
}
