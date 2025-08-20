package org.springframework.boot.autoconfigure.h2;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link H2ConsoleProperties}.
 */
public class H2ConsoleProperties__BeanDefinitions {
  /**
   * Get the bean definition for 'h2ConsoleProperties'.
   */
  public static BeanDefinition getHConsolePropertiesBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(H2ConsoleProperties.class);
    beanDefinition.setInstanceSupplier(H2ConsoleProperties::new);
    return beanDefinition;
  }
}
