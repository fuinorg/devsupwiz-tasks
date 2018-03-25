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
package org.fuin.devsupwiz.tasks.personal;

import javax.enterprise.inject.Vetoed;
import javax.validation.constraints.NotEmpty;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.fuin.devsupwiz.common.AbstractSetupTask;
import org.fuin.devsupwiz.common.UserInput;

/**
 * Sets the developer's personal data like name and email address. There can
 * only be one task of this type in a configuration.
 */
@Vetoed
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = SetPersonalDataTask.KEY)
public final class SetPersonalDataTask extends AbstractSetupTask {

    /** Unique normalized name of the task (for example used for FXML file). */
    public static final String KEY = "set-personal-data";

    /** Key to retrieve first name from the user preferences. */
    public static final String FIRST_NAME_KEY = KEY + ".firstName";

    /** Key to retrieve last name from the user preferences. */
    public static final String LAST_NAME_KEY = KEY + ".lastName";

    /** Key to retrieve email address from the user preferences. */
    public static final String EMAIL_KEY = KEY + ".email";

    @XmlAttribute(name = "first-name")
    @NotEmpty(message = "{first-name.empty}", groups = { UserInput.class })
    private String firstName;

    @XmlAttribute(name = "last-name")
    @NotEmpty(message = "{last-name.empty}", groups = { UserInput.class })
    private String lastName;

    @XmlAttribute(name = "exmail")
    @NotEmpty(message = "{email.empty}", groups = { UserInput.class })
    private String email;

    /**
     * Default constructor for JAXB.
     */
    protected SetPersonalDataTask() {
        super();
    }

    /**
     * Constructor with all mandatory data.
     * 
     * @param firstName
     *            First name (like 'Peter').
     * @param lastName
     *            Last name (like 'Parker').
     * @param email
     *            Email (like 'peter.parker@wherever-not-existing.com').
     */
    public SetPersonalDataTask(@NotEmpty final String firstName,
            @NotEmpty final String lastName, @NotEmpty final String email) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * Returns the first name.
     * 
     * @return First name (like 'Peter').
     */
    public final String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name.
     * 
     * @param firstName
     *            First name (like 'Peter').
     */
    public final void setFirstName(@NotEmpty final String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the last name.
     * 
     * @return Last name (like 'Parker').
     */
    public final String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name.
     * 
     * @param lastName
     *            Last name (like 'Parker').
     */
    public final void setLastName(@NotEmpty final String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns first plus lastname divided by a space. If only one of the names
     * is set this one is returned. Returns an empty space if none of them is
     * set.
     * 
     * @return Full name.
     */
    public final String getFullName() {
        if (firstName == null) {
            if (lastName == null) {
                return "";
            }
            return lastName;
        }
        if (lastName == null) {
            return firstName;
        }
        return firstName + " " + lastName;
    }

    /**
     * Returns the email.
     * 
     * @return Email (like 'peter.parker@wherever-not-existing.com').
     */
    public final String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     * 
     * @param email
     *            Email (like 'peter.parker@wherever-not-existing.com').
     */
    public final void setEmail(@NotEmpty final String email) {
        this.email = email;
    }

    @Override
    public void execute() {
        // Nothing to do - Just user input
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
