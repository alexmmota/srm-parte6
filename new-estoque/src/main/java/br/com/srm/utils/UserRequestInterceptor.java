package br.com.srm.utils;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(UserContext.CORRELATION_ID, UserContextHolder.getContext().getCorrelationId());
    }

}
