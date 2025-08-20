package com.cctv.controlcenter.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ConfigurationClassUtils;

/**
 * Bean definitions for {@link OpenApiConfig}.
 */
public class OpenApiConfig__BeanDefinitions {
  /**
   * Get the bean definition for 'openApiConfig'.
   */
  public static BeanDefinition getOpenApiConfigBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(OpenApiConfig.class);
    beanDefinition.setTargetType(OpenApiConfig.class);
    ConfigurationClassUtils.initializeConfigurationClass(OpenApiConfig.class);
    beanDefinition.setInstanceSupplier(OpenApiConfig$$SpringCGLIB$$0::new);
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'customOpenAPI'.
   */
  private static BeanInstanceSupplier<OpenAPI> getCustomOpenAPIInstanceSupplier() {
    return BeanInstanceSupplier.<OpenAPI>forFactoryMethod(OpenApiConfig.class, "customOpenAPI")
            .withGenerator((registeredBean) -> registeredBean.getBeanFactory().getBean(OpenApiConfig.class).customOpenAPI());
  }

  /**
   * Get the bean definition for 'customOpenAPI'.
   */
  public static BeanDefinition getCustomOpenAPIBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(OpenAPI.class);
    beanDefinition.setInstanceSupplier(getCustomOpenAPIInstanceSupplier());
    return beanDefinition;
  }
}
