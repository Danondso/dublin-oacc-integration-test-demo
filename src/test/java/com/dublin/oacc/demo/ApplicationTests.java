package com.dublin.oacc.demo;

import com.acciente.oacc.AccessControlContext;
import lombok.extern.slf4j.Slf4j;
import org.hsqldb.cmdline.SqlFile;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.assertj.ApplicationContextAssert;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
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
            Connection conn = DriverManager.getConnection("jdbc:hsqldb:mem:oaccdb;", "", "");
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
        //--- ACT ---
        Set<String> domains = accessControlContext.getDomainDescendants("TEST");
        //--- ASSERT ---
        assertEquals(domains.size(), 1);
        assertTrue(domains.contains("TEST"));
    }
}
