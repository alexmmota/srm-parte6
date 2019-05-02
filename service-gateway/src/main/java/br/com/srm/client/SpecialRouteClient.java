package br.com.srm.client;

import br.com.srm.model.AbTestingRoute;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("specialrouteservice")
public interface SpecialRouteClient {

    @RequestMapping(value = "/v1/route/abtesting/{serviceName}", method = RequestMethod.GET)
    AbTestingRoute getRoute(@PathVariable("serviceName") String serviceName);

}
