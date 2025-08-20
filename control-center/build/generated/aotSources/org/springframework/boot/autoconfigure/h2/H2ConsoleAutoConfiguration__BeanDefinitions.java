package org.springframework.boot.autoconfigure.h2;

import org.h2.server.web.JakartaWebServlet;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.core.ResolvableType;

/**
 * Bean definitions for {@link H2ConsoleAutoConfiguration}.
 */
public class H2ConsoleAutoConfiguration__BeanDefinitions {
  /**
   * Get the bean definition for 'h2ConsoleAutoConfiguration'.
   */
  public static BeanDefinition getHConsoleAutoConfigurationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(H2ConsoleAutoConfiguration.class);
    beanDefinition.setInstanceSupplier(H2ConsoleAutoConfiguration::new);
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'h2Console'.
   */
  private static BeanInstanceSupplier<ServletRegistrationBean> getHConsoleInstanceSupplier() {
    return BeanInstanceSupplier.<ServletRegistrationBean>forFactoryMethod(H2ConsoleAutoConfiguration.class, "h2Console", H2ConsoleProperties.class, ObjectProvider.class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean(H2ConsoleAutoConfiguration.class).h2Console(args.get(0), args.get(1)));
  }

  /**
   * Get the bean definition for 'h2Console'.
   */
  public static BeanDefinition getHConsoleBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(ServletRegistrationBean.class);
    beanDefinition.setTargetType(ResolvableType.forClassWithGenerics(ServletRegistrationBean.class, JakartaWebServlet.class));
    beanDefinition.setInstanceSupplier(getHConsoleInstanceSupplier());
    return beanDefinition;
  }
}
