package org.neo4j.dih;

import org.neo4j.dih.exception.DIHException;
import org.neo4j.dih.service.ImporterService;
import org.neo4j.graphdb.GraphDatabaseService;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import java.nio.charset.Charset;

@Path("/dih")
public class DIHPlugin {

    /**
     * Graph database instance.
     */
    private final GraphDatabaseService database;

    /**
     * Constructor.
     *
     * @param database
     */
    public DIHPlugin(@Context GraphDatabaseService database) {
        this.database = database;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    public Response update( @QueryParam("name") String name,
                            @QueryParam("clean") Boolean clean,
                            @QueryParam("debug") Boolean debug ) {
        try {
            ImporterService importer = new ImporterService(database, name, clean, debug);
            importer.execute();
        } catch (DIHException e) {
            e.printStackTrace();
        }

        return Response.status(Response.Status.OK).build();
    }

}
