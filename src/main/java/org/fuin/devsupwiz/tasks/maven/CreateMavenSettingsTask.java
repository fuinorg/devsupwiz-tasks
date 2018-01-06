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

import static org.fuin.devsupwiz.common.DevSupWizUtils.MDC_TASK_KEY;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.fuin.devsupwiz.common.SetupTask;
import org.fuin.devsupwiz.common.ValidateInstance;
import org.fuin.utils4j.Utils4J;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Creates and populates the "~/.m2/settings.xml" file.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = CreateMavenSettingsTask.KEY)
public class CreateMavenSettingsTask implements SetupTask {

    private static final String M2_SETTINGS_XML = ".m2/settings.xml";

    /** Unique normalized name of the task (for example used for FXML file). */
    static final String KEY = "create-maven-settings";

    private static final Logger LOG = LoggerFactory
            .getLogger(CreateMavenSettingsTask.class);

    @NotEmpty
    @XmlAttribute(name = "id")
    private String id;

    @NotEmpty(message = "{create-maven-settings.template.empty}")
    @XmlAttribute(name = "template")
    private String template;

    @NotEmpty(message = "{create-maven-settings.name.empty}")
    @XmlTransient
    private String name;

    @NotEmpty(message = "{create-maven-settings.password.empty}")
    @XmlTransient
    private String password;

    @NotNull(message = "settingsFile==null")
    @XmlTransient
    private File settingsFile;

    /**
     * Default constructor for JAXB.
     */
    protected CreateMavenSettingsTask() {
        super();
        this.settingsFile = new File(Utils4J.getUserHomeDir(), M2_SETTINGS_XML);
    }

    /**
     * Constructor for tests.
     * 
     * @param id
     *            Unique task identifier.
     * @param template
     *            Source template for the "settings.xml".
     * @param name
     *            User name.
     * @param password
     *            User password.
     */
    public CreateMavenSettingsTask(@NotEmpty final String id,
            @NotEmpty final String template, @NotEmpty final String name,
            @NotEmpty final String password) {
        this(id, template, name, password,
                new File(Utils4J.getUserHomeDir(), M2_SETTINGS_XML));
    }

    /**
     * Constructor for tests.
     * 
     * @param id
     *            Unique task identifier.
     * @param template
     *            Source template for the "settings.xml".
     * @param name
     *            User name.
     * @param password
     *            User password.
     * @param settingsFile
     *            File to create.
     */
    public CreateMavenSettingsTask(@NotEmpty final String id,
            @NotEmpty final String template, @NotEmpty final String name,
            @NotEmpty final String password, @NotNull final File settingsFile) {
        super();
        this.id = id;
        this.template = template;
        this.name = name;
        this.password = password;
        this.settingsFile = settingsFile;
    }

    /**
     * Returns the template.
     * 
     * @return Source template for the "settings.xml".
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Sets the template.
     * 
     * @param template
     *            Source template for the "settings.xml".
     */
    public void setTemplate(@NotEmpty final String template) {
        this.template = template;
    }

    /**
     * Returns template as file.
     * 
     * @return Template file.
     */
    public File getTemplateFile() {
        if (template == null) {
            throw new IllegalStateException(
                    "The template name for the 'settings.xml' is not set");
        }
        final File file = new File(StringUtils.replace(template, "~",
                Utils4J.getUserHomeDir().toString()));
        if (!file.exists()) {
            throw new IllegalStateException(
                    "The template files does not exist: " + file);
        }
        return file;
    }

    /**
     * Returns user name.
     * 
     * @return User name for Maven repository.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets user name.
     * 
     * @param name
     *            User name for Maven repository.
     */
    public void setName(@NotEmpty final String name) {
        this.name = name;
    }

    /**
     * Returns user password.
     * 
     * @return Maven repository password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets user password.
     * 
     * @param password
     *            Maven repository password.
     */
    public void setPassword(@NotEmpty final String password) {
        this.password = password;
    }

    @Override
    public boolean alreadyExecuted() {
        return settingsFile.exists();
    }

    @ValidateInstance
    @Override
    public void execute() {

        MDC.put(MDC_TASK_KEY, getTypeId());
        try {

            if (!alreadyExecuted()) {

                if (!settingsFile.getParentFile().exists()) {
                    if (!settingsFile.getParentFile().mkdir()) {
                        throw new RuntimeException(
                                "Wasn't able to create Maven directory: "
                                        + settingsFile.getParent());
                    }
                }

                try {
                    final String str = FileUtils.readFileToString(
                            getTemplateFile(), Charset.forName("utf-8"));
                    final String nameReplaced = StringUtils.replace(str,
                            "((USER))", name);
                    final String pwAndNameReplaced = StringUtils
                            .replace(nameReplaced, "((PW))", password);

                    FileUtils.writeStringToFile(settingsFile, pwAndNameReplaced,
                            Charset.forName("utf-8"));

                    LOG.info("Successfully create Maven settings: {}",
                            settingsFile);

                } catch (final IOException ex) {
                    throw new RuntimeException(
                            "Wasn't able to write Maven settings: "
                                    + settingsFile,
                            ex);
                }
            }

        } finally {
            MDC.remove(MDC_TASK_KEY);
        }

    }

    @Override
    public String getResource() {
        return this.getClass().getPackage().getName().replace('.', '/') + "/"
                + KEY;
    }

    @Override
    public String getFxml() {
        return "/" + getResource() + ".fxml";
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getType() {
        return KEY;
    }

    @Override
    public String getTypeId() {
        return getType() + "[" + getId() + "]";
    }

}
