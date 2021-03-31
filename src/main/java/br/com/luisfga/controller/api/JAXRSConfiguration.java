package br.com.luisfga.controller.api;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

@ApplicationPath("api")
public class JAXRSConfiguration extends Application {
    
    @GET
    @Path("/status")
    public String status() {
        return "UP";
    }
}
