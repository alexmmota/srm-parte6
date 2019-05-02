package br.com.srm.repository;

import br.com.srm.model.AbTestingRoute;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AbTestingRouteRepository extends CrudRepository<AbTestingRoute, String> {

    AbTestingRoute findByServiceName(String serviceName);

}
