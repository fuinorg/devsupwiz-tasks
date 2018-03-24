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

import static org.fuin.devsupwiz.common.DevSupWizFxUtils.createIconError16x16;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * UI controller for ssh key pair generation.
 */
@Loggable
public class GenerateSshKeyController implements SetupController {

    @FXML
    private TextField name;

    @FXML
    private TextField host;

    @FXML
    private Label title;

    @Inject
    private Validator validator;

    private GenerateSshKeyTask task;

    @Override
    public void init(final SetupTask setupTask) {
        if (!(setupTask instanceof GenerateSshKeyTask)) {
            throw new IllegalArgumentException(
                    "Expected task of type " + GenerateSshKeyTask.class.getName()
                            + ", but was: " + setupTask.getClass().getName());
        }
        task = (GenerateSshKeyTask) setupTask;

        host.setText(task.getHost());

        refreshStatus();
    }

    @Override
    public List<String> getValidationErrors() {

        final List<String> errors = new ArrayList<String>();

        if (!task.alreadyExecuted()) {

            // Execute bean validation using a new task instance
            final GenerateSshKeyTask t = new GenerateSshKeyTask("x", name.getText(),
                    host.getText());
            final Set<ConstraintViolation<GenerateSshKeyTask>> violations = validator
                    .validate(t, UserInput.class);
            for (final ConstraintViolation<GenerateSshKeyTask> violation : violations) {
                errors.add(violation.getMessage());
            }

            // Show validation errors on UI
            Decorator.removeAllDecorations(name);
            if (violated(violations, name.getId())) {
                Decorator.addDecoration(name, new GraphicDecoration(
                        createIconError16x16(), Pos.TOP_RIGHT));
            }
            Decorator.removeAllDecorations(host);
            if (violated(violations, host.getId())) {
                Decorator.addDecoration(host, new GraphicDecoration(
                        createIconError16x16(), Pos.TOP_RIGHT));
            }

        }

        // Return error messages to display on main screen
        return errors;
    }

    @Override
    public void save() {
        task.setName(name.getText());
        task.setHost(host.getText());
    }

    @Override
    public SetupTask getTask() {
        return task;
    }

    @Override
    public void refreshStatus() {
        final boolean alreadyExecuted = task.alreadyExecuted();
        name.setDisable(alreadyExecuted);
        host.setDisable(alreadyExecuted);
        if (alreadyExecuted) {
            title.setGraphic(createIconOk24x24());
        } else {
            title.setGraphic(createIconTodo24x24());
        }
    }

}
