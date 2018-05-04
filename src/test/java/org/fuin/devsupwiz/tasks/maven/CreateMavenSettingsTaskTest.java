/**
 * Copyright (C) 2015 Michael Schnell. All rights reserved. 
 * http://www.fuin.org/
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see http://www.gnu.org/licenses/.
 */
package org.fuin.devsupwiz.tasks.maven;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.fuin.devsupwiz.common.ConfigImpl;
import org.fuin.devsupwiz.common.DevSupWizUtils;
import org.fuin.devsupwiz.common.UserInput;
import org.fuin.utils4j.JaxbUtils;
import org.junit.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

/**
 * Test for the {@link CreateMavenSettingsTask} class.
 */
public class CreateMavenSettingsTaskTest {

    @Test
    public void testExecute() throws IOException {

        // PREPARE
        final Charset utf8 = Charset.forName("utf-8");
        final String template = IOUtils
                .resourceToString("/" + CreateMavenSettingsTask.class.getPackage().getName().replace('.', '/') + "/settings.xml", utf8);
        final File templateFile = File.createTempFile("maven-settings-template-", ".xml");
        FileUtils.write(templateFile, template, Charset.forName("utf-8"));

        final String expected = IOUtils.resourceToString(
                "/" + CreateMavenSettingsTask.class.getPackage().getName().replace('.', '/') + "/test-maven-settings.xml", utf8);
        final File targetFile = new File("target/maven-settings.xml");
        targetFile.delete();
        final CreateMavenSettingsTask testee = new CreateMavenSettingsTask(templateFile.toString(), "peter.parker", "secret123",
                targetFile);
        final ConfigImpl config = new ConfigImpl("test", testee);
        config.init();

        // TEST
        testee.execute();

        // VERIFY
        assertThat(targetFile).usingCharset(utf8).hasContent(expected);

    }

    @Test
    public void testValidateInstance() {

        final CreateMavenSettingsTask testee = new CreateMavenSettingsTask();

        final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<CreateMavenSettingsTask>> violations = validator.validate(testee, Default.class, UserInput.class);

        assertThat(DevSupWizUtils.violated(violations, "template")).isTrue();
        assertThat(DevSupWizUtils.violated(violations, "name")).isFalse();
        assertThat(DevSupWizUtils.violated(violations, "password")).isFalse();

        violations = validator.validate(testee, Default.class, CredentialsEnabled.class);

        assertThat(DevSupWizUtils.violated(violations, "template")).isTrue();
        assertThat(DevSupWizUtils.violated(violations, "name")).isTrue();
        assertThat(DevSupWizUtils.violated(violations, "password")).isTrue();
        
    }

    @Test
    public void testMarshal() {

        // PREPARE
        final File targetFile = new File("target/maven-settings.xml");
        targetFile.delete();
        final CreateMavenSettingsTask testee = new CreateMavenSettingsTask("~/.m2/settings.xml", "peter.parker", "secret123", targetFile);
        testee.setHint("Enter your JFrog credentials");
        testee.setSkipCredentials(false);

        // TEST
        final String xml = JaxbUtils.marshal(testee, CreateMavenSettingsTask.class);

        // VERIFY
        final Diff documentDiff = DiffBuilder
                .compare(JaxbUtils.XML_PREFIX + "<create-maven-settings template=\"~/.m2/settings.xml\" name=\"peter.parker\" "
                        + "skip-credentials=\"false\" hint=\"Enter your JFrog credentials\" />")
                .withTest(xml).ignoreWhitespace().build();

        assertThat(documentDiff.hasDifferences()).describedAs(documentDiff.toString()).isFalse();

    }

    @Test
    public void testUnmarshal() {

        // PREPARE
        final String xml = "<create-maven-settings id=\"x\" template=\"~/.m2/settings.xml\"/>";

        // TEST
        final CreateMavenSettingsTask testee = JaxbUtils.unmarshal(xml, CreateMavenSettingsTask.class);

        // VERIFY
        assertThat(testee).isNotNull();
        assertThat(testee.getTemplate()).isEqualTo("~/.m2/settings.xml");
        assertThat(testee.getName()).isNull();
        assertThat(testee.getPassword()).isNull();
        assertThat(testee.getResource()).isNotEmpty();
        assertThat(testee.getFxml()).isNotEmpty();

    }

}
