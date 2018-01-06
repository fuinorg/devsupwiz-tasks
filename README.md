# devsupwiz-tasks
Common tasks for the JavaFX based wizard (dev-setup-wizard).

[![Build Status](https://fuin-org.ci.cloudbees.com/job/devsupwiz-tasks/badge/icon)](https://fuin-org.ci.cloudbees.com/job/devsupwiz-tasks/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.fuin/devsupwiz-tasks/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.fuin/devsupwiz-tasks/)
[![LGPLv3 License](http://img.shields.io/badge/license-LGPLv3-blue.svg)](https://www.gnu.org/licenses/lgpl.html)
[![Java Development Kit 1.8](https://img.shields.io/badge/JDK-1.8-green.svg)](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

<a href="https://fuin-org.ci.cloudbees.com/job/devsupwiz-tasks"><img src="http://www.fuin.org/images/Button-Built-on-CB-1.png" width="213" height="72" border="0" alt="Built on CloudBees"/></a>

> :warning: Be aware that this is work in progress - The project has a very low test coverage at the moment :warning:

## set-hostname
Sets a new hostname for the virtual machine (VM).
```xml
<set-hostname task-class="org.fuin.devsupwiz.tasks.hostname.SetHostnameTask" />
```

<a href="https://github.com/fuinorg/devsupwiz-tasks/raw/master/tasks/doc/set-hostname.png" target="_blank"><img src="https://github.com/fuinorg/devsupwiz-tasks/raw/master/tasks/doc/set-hostname.png" width="320" height="335" alt="Set hostname screen"></a>

# create-git-config
Creates and populates the "~/.gitconfig" file.
```xml
<create-git-config task-class="org.fuin.devsupwiz.tasks.gitsetup.CreateGitConfigTask" />
```

<a href="https://github.com/fuinorg/devsupwiz-tasks/raw/master/tasks/doc/create-git-config.png" target="_blank"><img src="https://github.com/fuinorg/devsupwiz-tasks/raw/master/tasks/doc/create-git-config.png" width="320" height="335" alt="Create git config screen"></a>

# setup-git-ssh
Generates a new key pair and adds it to the "~/.ssh/config" file. The public key is also submitted to the git provider (Bitbucket, Github) using a REST API.
```xml
<setup-git-ssh id="1" provider="bitbucket" host="bitbucket.org" 
           task-class="org.fuin.devsupwiz.tasks.gitsetup.SetupGitSshTask" />
```

<a href="https://github.com/fuinorg/devsupwiz-tasks/raw/master/tasks/doc/setup-git-ssh.png" target="_blank"><img src="https://github.com/fuinorg/devsupwiz-tasks/raw/master/tasks/doc/setup-git-ssh.png" width="320" height="335" alt="Setup git ssh"></a>

# git-clone
Clones one or more git repositories. Requires that a valid SSH key is installed (See "setup-git-ssh" task).
```xml
<git-clone id="1" target-dir="~/git" task-class="org.fuin.devsupwiz.tasks.gitsetup.GitCloneTask" >
  <repository>git@bitbucket.org:my_account/my-project.git</repository>
  <repository>git@bitbucket.org:my_account/another-one.git</repository>
  <repository>git@bitbucket.org:my_account/whatever.git</repository>
</git-clone>
```

<a href="https://github.com/fuinorg/devsupwiz-tasks/raw/master/tasks/doc/clone-git-repositories.png" target="_blank"><img src="https://github.com/fuinorg/devsupwiz-tasks/raw/master/tasks/doc/clone-git-repositories.png" width="320" height="335" alt="Setup git ssh"></a>

