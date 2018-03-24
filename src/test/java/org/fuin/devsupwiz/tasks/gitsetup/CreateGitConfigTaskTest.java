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
package org.fuin.devsupwiz.tasks.gitsetup;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.apache.commons.io.IOUtils;
import org.fuin.devsupwiz.common.ConfigImpl;
import org.fuin.devsupwiz.common.DevSupWizUtils;
import org.fuin.devsupwiz.common.UserInput;
import org.fuin.utils4j.JaxbUtils;
import org.junit.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

/**
 * Test for the {@link CreateGitConfigTask} class.
 */
public class CreateGitConfigTaskTest {

    @Test
    public void testExecute() throws IOException {

        // PREPARE
        final Charset utf8 = Charset.forName("utf-8");
        final String expected = IOUtils
                .resourceToString("/" + CreateGitConfigTask.class.getPackage()
                        .getName().replace('.', '/') + "/test-gitconfig", utf8);
        final File file = new File("target/.test-gitconfig");
        file.delete();
        final CreateGitConfigTask testee = new CreateGitConfigTask(
                "Peter Parker", "peter.parker@somewhere.com",
                PushDefault.SIMPLE, file);
        final ConfigImpl config = new ConfigImpl("test", testee);
        config.init();

        // TEST
        testee.execute();

        // VERIFY
        assertThat(file).usingCharset(utf8).hasContent(expected);

    }

    @Test
    public void testValidateInstance() {

        final CreateGitConfigTask testee = new CreateGitConfigTask();

        final Validator validator = Validation.buildDefaultValidatorFactory()
                .getValidator();
        final Set<ConstraintViolation<CreateGitConfigTask>> violations = validator
                .validate(testee, Default.class, UserInput.class);

        assertThat(DevSupWizUtils.violated(violations, "email")).isTrue();
        assertThat(DevSupWizUtils.violated(violations, "name")).isTrue();

    }

    @Test
    public void testMarshal() {

        // PREPARE
        final File file = new File("target/.test-gitconfig");
        file.delete();
        final CreateGitConfigTask testee = new CreateGitConfigTask(
                "Peter Parker", "peter.parker@somewhere.com",
                PushDefault.SIMPLE, file);
        final ConfigImpl config = new ConfigImpl("test", testee);
        config.init();

        // TEST
        final String xml = JaxbUtils.marshal(testee, CreateGitConfigTask.class);

        // VERIFY
        final Diff documentDiff = DiffBuilder.compare(JaxbUtils.XML_PREFIX
                + "<create-git-config name=\"Peter Parker\" email=\"peter.parker@somewhere.com\" push-default=\"SIMPLE\"/>")
                .withTest(xml).ignoreWhitespace().build();

        assertThat(documentDiff.hasDifferences())
                .describedAs(documentDiff.toString()).isFalse();

    }

    @Test
    public void testUnmarshal() {

        // PREPARE
        final String xml = "<create-git-config id=\"x\" />";

        // TEST
        final CreateGitConfigTask testee = JaxbUtils.unmarshal(xml,
                CreateGitConfigTask.class);
        final ConfigImpl config = new ConfigImpl("test", testee);
        config.init();

        // VERIFY
        assertThat(testee).isNotNull();
        assertThat(testee.getEmail()).isNull();
        assertThat(testee.getName()).isNull();
        assertThat(testee.getPushDefault()).isEqualTo(PushDefault.SIMPLE);
        assertThat(testee.getResource()).isNotEmpty();
        assertThat(testee.getFxml()).isNotEmpty();

    }

}
