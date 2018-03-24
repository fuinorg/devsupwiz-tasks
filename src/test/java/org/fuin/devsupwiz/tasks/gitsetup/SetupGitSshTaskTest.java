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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.fuin.devsupwiz.common.ConfigImpl;
import org.fuin.devsupwiz.common.DevSupWizUtils;
import org.fuin.devsupwiz.common.UserInput;
import org.fuin.utils4j.JaxbUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

/**
 * Test for the {@link GenerateSshKeyTask} class.
 */
public class SetupGitSshTaskTest {

    private static final Logger LOG = LoggerFactory
            .getLogger(SetupGitSshTaskTest.class);

    private File sshDir;

    @Before
    public void setup() {
        sshDir = new File("target/.ssh");
        if (sshDir.exists()) {
            assertThat(FileUtils.deleteQuietly(sshDir));
        }
        final File userPrefDir = new File("target/.dev-setup");
        if (userPrefDir.exists()) {
            assertThat(FileUtils.deleteQuietly(userPrefDir)).isTrue();
        }
        assertThat(userPrefDir.mkdir()).isTrue();
    }

    @Test
    public void testExecute() throws IOException {

        // PREPARE
        final Charset utf8 = Charset.forName("utf-8");
        final String expected = IOUtils
                .resourceToString(
                        "/" + GenerateSshKeyTask.class.getPackage().getName()
                                .replace('.', '/') + "/test-setup-ssh-git",
                        utf8);
        final String user = "peter_parker";
        final String host = "bitbucket.org";
        final GenerateSshKeyTask testee = new GenerateSshKeyTask("x", user, host,
                sshDir);
        final ConfigImpl config = new ConfigImpl("test", testee);
        config.init();

        // TEST
        testee.execute();

        // VERIFY

        LOG.error(sshDir + " exists: " + sshDir.exists());
        File[] files = sshDir.listFiles();
        if (files == null) {
            LOG.error(sshDir + " has no files ");
        } else {
            for (File file : files) {
                LOG.error(sshDir + ": " + file);
            }
        }

        assertThat(testee.getConfigFile()).usingCharset(utf8)
                .hasContent(expected);

    }

    @Test
    public void testValidateInstance() {

        final GenerateSshKeyTask testee = new GenerateSshKeyTask();

        final Validator validator = Validation.buildDefaultValidatorFactory()
                .getValidator();
        final Set<ConstraintViolation<GenerateSshKeyTask>> violations = validator
                .validate(testee, Default.class, UserInput.class);

        assertThat(DevSupWizUtils.violated(violations, "name")).isTrue();
        assertThat(DevSupWizUtils.violated(violations, "host")).isTrue();

    }

    @Test
    public void testMarshal() {

        // PREPARE
        final GenerateSshKeyTask testee = new GenerateSshKeyTask("x", "peter_parker",
                "bitbucket.org", sshDir);

        // TEST
        final String xml = JaxbUtils.marshal(testee, GenerateSshKeyTask.class);

        // VERIFY
        final Diff documentDiff = DiffBuilder.compare(JaxbUtils.XML_PREFIX
                + "<generate-ssh-key id=\"x\" name=\"peter_parker\" host=\"bitbucket.org\" />")
                .withTest(xml).ignoreWhitespace().build();

        assertThat(documentDiff.hasDifferences())
                .describedAs(documentDiff.toString()).isFalse();

    }

    @Test
    public void testUnmarshal() {

        // PREPARE
        final String xml = "<generate-ssh-key id=\"x\" />";

        // TEST
        final GenerateSshKeyTask testee = JaxbUtils.unmarshal(xml,
                GenerateSshKeyTask.class);

        // VERIFY
        assertThat(testee).isNotNull();
        assertThat(testee.getId()).isEqualTo("x");
        assertThat(testee.getName()).isNull();
        assertThat(testee.getHost()).isNull();
        assertThat(testee.getResource()).isNotEmpty();
        assertThat(testee.getFxml()).isNotEmpty();

    }

}
