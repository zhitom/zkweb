package com.yasenagat.zkweb;
/**
 * 
 */

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Shandy
 *
 */
//@SpringBootApplication(exclude = {WebFluxAutoConfiguration.class,ReactorCoreAutoConfiguration.class})
@SpringBootApplication
@ComponentScan
@ServletComponentScan
//SpringBootServletInitializer for war打包方式
public class ZkWebSpringBootApplication extends SpringBootServletInitializer{
//	@Bean(name = "sessionProperties")
//    @Qualifier(value = "sessionProperties")
//    public SessionProperties sessionProperties(){
//        return new SessionProperties(null);
//    }
//	@Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//        return application.sources(SpringBootApplication.class);
//    }
	//for war打包方式
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ZkWebSpringBootApplication.class);
    }
    public static void main(String[] args) {
    	new SpringApplicationBuilder(ZkWebSpringBootApplication.class).
    		properties("spring.config.location=classpath:application-zkweb.yaml").run(args);
    	//SpringApplication.run(ZkWebSpringBootApplication.class, args);
    }
}