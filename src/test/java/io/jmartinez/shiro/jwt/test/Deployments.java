package io.jmartinez.shiro.jwt.test;

import io.jmartinez.shiro.jwt.example.boundary.UserRepositoryExample;
import io.jmartinez.shiro.jwt.example.entity.UserDefaultExample;
import io.jmartinez.shiro.jwt.example.jackson.ObjectMapperProviderExample;
import io.jmartinez.shiro.jwt.example.rest.JAXRSConfigurationExample;
import io.jmartinez.shiro.jwt.example.rest.ResourceExample;
import java.io.File;
import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;

@ArquillianSuiteDeployment
public class Deployments {

    @Deployment
    public static WebArchive createDeployment() {

        File[] filesCompile = Maven.resolver().loadPomFromFile("pom.xml").importDependencies(ScopeType.COMPILE).resolve().withTransitivity().asFile();

        JavaArchive jar = ShrinkWrap.create(MavenImporter.class)
                .loadPomFromFile("pom.xml")
                .importBuildOutput()
                .as(JavaArchive.class);

        return ShrinkWrap.create(WebArchive.class)
                .addAsLibraries(jar)
                .addAsLibraries(filesCompile)
                .addClasses(UserDefaultExample.class, UserRepositoryExample.class)
                .addClasses(JAXRSConfigurationExample.class, ObjectMapperProviderExample.class, ResourceExample.class)
                .addAsWebInfResource("WEB-INF/test.shiro.ini", "shiro.ini")
                .addAsWebInfResource("WEB-INF/web.xml", "web.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

    }

}
