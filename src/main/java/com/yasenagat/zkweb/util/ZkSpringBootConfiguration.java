package com.yasenagat.zkweb.util;


import java.beans.PropertyVetoException;
import java.util.Locale;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
public class ZkSpringBootConfiguration {
	@Autowired
    private Environment env;
	
    @Bean(name = "dataSource")
    @Qualifier(value = "dataSource")
    @Primary
    //@ConfigurationProperties(prefix = "c3p0")
    public DataSource getDataSource(){
    	//DruidDataSource dataSource = new DruidDataSource();
    	ComboPooledDataSource dataSource= org.springframework.boot.jdbc.DataSourceBuilder.create().type(ComboPooledDataSource.class).build();
    	dataSource.setJdbcUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUser(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        try {
			dataSource.setDriverClass(env.getProperty("spring.datasource.driver-class-name"));
		} catch (PropertyVetoException e) {
			e.printStackTrace();
			return null;
		}
        dataSource.setInitialPoolSize(Integer.parseInt(env.getProperty("spring.datasource.initial-pool-size")));
        dataSource.setMinPoolSize(Integer.parseInt(env.getProperty("spring.datasource.min-pool-size")));
        dataSource.setMaxPoolSize(Integer.parseInt(env.getProperty("spring.datasource.max-pool-size")));
        dataSource.setAcquireIncrement(Integer.parseInt(env.getProperty("spring.datasource.acquire-increment")));
        dataSource.setIdleConnectionTestPeriod(Integer.parseInt(env.getProperty("spring.datasource.idle-connection-test-period")));
        dataSource.setMaxIdleTime(Integer.parseInt(env.getProperty("spring.datasource.max-idle-time")));
        dataSource.setMaxStatements(Integer.parseInt(env.getProperty("spring.datasource.max-statements")));
        dataSource.setAcquireRetryAttempts(Integer.parseInt(env.getProperty("spring.datasource.acquire-retry-attempts")));
        dataSource.setBreakAfterAcquireFailure(Boolean.parseBoolean(env.getProperty("spring.datasource.break-after-acquire-failure")));
        return dataSource;
    }
    
    @Bean
    public SessionLocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        // 默认语言
        slr.setDefaultLocale(Locale.CHINA);
        return slr;
    }
    
//    @Bean
//    public LocaleChangeInterceptor localeChangeInterceptor() {
//        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
//        // 参数名
//        lci.setParamName("lang");
//        return lci;
//    }
    
//  @Bean
//  public MessageSource messageSource() {
//      //logger.info("MessageSource");
//      ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
//      messageSource.setBasename("config.messages.messages");
//
//      return messageSource;
//  }

  @Bean
  public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
  	RequestMappingHandlerAdapter requestMappingHandlerAdapter=new RequestMappingHandlerAdapter();
  	//spring boot 已经自带
//  	StringHttpMessageConverter stringHttpMessageConverter=new StringHttpMessageConverter();
//  	stringHttpMessageConverter.setSupportedMediaTypes(Lists.newArrayList(new MediaType("text/html;charset=UTF-8"),
//  			new MediaType("text/plain;charset=UTF-8")));
//  	MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter=new MappingJackson2HttpMessageConverter();
//  	mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Lists.newArrayList(new MediaType("application/json;charset=UTF-8")));
//  	requestMappingHandlerAdapter.setMessageConverters(Lists.newArrayList(stringHttpMessageConverter,
//  			mappingJackson2HttpMessageConverter));
  	return requestMappingHandlerAdapter;
  }
  
  //相当于 spring.mvc.servlet.load-on-startup=1
//@Bean
//public static BeanFactoryPostProcessor beanFactoryPostProcessor() {
//    return new BeanFactoryPostProcessor() {
//
//        @Override
//        public void postProcessBeanFactory(
//                ConfigurableListableBeanFactory beanFactory) throws BeansException {
//            BeanDefinition bean = beanFactory.getBeanDefinition(
//                    DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME);
//
//            bean.getPropertyValues().add("loadOnStartup", 1);
//        }
//    };
//}
//用@WebServlet(urlPatterns = "/cache/*")\@ServletComponentScan代替了
//@Bean(name = "cacheServlet")
//public ServletRegistrationBean<ZkCacheServlet> ZkCacheServlet(){
//    return new ServletRegistrationBean<ZkCacheServlet>(new ZkCacheServlet(),"/cache/*");
//}
}

