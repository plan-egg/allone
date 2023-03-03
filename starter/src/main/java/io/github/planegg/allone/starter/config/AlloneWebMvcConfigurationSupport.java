package io.github.planegg.allone.starter.config;

import org.hibernate.validator.BaseHibernateValidatorConfiguration;
import org.springframework.boot.validation.MessageInterpolatorFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;


/**
 * Hibernate Validator 配置
 */

@Configuration
public class AlloneWebMvcConfigurationSupport extends WebMvcConfigurationSupport {
    /**
     * 配置验证器
     *
     * Hibernate的校验模式
     * Hibernate Validator有普通模式(默认是这个模式) 和 快速模式两种验证模式。
     * 普通模式:  会校验完所有的属性，然后返回所有的验证失败信息。
     * 快速模式: 只要有一个验证失败，则返回。
     *
     * @return Validator
     */
    @Override
    protected Validator getValidator() {
/*        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        // 读取配置文件的编码格式
        messageSource.setDefaultEncoding("utf-8");
        // 缓存时间，-1表示不过期
        messageSource.setCacheMillis(-1);
        // 配置文件前缀名，设置为Messages,那你的配置文件必须以Messages.properties/Message_en.properties...
        messageSource.setBasename("ValidationMessages");*/

        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        MessageInterpolatorFactory interpolatorFactory = new MessageInterpolatorFactory();
        factoryBean.setMessageInterpolator(interpolatorFactory.getObject());
        //自定义错误信息
//        factoryBean.setValidationMessageSource(messageSource);
        // 快速失败
        factoryBean.getValidationPropertyMap().put(BaseHibernateValidatorConfiguration.FAIL_FAST, Boolean.TRUE.toString());

        return factoryBean;
    }


   /* @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
*//*        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new SourceHttpMessageConverter());
        converters.add(new AllEncompassingFormHttpMessageConverter());*//*
        //TODO 灯笼：处理Long转String
//        converters.add(dateHttpMessageConverter());
        converters.add(longHttpMessageConverter());
    }

    *//**
     * 时间格式转换器,将Date类型统一转换为yyyy-MM-dd HH:mm:ss格式的字符串
     * @return
     *//*
    @Bean
    public MappingJackson2HttpMessageConverter dateHttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        converter.setObjectMapper(mapper);
        return converter;
    }

    *//**
     * 返回json时候将long类型转换为String类型的转换器
     * @return
     *//*
    @Bean
    public MappingJackson2HttpMessageConverter longHttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        mapper.registerModule(simpleModule);
        converter.setObjectMapper(mapper);
        return converter;
    }*/
}
