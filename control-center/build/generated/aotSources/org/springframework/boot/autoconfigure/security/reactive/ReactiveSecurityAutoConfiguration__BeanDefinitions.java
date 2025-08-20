package org.springframework.boot.autoconfigure.security.reactive;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link ReactiveSecurityAutoConfiguration}.
 */
public class ReactiveSecurityAutoConfiguration__BeanDefinitions {
  /**
   * Get the bean definition for 'reactiveSecurityAutoConfiguration'.
   */
  public static BeanDefinition getReactiveSecurityAutoConfigurationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ReactiveSecurityAutoConfiguration.class);
    beanDefinition.setInstanceSupplier(ReactiveSecurityAutoConfiguration::new);
    return beanDefinition;
  }
}
