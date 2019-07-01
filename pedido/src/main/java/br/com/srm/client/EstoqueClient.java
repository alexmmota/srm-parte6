package br.com.srm.client;

import br.com.srm.client.dto.Product;
import br.com.srm.utils.UserContext;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("servicegateway")
public interface EstoqueClient {

    @RequestMapping(value = "/api/estoque/v1/departments/{departmentId}/products/{isbn}",
            method = RequestMethod.GET)
    Product findByIsbn(@RequestHeader(UserContext.AUTH_TOKEN) String headerAuthorization,
                       @RequestHeader(UserContext.CORRELATION_ID) String headerCorrelationId,
                       @PathVariable("departmentId") Long departmentId,
                       @PathVariable("isbn") String isbn);

    @RequestMapping(value = "/api/estoque/v1/departments/{departmentId}/products/{isbn}/subtractAmount",
            method = RequestMethod.POST)
    void subtractAmount(@RequestHeader(UserContext.AUTH_TOKEN) String headerAuthorization,
                        @RequestHeader(UserContext.CORRELATION_ID) String headerCorrelationId,
                        @PathVariable("departmentId") Long departmentId,
                        @PathVariable("isbn") String isbn,
                        @RequestParam("amount") Integer amount);

}
