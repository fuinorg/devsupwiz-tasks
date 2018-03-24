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

import static org.fuin.devsupwiz.common.DevSupWizUtils.MDC_TASK_KEY;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.enterprise.inject.Vetoed;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.fuin.devsupwiz.common.AbstractSetupTask;
import org.fuin.devsupwiz.common.LogOutputStream;
import org.fuin.devsupwiz.common.MultipleInstancesSetupTask;
import org.fuin.devsupwiz.common.ShellCommandExecutor;
import org.fuin.utils4j.Utils4J;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.event.Level;

/**
 * Clones one or more git repositories. Requires that a valid SSH key is
 * installed.
 */
@Vetoed
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = GitCloneTask.KEY)
public final class GitCloneTask extends AbstractSetupTask
        implements MultipleInstancesSetupTask {

    /** Unique normalized name of the task (for example used for FXML file). */
    static final String KEY = "git-clone";

    private static final Logger LOG = LoggerFactory
            .getLogger(GitCloneTask.class);

    @NotEmpty
    @XmlAttribute(name = "id")
    private String id;

    @NotEmpty(message = "target-dir")
    @XmlAttribute(name = "target-dir")
    private String targetDir;

    @XmlElement(name = "repository")
    private List<String> repositories;

    /**
     * Default constructor for JAXB.
     */
    protected GitCloneTask() {
        super();
    }

    /**
     * Constructor for tests.
     * 
     * @param id
     *            Unique task identifier.
     * @param targetDir
     *            Git base directory.
     * @param repositories
     *            List of repositories to clone.
     */
    public GitCloneTask(@NotEmpty final String id,
            @NotEmpty final String targetDir,
            @NotNull final List<String> repositories) {
        super();
        this.id = id;
        this.targetDir = targetDir;
        this.repositories = new ArrayList<>(repositories);
    }

    /**
     * Returns git base directory.
     * 
     * @return Target directory.
     */
    public final String getTargetDir() {
        return targetDir;
    }

    /**
     * Returns git base directory as file.
     * 
     * @return Target directory file.
     */
    public final File getTargetDirFile() {
        if (targetDir == null) {
            return new File(Utils4J.getUserHomeDir(), "git");
        }
        final String dir = StringUtils.replace(targetDir, "~",
                Utils4J.getUserHomeDir().toString());
        return new File(dir);
    }

    /**
     * Sets git base directory.
     * 
     * @param targetDir
     *            Target directory.
     */
    public final void setTargetDir(@NotEmpty final String targetDir) {
        this.targetDir = targetDir;
    }

    /**
     * Returns the list of repositores.
     * 
     * @return Immutable list.
     */
    public List<String> getRepositories() {
        if (repositories == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(repositories);
    }

    @Override
    public final void execute() {

        MDC.put(MDC_TASK_KEY, getTypeId());
        try {

            if (!alreadyExecuted()) {

                if (!getTargetDirFile().exists()) {
                    getTargetDirFile().mkdirs();
                }

                for (final String repository : repositories) {

                    final ShellCommandExecutor executor = new ShellCommandExecutor(
                            "git clone -v " + repository, 120,
                            new HashMap<String, String>(),
                            new LogOutputStream(Level.INFO),
                            new LogOutputStream(Level.ERROR),
                            getTargetDirFile());

                    final int result = executor.execute();
                    if (result != 0) {
                        throw new RuntimeException("Failed to clone "
                                + repository + ": " + result);
                    }

                }

                success();
                LOG.info("Successfully finished SSH git setup");

            }

        } finally {
            MDC.remove(MDC_TASK_KEY);
        }

    }

    @Override
    public final String getResource() {
        return this.getClass().getPackage().getName().replace('.', '/') + "/"
                + KEY;
    }

    @Override
    public final String getFxml() {
        return "/" + getResource() + ".fxml";
    }

    @Override
    public final String getId() {
        return id;
    }

    @Override
    public final String getType() {
        return KEY;
    }

    @Override
    public final String getTypeId() {
        return getType() + "[" + getId() + "]";
    }

}
