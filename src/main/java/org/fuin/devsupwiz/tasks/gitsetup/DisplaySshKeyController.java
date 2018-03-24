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

import static org.fuin.devsupwiz.common.DevSupWizUtils.getString;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.fuin.devsupwiz.common.Config;
import org.fuin.devsupwiz.common.Loggable;
import org.fuin.devsupwiz.common.SetupController;
import org.fuin.devsupwiz.common.SetupTask;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * UI controller for welcome screen.
 */
@Loggable
public class DisplaySshKeyController implements Initializable, SetupController {

    @FXML
    private Label title;

    @FXML
    private TextField name;

    @FXML
    private TextField host;

    @FXML
    private TextArea key;

    @Inject
    private Config config;

    private DisplaySshKeyTask task;

    private ResourceBundle bundle;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        this.bundle = resources;
        title.setText(getString(bundle, "title", config.getName()));

        final GenerateSshKeyTask keyGenTask = config
                .findTask(task.getTaskRef());
        if (keyGenTask == null) {
            throw new IllegalStateException(
                    "Wasn't able to find task: '" + task.getTaskRef() + "'");
        } else {
            name.setText(keyGenTask.getName());
            host.setText(keyGenTask.getHost());
            key.setText(keyGenTask.getPublicKey());
        }

    }

    @Override
    public void init(final SetupTask task) {
        if (!(task instanceof DisplaySshKeyTask)) {
            throw new IllegalArgumentException(
                    "Expected task of type " + DisplaySshKeyTask.class.getName()
                            + ", but was: " + task.getClass().getName());
        }
        this.task = (DisplaySshKeyTask) task;
        refreshStatus();
    }

    @Override
    public List<String> getValidationErrors() {
        return Collections.emptyList();
    }

    @Override
    public void save() {
        // Do nothing
    }

    @Override
    public SetupTask getTask() {
        return task;
    }

    @Override
    public void refreshStatus() {
        // Do nothing
    }

}
