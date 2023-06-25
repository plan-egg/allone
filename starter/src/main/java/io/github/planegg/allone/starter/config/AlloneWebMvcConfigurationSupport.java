package io.github.planegg.allone.starter.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.github.planegg.allone.starter.service.formatter.LocalDateFormatter;
import io.github.planegg.allone.starter.service.formatter.LocalDateTimeFormatter;
import org.hibernate.validator.BaseHibernateValidatorConfiguration;
import org.springframework.boot.validation.MessageInterpolatorFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Hibernate Validator 配置
 */

@Configuration
public class AlloneWebMvcConfigurationSupport extends WebMvcConfigurationSupport {
    /**
     * 配置验证器
     * <p>
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


    @Override
    protected void addFormatters(FormatterRegistry registry) {
        // 用于get 全局格式化日期转换
        registry.addFormatterForFieldType(LocalDate.class, new LocalDateFormatter());
        registry.addFormatterForFieldType(LocalDateTime.class, new LocalDateTimeFormatter());
    }



    //启动的时候进入ObjectMapper 构造断点，系统自动创建了
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0,localDateTimeHttpMessageConverter());
        converters.add(longHttpMessageConverter());
//        converters.add(dateHttpMessageConverter());

    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
    }


    /**
     * 序列化LocalDateTime
     */
    private MappingJackson2HttpMessageConverter localDateTimeHttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(pattern));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(pattern));

        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 反序列化时忽略多余字段
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 注册
        objectMapper.registerModule(javaTimeModule);
        objectMapper.setLocale(Locale.CHINA);
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+:08:00"));
        converter.setObjectMapper(objectMapper);
        return converter;
    }


    /**
     * 返回json时候将long类型转换为String类型的转换器
     *
     * @return
     */
    private MappingJackson2HttpMessageConverter longHttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        //序列换成json时,将所有的long变成string , 防止js Long类型溢出
        SimpleModule longModule = new SimpleModule();
        longModule.addSerializer(Long.class, ToStringSerializer.instance);
        longModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        longModule.addSerializer(BigInteger.class, ToStringSerializer.instance);

        // 反序列化时忽略多余字段
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 注册
        objectMapper.registerModule(longModule);
        converter.setObjectMapper(objectMapper);
        return converter;
    }
}
