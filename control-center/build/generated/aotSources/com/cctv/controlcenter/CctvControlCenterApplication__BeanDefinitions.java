package com.cctv.controlcenter;

import javax.sql.DataSource;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ConfigurationClassUtils;

/**
 * Bean definitions for {@link CctvControlCenterApplication}.
 */
public class CctvControlCenterApplication__BeanDefinitions {
  /**
   * Get the bean definition for 'cctvControlCenterApplication'.
   */
  public static BeanDefinition getCctvControlCenterApplicationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CctvControlCenterApplication.class);
    beanDefinition.setTargetType(CctvControlCenterApplication.class);
    ConfigurationClassUtils.initializeConfigurationClass(CctvControlCenterApplication.class);
    beanDefinition.setInstanceSupplier(CctvControlCenterApplication$$SpringCGLIB$$0::new);
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'dataLoader'.
   */
  private static BeanInstanceSupplier<CommandLineRunner> getDataLoaderInstanceSupplier() {
    return BeanInstanceSupplier.<CommandLineRunner>forFactoryMethod(CctvControlCenterApplication.class, "dataLoader", DataSource.class)
            .withGenerator((registeredBean, args) -> registeredBean.getBeanFactory().getBean(CctvControlCenterApplication.class).dataLoader(args.get(0)));
  }

  /**
   * Get the bean definition for 'dataLoader'.
   */
  public static BeanDefinition getDataLoaderBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(CommandLineRunner.class);
    beanDefinition.setInstanceSupplier(getDataLoaderInstanceSupplier());
    return beanDefinition;
  }
}
