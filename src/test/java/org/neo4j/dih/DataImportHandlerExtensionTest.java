package org.neo4j.dih;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.test.server.HTTP;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Unit test for DataImportHandler class extension.
 */
public class DataImportHandlerExtensionTest extends DIHUnitTest {

    @Before
    public void prepare() throws IOException, SQLException {
        initH2();
        initServerWithExtension();
    }

    @Test
    public void ping_should_work() throws Exception {
        check("/dih/api/ping", "GET", null, 200, "text/plain", "Pong");
    }

    @Test
    public void update_complexe_should_work() throws Exception {
        check("/dih/api/import", "POST", "name=example_complexe.xml", 200, "application/json", null);
    }

    @Test
    public void asset_html_should_work() throws Exception {
        check("/dih/test.html", "GET", null, 200, "text/html", "<title>Test Page</title>");
    }

    @Test
    public void asset_css_should_work() throws Exception {
        check("/dih/test.css", "GET", null, 200, "text/css", ".test {");
    }

    @Test
    public void asset_js_should_work() throws Exception {
        check("/dih/test.js", "GET", null, 200, "text/javascript", "function echo() {");
    }

    @Test
    public void asset_image_should_work() throws Exception {
        check("/dih/image/node-blue.png", "GET", null, 200, "image/png", null);
    }

    @Test
    public void asset_not_existing_file_should_return_404() throws Exception {
        check("/dih/404.html", "GET", null, 404, null, null);
    }

    @After
    public void destroy() throws SQLException {
        destroyServerWithExtension();
        destroyH2();
    }

    /**
     * Make all needed test on a request.
     *
     * @param uri Uri to test
     * @param method GET or POST
     * @param body Body of the request
     * @param code Return code that should be there
     * @param contentType ContentType that should be there
     * @param content Part of content that should be there
     * @return response of the request
     */
    private  HTTP.Response check(String uri, String method, String body, int code, String contentType, String content) {
        if(uri.startsWith("/")) {
            uri = uri.replaceFirst("/", "");
        }

        // When I access a none existing file
        HTTP.Builder builder =  HTTP.withBaseUri(server.httpURI().toString())
                .withHeaders("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

        HTTP.Response response;
        if(method.equalsIgnoreCase("GET")){
            response = builder.GET(uri);
        }
        else {
            response = builder.POST(uri, HTTP.RawPayload.rawPayload(body));
        }

        // Then it should reply ok with good  type & content
        if(code > 0)
            Assert.assertEquals(code, response.status());
        if(contentType != null)
            Assert.assertEquals(contentType, response.header("content-type"));
        if(content != null)
            Assert.assertTrue(response.rawContent().contains(content));

        return response;
    }
}
