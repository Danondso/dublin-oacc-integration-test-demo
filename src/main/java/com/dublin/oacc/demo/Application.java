package com.dublin.oacc.demo;

import com.acciente.oacc.AccessControlContext;
import com.acciente.oacc.PasswordCredentials;
import com.acciente.oacc.Resources;
import com.acciente.oacc.encryptor.bcrypt.BCryptPasswordEncryptor;
import com.acciente.oacc.sql.SQLAccessControlContextFactory;
import com.acciente.oacc.sql.SQLProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.sql.DriverManager;
import java.sql.SQLException;

import static com.acciente.oacc.sql.SQLAccessControlSystemInitializer.initializeOACC;

@SpringBootApplication
public class Application {

    @Bean
    public AccessControlContext createAccessControlContext(@Value("${spring.datasource.url}") String url, @Value("${spring.datasource.oacc.rootpassword}") String rootpassword) throws SQLException {
        initializeOACC(DriverManager.getConnection(url), "OACC", rootpassword.toCharArray(), BCryptPasswordEncryptor.newInstance(12));
        final AccessControlContext accessControlContext = SQLAccessControlContextFactory.getAccessControlContext(DriverManager.getConnection(url), "OACC", SQLProfile.SQLServer_12_0_NON_RECURSIVE, BCryptPasswordEncryptor.newInstance(12));
        accessControlContext.authenticate(Resources.getInstance(0), PasswordCredentials.newInstance(rootpassword.toCharArray()));
        return accessControlContext;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
