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

import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_WRITE;
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
import org.apache.commons.lang3.StringUtils;
import org.fuin.devsupwiz.common.AbstractSetupTask;
import org.fuin.devsupwiz.common.DevSupWizUtils;
import org.fuin.devsupwiz.common.UserInput;
import org.fuin.utils4j.Utils4J;
import org.slf4j.MDC;

/**
 * Creates and populates the "~/.m2/settings.xml" file.
 */
@Vetoed
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = CreateMavenSettingsTask.KEY)
public final class CreateMavenSettingsTask extends AbstractSetupTask {

    private static final String M2_SETTINGS_XML = ".m2/settings.xml";

    /** Unique normalized name of the task (for example used for FXML file). */
    static final String KEY = "create-maven-settings";

    @NotEmpty(message = "{create-maven-settings.template.empty}")
    @XmlAttribute(name = "template")
    private String template;

    @NotEmpty(message = "{create-maven-settings.name.empty}", groups = { CredentialsEnabled.class })
    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "hint")
    private String hint;

    @XmlAttribute(name = "skip-credentials")
    private Boolean skipCredentials;

    @NotEmpty(message = "{create-maven-settings.password.empty}", groups = { CredentialsEnabled.class })
    private transient String password;

    @NotNull(message = "settingsFile==null")
    private transient File settingsFile;

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
     * @param template
     *            Source template for the "settings.xml".
     * @param name
     *            User name.
     * @param password
     *            User password.
     */
    public CreateMavenSettingsTask(@NotEmpty final String template, @NotEmpty final String name, @NotEmpty final String password) {
        this(template, name, password, new File(Utils4J.getUserHomeDir(), M2_SETTINGS_XML));
    }

    /**
     * Constructor for tests.
     * 
     * @param template
     *            Source template for the "settings.xml".
     * @param name
     *            User name.
     * @param password
     *            User password.
     * @param settingsFile
     *            File to create.
     */
    public CreateMavenSettingsTask(@NotEmpty final String template, @NotEmpty final String name, @NotEmpty final String password,
            @NotNull final File settingsFile) {
        super();
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
    public final String getTemplate() {
        return template;
    }

    /**
     * Sets the template.
     * 
     * @param template
     *            Source template for the "settings.xml".
     */
    public final void setTemplate(@NotEmpty final String template) {
        this.template = template;
    }

    /**
     * Returns template as file.
     * 
     * @return Template file.
     */
    public final File getTemplateFile() {
        if (template == null) {
            throw new IllegalStateException("The template name for the 'settings.xml' is not set");
        }
        final File file = new File(StringUtils.replace(template, "~", Utils4J.getUserHomeDir().toString()));
        if (!file.exists()) {
            throw new IllegalStateException("The template files does not exist: " + file);
        }
        return file;
    }

    /**
     * Returns user name.
     * 
     * @return User name for Maven repository.
     */
    public final String getName() {
        return name;
    }

    /**
     * Sets user name.
     * 
     * @param name
     *            User name for Maven repository.
     */
    public final void setName(@NotEmpty final String name) {
        this.name = name;
    }

    /**
     * Returns user password.
     * 
     * @return Maven repository password.
     */
    public final String getPassword() {
        return password;
    }

    /**
     * Sets user password.
     * 
     * @param password
     *            Maven repository password.
     */
    public final void setPassword(@NotEmpty final String password) {
        this.password = password;
    }

    /**
     * Returns the hint what the Maven credentials are for.
     * 
     * @return Hint for the user what credentials to use.
     */
    public final String getHint() {
        return hint;
    }

    /**
     * Sets the hint what the Maven credentials are for.
     * 
     * @param hint
     *            Hint for the user what credentials to use.
     */
    public final void setHint(final String hint) {
        this.hint = hint;
    }

    /**
     * Defines if no credentials are required.
     * 
     * @return TRUE if the user does not need to enter the credentials. In case
     *         the value is <code>null</code> this defaults to FALSE.
     */
    public final boolean isSkipCredentials() {
        if (skipCredentials == null) {
            return false;
        }
        return skipCredentials;
    }

    /**
     * Defines if no credentials are required.
     * 
     * @return TRUE if the user does not need to enter the credentials or
     *         <code>null</code> if undefined.
     */
    public Boolean getSkipCredentials() {
        return skipCredentials;
    }

    /**
     * Defines if no credentials are required.
     * 
     * @param skipCredentials
     *            TRUE if the user does not need to enter the credentials-
     */
    public final void setSkipCredentials(final Boolean skipCredentials) {
        this.skipCredentials = skipCredentials;
    }

    @Override
    public final void execute() {

        MDC.put(MDC_TASK_KEY, getType());
        try {

            if (!settingsFile.getParentFile().exists()) {
                if (!settingsFile.getParentFile().mkdir()) {
                    throw new RuntimeException("Wasn't able to create Maven directory: " + settingsFile.getParent());
                }
            }

            try {
                final String str = FileUtils.readFileToString(getTemplateFile(), Charset.forName("utf-8"));
                if (isSkipCredentials()) {
                    FileUtils.writeStringToFile(settingsFile, str, Charset.forName("utf-8"));
                } else {
                    final String nameReplaced = StringUtils.replace(str, "((USER))", name);
                    final String pwAndNameReplaced = StringUtils.replace(nameReplaced, "((PW))", password);
                    FileUtils.writeStringToFile(settingsFile, pwAndNameReplaced, Charset.forName("utf-8"));
                }

                // Only owner is allowed to access settings.xml with repo pw
                DevSupWizUtils.setFilePermissions(settingsFile, OWNER_READ, OWNER_WRITE);

            } catch (final IOException ex) {
                throw new RuntimeException("Wasn't able to write Maven settings: " + settingsFile, ex);
            }

        } finally {
            MDC.remove(MDC_TASK_KEY);
        }

    }

    @Override
    public final String getResource() {
        return this.getClass().getPackage().getName().replace('.', '/') + "/" + KEY;
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
