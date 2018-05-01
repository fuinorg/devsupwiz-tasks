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
package org.fuin.devsupwiz.tasks.hostname;

import static org.fuin.devsupwiz.common.DevSupWizUtils.MDC_TASK_KEY;

import java.util.HashMap;

import javax.enterprise.inject.Vetoed;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.fuin.devsupwiz.common.AbstractSetupTask;
import org.fuin.devsupwiz.common.LogOutputStream;
import org.fuin.devsupwiz.common.ShellCommandExecutor;
import org.fuin.devsupwiz.common.UserInput;
import org.slf4j.MDC;
import org.slf4j.event.Level;

/**
 * Sets the hostname for the system.
 */
@Vetoed
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = SetHostnameTask.KEY)
public final class SetHostnameTask extends AbstractSetupTask {

    /** Unique normalized name of the task (for example used for FXML file). */
    static final String KEY = "set-hostname";

    @XmlAttribute(name = "name")
    @Pattern(regexp = "[a-z][a-z0-9\\-]*", message = "{set-hostname.pattern}", groups = { UserInput.class })
    @NotEmpty(message = "{set-hostname.empty}", groups = { UserInput.class })
    private String name;

    /**
     * Default constructor for JAXB.
     */
    protected SetHostnameTask() {
        super();
    }

    /**
     * Constructor with name.
     * 
     * @param name
     *            Host name.
     */
    public SetHostnameTask(@NotEmpty final String name) {
        super();
        this.name = name;
    }

    /**
     * Returns name.
     * 
     * @return Host name.
     */
    public final String getName() {
        return name;
    }

    /**
     * Sets name.
     * 
     * @param name
     *            Host name.
     */
    public final void setName(@NotEmpty final String name) {
        this.name = name;
    }

    @Override
    public final void execute() {

        MDC.put(MDC_TASK_KEY, getType());
        try {

            final ShellCommandExecutor executor = new ShellCommandExecutor("hostnamectl set-hostname '" + name + "'", 5,
                    new HashMap<String, String>(), new LogOutputStream(Level.INFO), new LogOutputStream(Level.ERROR));

            final int result = executor.execute();
            if (result != 0) {
                throw new RuntimeException("Error # " + result + " while trying to set the host name to '" + name + "'");
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
