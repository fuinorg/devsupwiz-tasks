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
package org.fuin.devsupwiz.tasks;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.fuin.devsupwiz.common.ConfigImpl;
import org.fuin.devsupwiz.tasks.gitsetup.CreateGitConfigTask;
import org.fuin.devsupwiz.tasks.gitsetup.GitCloneTask;
import org.fuin.devsupwiz.tasks.gitsetup.GitProvider;
import org.fuin.devsupwiz.tasks.gitsetup.PushDefault;
import org.fuin.devsupwiz.tasks.gitsetup.SetupGitSshTask;
import org.fuin.devsupwiz.tasks.hostname.SetHostnameTask;
import org.fuin.devsupwiz.tasks.maven.CreateMavenSettingsTask;
import org.fuin.devsupwiz.tasks.personal.SetPersonalDataTask;
import org.fuin.utils4j.JaxbUtils;
import org.fuin.utils4j.Utils4J;
import org.junit.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

/**
 * Tests marshal/unmarshal of the configuration.
 */
public class TestMarshalUnmarshalConfig {

    @Test
    public void testMarshal() throws IOException {

        // PREPARE
        final URL url = Utils4J
                .url("classpath:org/fuin/devsupwiz/tasks/my-project-setup.xml");
        final String original = IOUtils.toString(url, Charset.forName("utf-8"));
        final ConfigImpl testee = ConfigImpl.load(url);

        // TEST
        final String xml = JaxbUtils.marshal(testee, ConfigImpl.class,
                CreateGitConfigTask.class, SetHostnameTask.class,
                SetPersonalDataTask.class, GitCloneTask.class,
                CreateMavenSettingsTask.class, SetupGitSshTask.class);

        // VERIFY
        final Diff documentDiff = DiffBuilder.compare(original).withTest(xml)
                .ignoreWhitespace().build();

        assertThat(documentDiff.hasDifferences())
                .describedAs(documentDiff.toString()).isFalse();

    }

    @Test
    public void testUnmarshal() throws IOException {

        // PREPARE
        final SetPersonalDataTask setPersonalDataTask = new SetPersonalDataTask(
                "a", "b", "c");
        final SetHostnameTask setHostnameTask = new SetHostnameTask("a");
        final CreateGitConfigTask createGitConfigTask = new CreateGitConfigTask(
                "a", "b", PushDefault.SIMPLE);
        final CreateMavenSettingsTask createMavenSettingsTask = new CreateMavenSettingsTask(
                "a", "b", "c");
        final SetupGitSshTask setupGitSshTask = new SetupGitSshTask("1", "a",
                "b", GitProvider.GITHUB, "c", true);
        final GitCloneTask gitCloneTask = new GitCloneTask("1", "a",
                new ArrayList<>());

        // TEST
        final ConfigImpl testee = ConfigImpl.load(Utils4J.url(
                "classpath:org/fuin/devsupwiz/tasks/my-project-setup.xml"));

        // VERIFY
        assertThat(testee).isNotNull();
        assertThat(testee.getName()).isEqualTo("my-project");
        assertThat(testee.getTasks()).contains(setPersonalDataTask,
                setHostnameTask, createGitConfigTask, setupGitSshTask,
                gitCloneTask, createMavenSettingsTask);

    }

}
