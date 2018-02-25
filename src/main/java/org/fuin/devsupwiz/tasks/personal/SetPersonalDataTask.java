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

import static org.fuin.devsupwiz.common.DevSupWizUtils.MDC_TASK_KEY;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.validation.constraints.NotEmpty;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.fuin.devsupwiz.common.AbstractSetupTask;
import org.fuin.devsupwiz.common.ValidateInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Sets the developer's personal data like name and email address. There can
 * only be one task of this type in a configuration.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = SetPersonalDataTask.KEY)
public class SetPersonalDataTask extends AbstractSetupTask {

    /** Unique normalized name of the task (for example used for FXML file). */
    static final String KEY = "set-personal-data";

    /** Key to retrieve first name from the user preferences. */
    public static final String FIRST_NAME_KEY = KEY + ".firstName";

    /** Key to retrieve last name from the user preferences. */
    public static final String LAST_NAME_KEY = KEY + ".lastName";

    /** Key to retrieve email address from the user preferences. */
    public static final String EMAIL_KEY = KEY + ".email";

    private static final Logger LOG = LoggerFactory
            .getLogger(SetPersonalDataTask.class);

    @XmlTransient
    private Preferences userPrefs;

    @NotEmpty(message = "{first-name.empty}")
    @XmlTransient
    private String firstName;

    @NotEmpty(message = "{last-name.empty}")
    @XmlTransient
    private String lastName;

    @NotEmpty(message = "{email.empty}")
    @XmlTransient
    private String email;

    /**
     * Default constructor for JAXB.
     */
    protected SetPersonalDataTask() {
        super();
        userPrefs = Preferences.userRoot();
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
        userPrefs = Preferences.userRoot();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * Returns the first name.
     * 
     * @return First name (like 'Peter').
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name.
     * 
     * @param firstName
     *            First name (like 'Peter').
     */
    public void setFirstName(@NotEmpty final String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the last name.
     * 
     * @return Last name (like 'Parker').
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name.
     * 
     * @param lastName
     *            Last name (like 'Parker').
     */
    public void setLastName(@NotEmpty final String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the email.
     * 
     * @return Email (like 'peter.parker@wherever-not-existing.com').
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     * 
     * @param email
     *            Email (like 'peter.parker@wherever-not-existing.com').
     */
    public void setEmail(@NotEmpty final String email) {
        this.email = email;
    }

    @Override
    public boolean alreadyExecuted() {
        return userPrefs.getBoolean(KEY, false);
    }

    @ValidateInstance
    @Override
    public void execute() {

        MDC.put(MDC_TASK_KEY, getType());
        try {

            if (!alreadyExecuted()) {

                try {
                    userPrefs.putBoolean(KEY, true);
                    userPrefs.put(FIRST_NAME_KEY, firstName);
                    userPrefs.put(LAST_NAME_KEY, lastName);
                    userPrefs.put(EMAIL_KEY, email);
                    userPrefs.flush();
                } catch (final BackingStoreException ex) {
                    throw new RuntimeException(
                            "Failed to save the personal data in the user's preferences",
                            ex);
                }
                LOG.info(
                        "Successfully saved the personal data: firstName='{}', lastName='{}', email='{}'",
                        firstName, lastName, email);

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
    public String getType() {
        return KEY;
    }

    @Override
    public String getTypeId() {
        return KEY;
    }

}
