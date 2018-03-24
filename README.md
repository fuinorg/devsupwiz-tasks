# devsupwiz-tasks
Common tasks for the JavaFX based wizard ([dev-setup-wizard](https://github.com/fuinorg/dev-setup-wizard)).

[![Build Status](https://fuin-org.ci.cloudbees.com/job/devsupwiz-tasks/badge/icon)](https://fuin-org.ci.cloudbees.com/job/devsupwiz-tasks/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.fuin.devsupwiz/devsupwiz-tasks/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.fuin.devsupwiz/devsupwiz-tasks/)
[![LGPLv3 License](http://img.shields.io/badge/license-LGPLv3-blue.svg)](https://www.gnu.org/licenses/lgpl.html)
[![Java Development Kit 1.8](https://img.shields.io/badge/JDK-1.8-green.svg)](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

<a href="https://fuin-org.ci.cloudbees.com/job/devsupwiz-tasks"><img src="http://www.fuin.org/images/Button-Built-on-CB-1.png" width="213" height="72" border="0" alt="Built on CloudBees"/></a>

> :warning: Be aware that this is work in progress - The project has a very low test coverage at the moment :warning:

## set-personal-data
Sets personal data like name and email address.
```xml
<set-personal-data />
```

<a href="https://github.com/fuinorg/devsupwiz-tasks/raw/master/doc/set-personal-data.png" target="_blank"><img src="https://github.com/fuinorg/devsupwiz-tasks/raw/master/doc/set-personal-data.png" width="320" height="335" alt="Sets personal data screen"></a>

## set-hostname
Sets a new hostname for the virtual machine (VM).
```xml
<set-hostname />
```

<a href="https://github.com/fuinorg/devsupwiz-tasks/raw/master/doc/set-hostname.png" target="_blank"><img src="https://github.com/fuinorg/devsupwiz-tasks/raw/master/doc/set-hostname.png" width="320" height="335" alt="Set hostname screen"></a>

# create-git-config
Creates and populates the "~/.gitconfig" file.
```xml
<create-git-config />
```

<a href="https://github.com/fuinorg/devsupwiz-tasks/raw/master/doc/create-git-config.png" target="_blank"><img src="https://github.com/fuinorg/devsupwiz-tasks/raw/master/doc/create-git-config.png" width="320" height="335" alt="Create git config screen"></a>

# generate-ssh-key
Generates a new key pair and adds it to the "~/.ssh/config" file. 
```xml
<generate-ssh-key id="1" host="bitbucket.org" />
```

<a href="https://github.com/fuinorg/devsupwiz-tasks/raw/master/doc/generate-ssh-key.png" target="_blank"><img src="https://github.com/fuinorg/devsupwiz-tasks/raw/master/doc/generate-ssh-key.png" width="320" height="335" alt="Create ssh keys"></a>

# display-ssh-key
Displays a newly generated public key. 
```xml
<display-ssh-key id="1" ref="generate-ssh-key[1]" />
```

<a href="https://github.com/fuinorg/devsupwiz-tasks/raw/master/doc/display-ssh-key.png" target="_blank"><img src="https://github.com/fuinorg/devsupwiz-tasks/raw/master/doc/display-ssh-key.png" width="320" height="335" alt="Display ssh public key"></a>

# git-clone
Clones one or more git repositories. Requires that a valid SSH key is created and installed in your repository (See "generate-ssh-key" task).
```xml
<git-clone id="1" target-dir="~/git">
  <repository>git@bitbucket.org:my_account/my-project.git</repository>
  <repository>git@bitbucket.org:my_account/another-one.git</repository>
  <repository>git@bitbucket.org:my_account/whatever.git</repository>
</git-clone>
```

<a href="https://github.com/fuinorg/devsupwiz-tasks/raw/master/doc/clone-git-repositories.png" target="_blank"><img src="https://github.com/fuinorg/devsupwiz-tasks/raw/master/doc/clone-git-repositories.png" width="320" height="335" alt="Setup git ssh"></a>


# create-maven-settings
Generates a new ".m2/settings.xml" file with a user name and password to allow access to a private Maven repository.
```xml
<create-maven-settings id="1" template="~/git/my-project/config/settings.xml" />
```

<a href="https://github.com/fuinorg/devsupwiz-tasks/raw/master/doc/create-maven-settings.png" target="_blank"><img src="https://github.com/fuinorg/devsupwiz-tasks/raw/master/doc/create-maven-settings.png" width="320" height="335" alt="Create Maven settings"></a>



* * *

## Snapshots

Snapshots can be found on the [OSS Sonatype Snapshots Repository](http://oss.sonatype.org/content/repositories/snapshots/org/fuin "Snapshot Repository"). 

Add the following to your .m2/settings.xml to enable snapshots in your Maven build:

```xml
<repository>
    <id>sonatype.oss.snapshots</id>
    <name>Sonatype OSS Snapshot Repository</name>
    <url>http://oss.sonatype.org/content/repositories/snapshots</url>
    <releases>
        <enabled>false</enabled>
    </releases>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
</repository>
```

 