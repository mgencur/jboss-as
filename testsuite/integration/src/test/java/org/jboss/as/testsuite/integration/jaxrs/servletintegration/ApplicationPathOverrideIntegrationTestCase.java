/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.testsuite.integration.jaxrs.servletintegration;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.api.Run;
import org.jboss.arquillian.api.RunModeType;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.as.testsuite.integration.common.HttpRequest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Tests a JAX-RS deployment with an application bundled, that has an @ApplicationPath annotation.
 *
 * This annotation is overridden by a mapping in web.xml
 *
 * JAX-RS 1.1 2.3.2 bullet point 3
 *
 * @author Stuart Douglas
 */
@RunWith(Arquillian.class)
@Run(RunModeType.AS_CLIENT)
public class ApplicationPathOverrideIntegrationTestCase {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = ShrinkWrap.create(WebArchive.class,"jaxrsapp.war");
        war.addPackage(HttpRequest.class.getPackage());
        war.addClasses(ApplicationPathOverrideIntegrationTestCase.class, HelloWorldResource.class,HelloWorldPathApplication.class);
        war.addWebResource(WebXml.get("<servlet-mapping>\n" +
                "        <servlet-name>"+HelloWorldPathApplication.class.getName()+"</servlet-name>\n" +
                "        <url-pattern>/override/*</url-pattern>\n" +
                "    </servlet-mapping>\n" +
                "\n"),"web.xml");
        return war;
    }


    private static String performCall(String urlPattern) throws Exception {
        return HttpRequest.get("http://localhost:8080/jaxrsapp/" + urlPattern, 5, TimeUnit.SECONDS);
    }

    @Test
    public void testJaxRsWithNoApplication() throws Exception {
        String result = performCall("override/helloworld");
        assertEquals("Hello World!", result);
    }


}
