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
import java.io.IOException;
import java.nio.charset.Charset;

import javax.enterprise.inject.Vetoed;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.FileUtils;
import org.fuin.devsupwiz.common.AbstractSetupTask;
import org.fuin.devsupwiz.common.UserInput;
import org.fuin.utils4j.Utils4J;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Creates and populates the "~/.gitconfig" file. Only one instance of this type
 * is allowed
 */
@Vetoed
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = CreateGitConfigTask.KEY)
public final class CreateGitConfigTask extends AbstractSetupTask {

    /** Unique normalized name of the task (for example used for FXML file). */
    static final String KEY = "create-git-config";

    private static final Logger LOG = LoggerFactory
            .getLogger(CreateGitConfigTask.class);

    @NotEmpty(message = "{create-git-config.name.empty}", groups = {
            UserInput.class })
    @XmlAttribute(name = "name")
    private String name;

    @NotEmpty(message = "{create-git-config.email.empty}", groups = {
            UserInput.class })
    @XmlAttribute(name = "email")
    private String email;

    @NotNull(message = "{create-git-config.push-default.empty}", groups = {
            UserInput.class })
    @XmlAttribute(name = "push-default")
    private PushDefault pushDefault;

    @NotNull(message = "configFile==null")
    private transient File configFile;

    /**
     * Default constructor for JAXB.
     */
    protected CreateGitConfigTask() {
        super();
        this.configFile = new File(Utils4J.getUserHomeDir(), ".gitconfig");
        this.pushDefault = PushDefault.SIMPLE;
    }

    /**
     * Constructor for tests.
     * 
     * @param name
     *            User's name.
     * @param email
     *            User's email address.
     * @param pushDefault
     *            Default setting for push.
     */
    public CreateGitConfigTask(@NotEmpty final String name,
            @NotEmpty final String email,
            @NotNull final PushDefault pushDefault) {
        this(name, email, pushDefault,
                new File(Utils4J.getUserHomeDir(), ".gitconfig"));
    }

    /**
     * Constructor for tests.
     * 
     * @param name
     *            User's name.
     * @param email
     *            User's email address.
     * @param pushDefault
     *            Default setting for push.
     * @param configFile
     *            File to create.
     */
    public CreateGitConfigTask(@NotEmpty final String name,
            @NotEmpty final String email,
            @NotNull final PushDefault pushDefault,
            @NotNull final File configFile) {
        super();
        this.name = name;
        this.email = email;
        this.pushDefault = pushDefault;
        this.configFile = configFile;
    }

    /**
     * Returns user's name.
     * 
     * @return First name and last name.
     */
    public final String getName() {
        return name;
    }

    /**
     * Sets user's name.
     * 
     * @param name
     *            First name and last name.
     */
    public final void setName(@NotEmpty final String name) {
        this.name = name;
    }

    /**
     * Returns user's email.
     * 
     * @return Email address.
     */
    public final String getEmail() {
        return email;
    }

    /**
     * Sets user's email.
     * 
     * @param email
     *            Email address.
     */
    public final void setEmail(@NotEmpty final String email) {
        this.email = email;
    }

    /**
     * Returns the push default.
     * 
     * @return Push default.
     */
    public final PushDefault getPushDefault() {
        return pushDefault;
    }

    /**
     * Sets the push default.
     * 
     * @param pushDefault
     *            Push default.
     */
    public final void setPushDefault(@NotNull final PushDefault pushDefault) {
        this.pushDefault = pushDefault;
    }

    @Override
    public final void execute() {

        MDC.put(MDC_TASK_KEY, getType());
        try {

            if (!alreadyExecuted()) {
                final String str = "[user]\n" + "\tname = " + name + "\n"
                        + "\temail = " + email + "\n" + "[push]\n"
                        + "\tdefault = " + pushDefault.name().toLowerCase()
                        + "\n";
                try {

                    FileUtils.writeStringToFile(configFile, str,
                            Charset.forName("utf-8"));

                    success();
                    LOG.info("Successfully create git config: {}", configFile);

                } catch (final IOException ex) {
                    throw new RuntimeException(
                            "Wasn't able to write git config: " + configFile,
                            ex);
                }
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
    public final String getType() {
        return KEY;
    }

    @Override
    public final String getTypeId() {
        return KEY;
    }

}
