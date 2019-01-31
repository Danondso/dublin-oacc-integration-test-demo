package com.dublin.oacc.demo;

import com.acciente.oacc.AccessControlContext;
import com.acciente.oacc.ResourcePermission;
import com.acciente.oacc.ResourcePermissions;
import com.acciente.oacc.Resources;
import lombok.extern.slf4j.Slf4j;
import org.hsqldb.cmdline.SqlFile;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Slf4j
@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class ApplicationTests {

    @Autowired
    private AccessControlContext accessControlContext;

    @BeforeClass
    public static void setup() {

        try {
            Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:oaccdb;sql.syntax_pgs=true", "", "");
            SqlFile sf = new SqlFile(new File("src/test/resources/sql/initializeOACCDatabase.sql"));
            sf.setConnection(conn);
            sf.execute();
        } catch (Exception e) {
            log.error("Unable to create the OaccDatabase for testing..", e);
        }
    }


    @Test
    public void testCreatingDomainIsSuccessful() {
        //--- ARRANGE ---
        accessControlContext.createDomain("TEST");
        accessControlContext.createResourceClass("TEST_RESOURCE_CLASS", false, false);
        accessControlContext.createResourceClass("SECOND_TEST_RESOURCE_CLASS", false, false);
        accessControlContext.createResourcePermission("SECOND_TEST_RESOURCE_CLASS", "SOME_PERMISSION");
        accessControlContext.createResource("TEST_RESOURCE_CLASS", "TEST", "Johnny Test");
        accessControlContext.createResource("SECOND_TEST_RESOURCE_CLASS", "TEST", "Johnny Test's Dog");

        accessControlContext.setResourcePermissions(Resources.getInstance("Johnny Test"),
                Resources.getInstance("Johnny Test's Dog"),
                Sets.newSet(ResourcePermissions.getInstance("SOME_PERMISSION")));

        //--- ACT ---
        Set<String> domains = accessControlContext.getDomainDescendants("TEST");
        //--- ASSERT ---
        assertEquals(domains.size(), 1);
        assertTrue(domains.contains("TEST"));

        Set<ResourcePermission> permissions = accessControlContext.getResourcePermissions(Resources.getInstance("Johnny Test"),
                Resources.getInstance("Johnny Test's Dog"));

        assertThat(permissions.size(), is(1));
        assertTrue(permissions.contains(ResourcePermissions.getInstance("SOME_PERMISSION")));

    }
}
