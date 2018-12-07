package com.yasenagat.zkweb;
/**
 * 
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private final static Logger logger = LoggerFactory.getLogger(ZkWebSpringBootApplication.class);
	public final static String applicationYamlFileName="application-zkweb.yaml";
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
		String file=ZkWebSpringBootApplication.class.getClassLoader().getResource(applicationYamlFileName).getFile();
    	logger.info("applicationYamlFileName({})={}",applicationYamlFileName,file);
		application.properties("spring.config.location=classpath:/"+applicationYamlFileName);
        return application.sources(ZkWebSpringBootApplication.class);
    }
    public static void main(String[] args) {
    	//System.setProperty("spring.config.location", "classpath*:/application-zkweb.yaml");
    	//SpringApplication.run(ZkWebSpringBootApplication.class, args);
    	String file=ZkWebSpringBootApplication.class.getClassLoader().getResource(applicationYamlFileName).getFile();
    	logger.info("applicationYamlFileName({})={}",applicationYamlFileName,file);
    	new SpringApplicationBuilder(ZkWebSpringBootApplication.class).
			properties("spring.config.location=classpath:/"+applicationYamlFileName).run(args);
    }
}