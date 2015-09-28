package org.neo4j.dih.service;

import org.junit.Assert;
import org.junit.Test;
import org.neo4j.dih.DIHUnitTest;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class for TemplateService.
 */
public class TemplateServiceTest extends DIHUnitTest {


    @Test
    public void compile_should_succeed() {
        Map<String, Object> user = new HashMap<>();
        user.put("name", "root");
        user.put("host", "localhost");

        Map<String, Object> params = new HashMap<>();
        params.put("user", user);

        String template = "MERGE (user:User { user :'${user.name}') MERGE (host:Host { name:'${user.host}')";

        String result = TemplateService.getInstance().compile(template, params);

        Assert.assertEquals("MERGE (user:User { user :'root') MERGE (host:Host { name:'localhost')", result);
    }

}
