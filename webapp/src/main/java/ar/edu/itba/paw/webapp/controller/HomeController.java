package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.dto.output.HomeDto;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/")
@Component
public class HomeController {

    @Context
    private UriInfo uriInfo;

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response giveEndpointsInformation() {
        return Response.ok(new HomeDto(uriInfo)).build();
    }
}
