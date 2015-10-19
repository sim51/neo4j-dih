package org.neo4j.dih;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.neo4j.dih.exception.DIHRuntimeException;
import org.neo4j.dih.service.ImporterService;
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
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.FutureTask;

/**
 * DIH extension endpoint.
 *
 * @author bsimard
 * @version $Id: $Id
 */
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
     * Thread-safe task list of import.
     */
    private final ConcurrentMap<String, FutureTask> tasks = new ConcurrentHashMap<String, FutureTask>();

    /**
     * Constructor.
     *
     * @param database a {@link org.neo4j.graphdb.GraphDatabaseService} object.
     */
    public DataImportHandlerExtension(@Context GraphDatabaseService database) {
        this.database = database;
    }

    /**
     * Endpoint that execute an import process.
     *
     * @param name Name of the configuration file
     * @param clean Does clean mode is activated ?
     * @param debug Does debug mode is activated ?
     * @return a {@link javax.ws.rs.core.Response} object.
     */
    @POST
    @Path("/api/import")
    public Response execute(@FormParam("name") String name,
                           @FormParam("clean") Boolean clean,
                           @FormParam("debug") Boolean debug) {
        try {
            ImporterService importer = new ImporterService(database, name, clean, debug);
            importer.execute();
            return Response.ok(importer.stats.toJson(), MediaType.APPLICATION_JSON_TYPE).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.toString()).type( MediaType.TEXT_PLAIN).build();
        }
    }

    /**
     * Endpoint to list all import configuration files found.
     *
     * @return a {@link javax.ws.rs.core.Response} object.
     * @throws java.io.IOException if any.
     */
    @GET
    @Path("/api/listing")
    public Response listing() throws IOException {
        List<String> files = ImporterService.getAllConfiguration();
        return Response.ok(new ObjectMapper().writeValueAsString(files), MediaType.APPLICATION_JSON).build();
    }

    /**
     * Just a ping endpoint, because it's always to have one.
     *
     * @return a {@link javax.ws.rs.core.Response} object.
     */
    @GET
    @Path("/api/ping")
    public Response ping() {
        return Response.ok("Pong".getBytes(Charset.forName("UTF-8")), MediaType.TEXT_PLAIN).build();
    }

    /**
     * Endpoint that return the content of an import configuration (or property) file.
     *
     * @param name Name of the configuration/properties file
     * @return a {@link javax.ws.rs.core.Response} object.
     */
    @GET
    @Path("/api/get")
    public Response getConfig(@QueryParam("name") String name) {
        InputStream fileStream = findFileStream("conf/dih", name);
        if (fileStream == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(fileStream, MediaType.APPLICATION_XML).build();
        }
    }

    /**
     * Static web server files.
     *
     * @param file a {@link java.lang.String} object.
     * @return a {@link javax.ws.rs.core.Response} object.
     */
    @GET
    @Path("{file:(?i).+\\.(png|jpg|jpeg|svg|gif|html?|js|css|txt)}")
    public Response file(@PathParam("file") String file) {
        InputStream fileStream = findFileStream("webapp", file);
        if (fileStream == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(fileStream, mediaType(file)).build();
        }
    }

    /**
     * Find a file in classloader and return it as an <code>InputStream</code>.
     * If file is not found, it return <code>null</code>.
     * /!\ You are responsable to close the InputStream !
     *
     * @param directory Main directory where to find the file
     * @param path Path/name of the file to search.
     * @return InputStream of the specified file or <code>null</code>
     */
    private InputStream findFileStream(String directory, String path) {
        InputStream is = null;
        URL fileUrl = ClassLoader.getSystemResource(directory + "/" + path);
        log.debug("Find file %s url %s", path, fileUrl);

        try {
            if (fileUrl != null) {
                File file = new File(fileUrl.getFile());
                is = FileUtils.openInputStream(file);
            }
        } catch (IOException e) {
            throw new DIHRuntimeException("File %s url %s produce error %s", path, fileUrl, e.getMessage());
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
