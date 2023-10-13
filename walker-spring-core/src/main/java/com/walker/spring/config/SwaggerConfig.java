package com.walker.spring.config;


import com.walker.service.Config;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 图形化测试工具配置
 * <p>
 * http://localhost:8080/swagger-ui.html
 *
 * @Api：用在类上，说明该类的作用。
 * @ApiOperation：注解来给API增加方法说明。
 * @ApiImplicitParams : 用在方法上包含一组参数说明。
 * @ApiImplicitParam：用来注解来给方法入参增加说明。
 * @ApiResponses：用于表示一组响应
 * @ApiResponse：用在@ApiResponses中，一般用于表达一个错误的响应信息 l   code：数字，例如400
 * l   message：信息，例如"请求参数没填好"
 * l   response：抛出异常的类
 * @ApiModel：描述一个Model的信息（一般用在请求参数无法使用@ApiImplicitParam注解进行描述的时候） l   @ApiModelProperty：描述一个model的属性
 * 注意：@ApiImplicitParam的参数说明：
 * paramType：指定参数放在哪个地方
 * header：请求参数放置于Request Header，使用@RequestHeader获取
 * query：请求参数放置于请求地址，使用@RequestParam获取
 * path：（用于restful接口）-->请求参数的获取：@PathVariable
 * body：（不常用）
 * form（不常用）
 * name：参数名
 * dataType：参数类型
 * required：参数是否必须传
 * true | false
 * value：说明参数的意思
 * defaultValue：参数的默认值
 */


@Configuration//说明是一个配置类
@EnableSwagger2
public class SwaggerConfig {
    private final Logger log = LoggerFactory.getLogger(getClass());


/**
 * Swagger2配置类
 * 在与spring boot集成时，放在与Application.java同级的目录下。
 * 通过@Configuration注解，让Spring来加载该类配置。
 * 再通过@EnableSwagger2注解来启用Swagger2。
 */

    /**
     * 创建API应用
     * apiInfo() 增加API相关信息
     * 通过select()函数返回一个ApiSelectorBuilder实例,用来控制哪些接口暴露给Swagger来展现，
     * 本例采用指定扫描的包路径来定义指定要建立API的目录。
     */
    @Bean
    public Docket createRestApi() {
        log.info(Config.getPre() + "SwaggerConfig createRestApi");
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //扫描函数注解
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                //api包指定
//                .apis(RequestHandlerSelectors.basePackage("comw.walker.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 创建该API的基本信息（这些基本信息会展现在文档页面中）
     * 访问地址：http://项目实际地址/swagger-ui.html
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Spring Boot中使用Swagger2构建RESTful APIs的在线测试")
                .description("https://github.com/1424234500/walker")
                .termsOfServiceUrl("http://www.baidu.com")
                .contact(new Contact("walker", "https://github.com/1424234500", "1424234500@qq.com"))
                .version("1.0")
                .build();
    }
}