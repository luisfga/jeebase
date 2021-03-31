package br.com.luisfga.controller.api.resources;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/consulta")
public class Consulta {
    
    @GET
    @Path("/item/{txt}")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject consultaProdutos(@Context HttpServletRequest request, @PathParam("txt") String txt) {
        
        JsonObjectBuilder payloadBuilder = Json.createObjectBuilder();

        payloadBuilder.add("message", "hit ok: /api/consulta/item/"+txt);
        
        return payloadBuilder.build();
    }
}
