package br.com.srm.model;

public class AbTestingRoute {

    private String serviceName;
    private String active;
    private String endpoint;
    private Integer weight;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "AbTestingRoute{" +
                "serviceName='" + serviceName + '\'' +
                ", active='" + active + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", weight=" + weight +
                '}';
    }
}
