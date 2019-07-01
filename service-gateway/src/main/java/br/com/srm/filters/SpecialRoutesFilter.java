package br.com.srm.filters;

import br.com.srm.client.SpecialRouteClient;
import br.com.srm.helper.FilterUtils;
import br.com.srm.model.AbTestingRoute;
import com.google.common.base.Strings;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class SpecialRoutesFilter extends ZuulFilter {
    private static final int FILTER_ORDER =  1;
    private static final boolean SHOULD_FILTER = false;

    private static Logger logger = LoggerFactory.getLogger(SpecialRoutesFilter.class);

    @Autowired
    private FilterUtils filterUtils;

    @Autowired
    private SpecialRouteClient specialRouteClient;

    @Override
    public String filterType() {
        return filterUtils.ROUTE_FILTER_TYPE;
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
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        AbTestingRoute abTestRoute = specialRouteClient.getRoute(filterUtils.getServiceId());
        logger.info("m=run, abTestRoute={}", abTestRoute);
        if (abTestRoute != null && useSpecialRoute(abTestRoute)) {
            ctx.set("serviceId", "new-estoqueservice");
//            String route = buildRouteString(ctx.getRequest().getRequestURI(),
//                    abTestRoute.getEndpoint(), ctx.get("serviceId").toString());
//            forwardToSpecialRoute(route);
        }
        return null;
    }

    public boolean useSpecialRoute(AbTestingRoute testRoute){
        if (testRoute.getActive().equals("N"))
            return false;
        int value = new Random().nextInt((10 - 1) + 1) + 1;
        return testRoute.getWeight() < value;
    }

    private String buildRouteString(String oldEndpoint, String newEndpoint, String serviceName){
        int index = oldEndpoint.indexOf(serviceName);
        String strippedRoute = oldEndpoint.substring(index + serviceName.length());
        return String.format("%s/%s", newEndpoint, strippedRoute);
    }

    private ProxyRequestHelper helper = new ProxyRequestHelper();

    private void forwardToSpecialRoute(String route) {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();

        MultiValueMap<String, String> headers = this.helper.buildZuulRequestHeaders(request);
        MultiValueMap<String, String> params = this.helper.buildZuulRequestQueryParams(request);
        String verb = request.getMethod().toUpperCase();
        InputStream requestEntity = getRequestBody(request);

        if (request.getContentLength() < 0) {
            context.setChunkedRequestBody();
        }

        this.helper.addIgnoredHeaders();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpResponse response = forward(httpClient, verb, route, request, headers, params, requestEntity);
            setResponse(response);
            context.setRouteHost(null);
        } catch (Exception ex ) {
            ex.printStackTrace();
        } finally{
            try {
                httpClient.close();
            }
            catch(IOException ex){}
        }
    }

    private InputStream getRequestBody(HttpServletRequest request) {
        InputStream requestEntity = null;
        try {
            requestEntity = request.getInputStream();
        } catch (IOException ex) {}
        return requestEntity;
    }

    private HttpResponse forward(HttpClient httpclient, String verb,
                                 String uri,
                                 HttpServletRequest request,
                                 MultiValueMap<String, String> headers,
                                 MultiValueMap<String, String> params,
                                 InputStream requestEntity) throws Exception {
        URL host = new URL(uri);
        HttpHost httpHost = new HttpHost(host.getHost(), host.getPort(), host.getProtocol());

        HttpRequest httpRequest;
        int contentLength = request.getContentLength();
        InputStreamEntity entity = new InputStreamEntity(requestEntity, contentLength,
                request.getContentType() != null ? ContentType.create(request.getContentType()) : null);

        switch (verb.toUpperCase()) {
            case "POST":
                HttpPost httpPost = new HttpPost(uri);
                httpRequest = httpPost;
                httpPost.setEntity(entity);
                break;
            case "PUT":
                HttpPut httpPut = new HttpPut(uri);
                httpRequest = httpPut;
                httpPut.setEntity(entity);
                break;
            case "PATCH":
                HttpPatch httpPatch = new HttpPatch(uri);
                httpRequest = httpPatch;
                httpPatch.setEntity(entity);
                break;
            default:
                httpRequest = new BasicHttpRequest(verb, uri + convertParams(params));

        }
        try {
            httpRequest.setHeaders(convertHeaders(headers));
            HttpResponse zuulResponse = forwardRequest(httpclient, httpHost, httpRequest);
            return zuulResponse;
        }
        finally {
        }
    }

    private HttpResponse forwardRequest(HttpClient httpclient, HttpHost httpHost,
                                        HttpRequest httpRequest) throws IOException {
        return httpclient.execute(httpHost, httpRequest);
    }


    private void setResponse(HttpResponse response) throws IOException {
        this.helper.setResponse(response.getStatusLine().getStatusCode(),
                response.getEntity() == null ? null : response.getEntity().getContent(),
                revertHeaders(response.getAllHeaders()));
    }

    private Header[] convertHeaders(MultiValueMap<String, String> headers) {
        List<Header> list = new ArrayList<>();
        for (String name : headers.keySet()) {
            for (String value : headers.get(name)) {
                list.add(new BasicHeader(name, value));
            }
        }
        return list.toArray(new BasicHeader[0]);
    }

    private String convertParams(MultiValueMap<String, String> params) {
        StringBuilder stringBuilder = new StringBuilder("?");
        for (Map.Entry<String, String> entry : params.toSingleValueMap().entrySet()) {
            stringBuilder.append(entry.getKey()+"="+entry.getValue()+"&");
        }
        if (!Strings.isNullOrEmpty(stringBuilder.toString()))
            return stringBuilder.toString().substring(0, stringBuilder.toString().length()-2);
        return null;
    }

    private MultiValueMap<String, String> revertHeaders(Header[] headers) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        for (Header header : headers) {
            String name = header.getName();
            if (!map.containsKey(name)) {
                map.put(name, new ArrayList<String>());
            }
            map.get(name).add(header.getValue());
        }
        return map;
    }

}
