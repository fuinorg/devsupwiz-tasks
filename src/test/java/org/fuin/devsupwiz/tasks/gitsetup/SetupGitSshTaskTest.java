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
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.fuin.utils4j.JaxbUtils;
import org.fuin.utils4j.PropertiesFilePreferencesFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

/**
 * Test for the {@link SetupGitSshTask} class.
 */
public class SetupGitSshTaskTest {

    private static final String PREFERENCES_FACTORY = "java.util.prefs.PreferencesFactory";

    private static final Logger LOG = LoggerFactory
            .getLogger(SetupGitSshTaskTest.class);

    private File sshDir;

    private String originalUserPrefDir;

    private String originalPreferencesFactory;

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

        originalUserPrefDir = System
                .getProperty(PropertiesFilePreferencesFactory.USER_PREF_DIR);
        originalPreferencesFactory = System.getProperty(PREFERENCES_FACTORY);

        System.setProperty(PropertiesFilePreferencesFactory.USER_PREF_DIR,
                userPrefDir.toString());
        System.setProperty(PREFERENCES_FACTORY,
                PropertiesFilePreferencesFactory.class.getName());

    }

    @After
    public void tearDown() {
        if (originalUserPrefDir == null) {
            System.clearProperty(
                    PropertiesFilePreferencesFactory.USER_PREF_DIR);
        } else {
            System.setProperty(PropertiesFilePreferencesFactory.USER_PREF_DIR,
                    originalUserPrefDir);
        }
        if (originalPreferencesFactory == null) {
            System.clearProperty(PREFERENCES_FACTORY);
        } else {
            System.setProperty(PREFERENCES_FACTORY, originalPreferencesFactory);
        }
    }

    @Test
    public void testExecute() throws IOException {

        // PREPARE
        final Charset utf8 = Charset.forName("utf-8");
        final String expected = IOUtils
                .resourceToString(
                        "/" + SetupGitSshTask.class.getPackage().getName()
                                .replace('.', '/') + "/test-setup-ssh-git",
                        utf8);
        final String user = "peter_parker";
        final String host = "bitbucket.org";
        final SetupGitSshTask testee = new SetupGitSshTask("x", user, "secret",
                GitProvider.BITBUCKET, host, sshDir, false);

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
    public void testExecuteValidateInstance() {

        try (final SeContainer container = SeContainerInitializer.newInstance()
                .initialize()) {
            final SetupGitSshTask testee = container
                    .select(SetupGitSshTask.class).get();
            try {
                testee.execute();
                fail();
            } catch (final IllegalStateException ex) {
                assertThat(ex.getMessage()).startsWith("The instance of type '"
                        + SetupGitSshTask.class.getName() + "' "
                        + "was invalid when calling method");
                assertThat(ex.getMessage()).contains("Provider is required");
                assertThat(ex.getMessage()).contains("Password is required");
                assertThat(ex.getMessage()).contains("Username is required");
                assertThat(ex.getMessage()).contains("Host is required");
            }
        }

    }

    @Test
    public void testMarshal() {

        // PREPARE
        final SetupGitSshTask testee = new SetupGitSshTask("x", "peter_parker",
                "secret", GitProvider.BITBUCKET, "bitbucket.org", sshDir,
                false);

        // TEST
        final String xml = JaxbUtils.marshal(testee, SetupGitSshTask.class);

        // VERIFY
        final Diff documentDiff = DiffBuilder.compare(JaxbUtils.XML_PREFIX
                + "<setup-git-ssh id=\"x\" provider=\"bitbucket\" host=\"bitbucket.org\" />")
                .withTest(xml).ignoreWhitespace().build();

        assertThat(documentDiff.hasDifferences())
                .describedAs(documentDiff.toString()).isFalse();

    }

    @Test
    public void testUnmarshal() {

        // PREPARE
        final String xml = "<setup-git-ssh id=\"x\" />";

        // TEST
        final SetupGitSshTask testee = JaxbUtils.unmarshal(xml,
                SetupGitSshTask.class);

        // VERIFY
        assertThat(testee).isNotNull();
        assertThat(testee.getId()).isEqualTo("x");
        assertThat(testee.getName()).isNull();
        assertThat(testee.getPassword()).isNull();
        assertThat(testee.getProvider()).isNull();
        assertThat(testee.getHost()).isNull();
        assertThat(testee.getResource()).isNotEmpty();
        assertThat(testee.getFxml()).isNotEmpty();

    }

}
