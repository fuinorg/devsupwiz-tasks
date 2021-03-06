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

import static org.fuin.devsupwiz.common.DevSupWizFxUtils.createIconOk24x24;
import static org.fuin.devsupwiz.common.DevSupWizFxUtils.createIconTodo24x24;
import static org.fuin.devsupwiz.common.DevSupWizUtils.violated;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.controlsfx.control.decoration.Decorator;
import org.controlsfx.control.decoration.GraphicDecoration;
import org.fuin.devsupwiz.common.Loggable;
import org.fuin.devsupwiz.common.SetupController;
import org.fuin.devsupwiz.common.SetupTask;
import org.fuin.devsupwiz.common.UserInput;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * UI controller for personal data configuration.
 */
@Loggable
public class SetPersonalDataController implements SetupController {

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField email;

    @FXML
    private Label title;

    @Inject
    private Validator validator;

    private SetPersonalDataTask task;

    @Override
    public void init(final SetupTask task) {
        if (!(task instanceof SetPersonalDataTask)) {
            throw new IllegalArgumentException("Expected task of type "
                    + SetPersonalDataTask.class.getName() + ", but was: "
                    + task.getClass().getName());
        }
        this.task = (SetPersonalDataTask) task;
        refreshStatus();
    }

    @Override
    public List<String> getValidationErrors() {

        final List<String> errors = new ArrayList<String>();

        if (!task.alreadyExecuted()) {

            // Execute bean validation using a new task instance
            final SetPersonalDataTask t = new SetPersonalDataTask(
                    firstName.getText(), lastName.getText(), email.getText());
            final Set<ConstraintViolation<SetPersonalDataTask>> violations = validator
                    .validate(t, UserInput.class);
            for (final ConstraintViolation<SetPersonalDataTask> violation : violations) {
                errors.add(violation.getMessage());
            }

            // Show validation errors on UI
            Decorator.removeAllDecorations(firstName);
            Decorator.removeAllDecorations(lastName);
            Decorator.removeAllDecorations(email);

            if (violated(violations, firstName.getId())) {
                Decorator.addDecoration(firstName, new GraphicDecoration(
                        createErrorNode(), Pos.TOP_RIGHT));
            }
            if (violated(violations, lastName.getId())) {
                Decorator.addDecoration(lastName, new GraphicDecoration(
                        createErrorNode(), Pos.TOP_RIGHT));
            }
            if (violated(violations, email.getId())) {
                Decorator.addDecoration(email, new GraphicDecoration(
                        createErrorNode(), Pos.TOP_RIGHT));
            }

        }

        // Return error messages to display on main screen
        return errors;
    }

    private Node createErrorNode() {
        return new ImageView(new Image("/error-16x16.png"));
    }

    @Override
    public void save() {
        task.setFirstName(firstName.getText());
        task.setLastName(lastName.getText());
        task.setEmail(email.getText());
    }

    private void displayData() {
        firstName.setText(task.getFirstName());
        lastName.setText(task.getLastName());
        email.setText(task.getEmail());
    }

    @Override
    public SetupTask getTask() {
        return task;
    }

    @Override
    public void refreshStatus() {
        final boolean alreadyExecuted = task.alreadyExecuted();
        displayData();
        firstName.setDisable(alreadyExecuted);
        lastName.setDisable(alreadyExecuted);
        email.setDisable(alreadyExecuted);
        if (alreadyExecuted) {
            title.setGraphic(createIconOk24x24());
        } else {
            title.setGraphic(createIconTodo24x24());
        }
    }

}
