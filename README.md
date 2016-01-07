
A few useful links:
* [Bug Tracker][issues]
* [IRC][irc]

# License / Use in Modpacks

Ask again when there is something to use

# Releases

Nothing here yet :P

# Contributing

Nightfall/SRails is the central repository for members of Nightfall

everybody else should fork

## Setup

Want to tinker with the mod? Here is how - for IntelliJ IDEA users.

**Important**
- Make sure you have the Gradle plugin enabled in IntelliJ IDEA (File->Settings->Plugins).
- Make sure you have the Scala plugin enabled.

Clone the repository, then in it run  
`gradlew setupDecompWorkspace`  
to setup the workspace, including assets and such, then  
`gradlew idea`  
to create an IntelliJ IDEA project.

Open the project and you will be asked to *import the Gradle project* (check your Event Log if you missed the pop-up). **Do so**. This will configure additionally referenced libraries.

use the customizable gradle wrapper if it doesnt work or get stuck   
or a recent local gradle version (2.9 or 2.10)

In the case you wish to use Eclipse rather than IntelliJ IDEA, the process is mostly the same, except you must run `gradlew eclipse` rather than `gradlew idea`.


## Workflow

we have 3 types of branches

`master` main branch

`feature/<featurename>` features what else ?

`hotfix/<bug-name or issue #>` now do i need to explain this ?

### creating a branch

features and hotfixes are branched out from `master` and eventually merged in again

example branch: `feature/conveyorbelts`

git commandline:
```git
git checkout master
git checkout -b feature/conveyorbelts
```

## Pull Requests

The following are a few quick guidelines on pull requests. That is to say they are not necessarily *rules*, so there may be exceptions and all that. Just try to stick to those points as a baseline.
- Make sure your code is formatted properly.
- Make sure it builds and works.
- Try to keep your changes as minimal as possible. In particular, no whitespace changes in existing files, please.
- Use the [Staging Area](http://gitready.com/beginner/2009/01/18/the-staging-area.html) in your commits to make then more focused   
- [Squash](http://gitready.com/advanced/2009/02/10/squashing-commits-with-rebase.html) your commits into logical units!
- keep it generally clean

before creating a PR to merge a feature / hotfix:
- merge/rebase changes in master into your branch
- test your feature

create a [Pull Request][pr]: https://github.com/Nightfall/SRails/compare/master...feature/feature-name



[irc]: http://webchat.esper.net/?channels=#sapphire
[issues]: https://github.com/Nightfall/SRails/issues?state=open
[pr]: https://github.com/Nightfall/SRails/pulls?state=open
