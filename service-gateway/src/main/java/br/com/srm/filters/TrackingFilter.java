package br.com.srm.filters;

import br.com.srm.helper.FilterUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrackingFilter extends ZuulFilter {

    private static final int FILTER_ORDER = 1;
    private static final boolean SHOULD_FILTER=true;
    private static final Logger logger = LoggerFactory.getLogger(TrackingFilter.class);

    @Autowired
    private FilterUtils filterUtils;

    @Override
    public String filterType() {
        return FilterUtils.PRE_FILTER_TYPE;
    }

    @Override
    public int filterOrder() {
        return FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        return SHOULD_FILTER;
    }

    @Override
    public Object run() throws ZuulException {
        if (isCorrelationIdPresent()) {
            logger.info("tmx-correlation-id encontrado em tracking filter: {}",
                    filterUtils.getCorrelationId());
        } else {
            filterUtils.setCorrelationId(generateCorrelationId());
            logger.info("tmx-correlation-id generated in tracking filter: {}.",
                    filterUtils.getCorrelationId());
        }
        RequestContext ctx = RequestContext.getCurrentContext();
        logger.info("Processando requisição recebida para {}.",
                ctx.getRequest().getRequestURI());
        return null;
    }

    private String generateCorrelationId(){
        return java.util.UUID.randomUUID().toString();
    }

    private boolean isCorrelationIdPresent(){
        if (filterUtils.getCorrelationId() !=null){
            return true;
        }
        return false;
    }

}
