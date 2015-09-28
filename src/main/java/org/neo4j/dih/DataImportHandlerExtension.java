package org.neo4j.dih;

import org.neo4j.dih.exception.DIHRuntimeException;
import org.neo4j.dih.service.ImporterService;
import org.neo4j.dih.service.TemplateService;
import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;

@Path("/")
public class DataImportHandlerExtension {

    /**
     * The logger
     */
    private static final Logger log = LoggerFactory.getLogger(DataImportHandlerExtension.class);

    /**
     * Graph database instance.
     */
    private final GraphDatabaseService database;

    /**
     * Constructor.
     *
     * @param database
     */
    public DataImportHandlerExtension(@Context GraphDatabaseService database) {
        this.database = database;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    public Response update(@QueryParam("name") String name,
                           @QueryParam("clean") Boolean clean,
                           @QueryParam("debug") Boolean debug) {
        try {
            ImporterService importer = new ImporterService(database, name, clean, debug);
            importer.execute();
            return Response.status(Response.Status.OK).entity(importer.stats.toJson()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
        }
    }

    @GET
    @Path("/ping")
    public Response ping() {
        return Response.ok("Pong".getBytes(Charset.forName("UTF-8")), MediaType.TEXT_PLAIN).build();
    }

    @GET
    @Path("/admin")
    public Response admin() {
        File template = new File(ClassLoader.getSystemResource("admin/index.vm").getFile());
        String page = TemplateService.getInstance().compile(template, new HashMap<String, Object>());
        return Response.status(Response.Status.OK).entity(page.getBytes(Charset.forName("UTF-8"))).build();
    }

    @GET
    @Path("{file:(?i).+\\.(png|jpg|jpeg|svg|gif|html?|js|css|txt)}")
    public Response file(@PathParam("file") String file) {
        InputStream fileStream = findFileStream(file);
        if (fileStream == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(fileStream, mediaType(file)).build();
        }
    }

    /**
     * Find a file in classloader and return it as an <code>InputStream</code>.
     * If file is not found, it return <code>null</code>.
     *
     * @param file Path/name of the file to search.
     * @return InputStream of the specified file or <code>null</code>
     */
    private InputStream findFileStream(String file) {
        InputStream is = null;
        URL fileUrl = ClassLoader.getSystemResource("assets/" + file);
        log.debug("Find file %s url %s", file, fileUrl);

        try {
            if (fileUrl != null) {
                is = fileUrl.openStream();
            }
        } catch (IOException e) {
            throw new DIHRuntimeException("File %s url %s produce error %s", file, fileUrl, e.getMessage());
        }
        return is;
    }

    /**
     * Compute media-type of a file by ts extension.
     *
     * @param file path/name of the file
     * @return MediaType of the file
     */
    private String mediaType(String file) {
        String mediaType = MediaType.TEXT_PLAIN;
        Integer dot = file.lastIndexOf(".");
        String ext = file.substring(dot + 1).toLowerCase();
        switch (ext) {
            case "png":
                mediaType = "image/png";
                break;
            case "gif":
                mediaType = "image/gif";
                break;
            case "jpg":
            case "jpeg":
                mediaType = "image/jpg";
                break;
            case "json":
                mediaType = MediaType.APPLICATION_JSON;
                break;
            case "js":
                mediaType = "text/javascript";
                break;
            case "css":
                mediaType = "text/css";
                break;
            case "svg":
                mediaType = MediaType.APPLICATION_SVG_XML;
                break;
            case "html":
                mediaType = MediaType.TEXT_HTML;
                break;
            case "txt":
                mediaType = MediaType.TEXT_PLAIN;
                break;
            default:
                mediaType = MediaType.TEXT_PLAIN;
                break;
        }

        return mediaType;
    }
}
