package br.com.srm.controller;

import br.com.srm.model.AbTestingRoute;
import br.com.srm.service.AbTestingRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/v1/route")
public class SpecialRoutesServiceController {

    @Autowired
    private AbTestingRouteService routeService;

    @RequestMapping(value="/abtesting/{serviceName}",method = RequestMethod.GET)
    public AbTestingRoute abstestings(@PathVariable("serviceName") String serviceName) {
        return routeService.getRoute( serviceName);
    }

}