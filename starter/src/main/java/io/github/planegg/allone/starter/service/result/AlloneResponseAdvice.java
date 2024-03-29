package io.github.planegg.allone.starter.service.result;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一封装返回结果
 */
@RestControllerAdvice
public class AlloneResponseAdvice implements ResponseBodyAdvice<Object> {

    @Autowired
    private IReqResultFactory reqResultFactory;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return !returnType.getDeclaringClass().getName().contains("springfox");

    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                             Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
                             ServerHttpResponse response) {
        if (body == null){
            return null;
        }
        // 提供一定的灵活度，如果body已经被包装了，就不进行包装
        if (body instanceof ReqResultDti) {
            return body;
        }
        //返回值body为String类型时
        if (body instanceof String) {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
            return new Gson().toJson(reqResultFactory.createSuccessMsg(body));
        }
        return reqResultFactory.createSuccessMsg(body);
    }
}
