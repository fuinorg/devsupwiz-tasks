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

import javax.enterprise.inject.Vetoed;
import javax.validation.constraints.NotEmpty;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.fuin.devsupwiz.common.AbstractSetupTask;
import org.fuin.devsupwiz.common.MultipleInstancesSetupTask;

/**
 * Display a newly generated SSH public key to allow the user to copy it.
 */
@Vetoed
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = DisplaySshKeyTask.KEY)
public final class DisplaySshKeyTask extends AbstractSetupTask
        implements MultipleInstancesSetupTask {

    /** Unique normalized name of the task (for example used for FXML file). */
    static final String KEY = "display-ssh-key";

    @NotEmpty
    @XmlAttribute(name = "id")
    private String id;

    @NotEmpty
    @XmlAttribute(name = "ref")
    private String taskRef;

    /**
     * Default constructor for JAXB.
     */
    protected DisplaySshKeyTask() {
        super();
    }

    /**
     * Constructor for tests.
     * 
     * @param id
     *            Unique task identifier.
     * @param taskRef
     *            Reference to the {@link GenerateSshKeyTask} to display it's public key.
     */
    public DisplaySshKeyTask(@NotEmpty final String id,
            @NotEmpty final String taskRef) {
        super();
        this.id = id;
        this.taskRef = taskRef;
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

    /**
     * Returns a unique reference to the {@link GenerateSshKeyTask} that created
     * the new key.
     * 
     * @return Unique identifier of the task.
     */
    public final String getTaskRef() {
        return taskRef;
    }

    @Override
    public void execute() {
        // Do nothing
    }

}
