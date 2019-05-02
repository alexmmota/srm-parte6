package br.com.srm.service;

import br.com.srm.exception.NoRouteFound;
import br.com.srm.model.AbTestingRoute;
import br.com.srm.repository.AbTestingRouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AbTestingRouteService {

    @Autowired
    private AbTestingRouteRepository abTestingRouteRepository;

    public AbTestingRoute getRoute(String serviceName) {
        return abTestingRouteRepository.findByServiceName(serviceName);
    }

    public void saveAbTestingRoute(AbTestingRoute route){
        abTestingRouteRepository.save(route);
    }

    public void updateRouteAbTestingRoute(AbTestingRoute route){
        abTestingRouteRepository.save(route);
    }

    public void deleteRoute(AbTestingRoute route){
        abTestingRouteRepository.deleteById(route.getServiceName());
    }
}
