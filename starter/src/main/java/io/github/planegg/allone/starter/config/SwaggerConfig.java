package io.github.planegg.allone.starter.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@ConfigurationProperties(prefix = "allone.ext.swagger")
@ConditionalOnProperty(prefix = "allone.ext.swagger", value = "enabled", havingValue = "true")
public class SwaggerConfig {

    /**
     * 接口类包名
     */
    private String baskPkg;
    /**
     * 文档标题
     */
    private String title;

    private String desc;

    private String version;
    private String teamUrl;
    private String contactName;
    private String license;
    private String licenseUrl;


    @Bean
    @ConditionalOnMissingBean(Docket.class)
    public Docket docket(){
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .enable(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage(baskPkg))
//                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
//                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build();
    }


    @SuppressWarnings("all")
    public ApiInfo apiInfo(){
        return new ApiInfo(
                title,
                desc,
                version,
                teamUrl,
                contactName,
                license,  //许可证
                licenseUrl //许可证链接
        );
    }

    public void setBaskPkg(String baskPkg) {
        this.baskPkg = baskPkg;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setTeamUrl(String teamUrl) {
        this.teamUrl = teamUrl;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }
}
