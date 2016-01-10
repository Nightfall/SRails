# SRails [![Jenkins Build Status](http://img.shields.io/jenkins/s/http/jenkins.rx14.co.uk/job/Nightfall/SRails.svg?style=flat-square)](http://jenkins.rx14.co.uk/job/Nightfall/job/SRails/) [![Travis build staus](https://img.shields.io/travis/Nightfall/SRails/master.svg?style=flat-square)](https://travis-ci.org/Nightfall/SRails/branches)


A few useful links:
* [Bug Tracker][issues]
* [IRC][irc]

# Use in Modpacks

Sadly there is no  stable release that is usable in mopacks yet
check back later

# Releases

Nothing here yet :P

# Contributing


**Important**
- Make sure you **never merge** yourself unless really needed
- [Merging vs. Rebasing](https://www.atlassian.com/git/tutorials/merging-vs-rebasing)

Nightfall/SRails is the central repository for @Nightfall/contributors
Everybody else should [fork from master](https://github.com/Nightfall/SRails#fork-destination-box)

## Setup

### git setup

`git config --global --bool pull.rebase true`

**Important**
- Make sure you know how to **use [rebase][rebase] correctly**


### IDEA setup

**Important**
- Make sure you have the Gradle plugin enabled in IntelliJ IDEA (File->Settings->Plugins).
- Make sure you have the Scala plugin enabled.

Clone the repository, then in it run  
`gradlew setupDecompWorkspace`  
to setup the workspace, including assets and such, then  
`gradlew idea`  
to create an IntelliJ IDEA project.

Open the project and you will be asked to *import the Gradle project* (check your Event Log if you missed the pop-up). **Do so**. This will configure additionally referenced libraries.

Use the customizable gradle wrapper if it doesnt work or get stuck
or a recent local gradle version (2.9 or 2.10)

In the case you wish to use Eclipse rather than IntelliJ IDEA, the process is mostly the same, except you must run `gradlew eclipse` rather than `gradlew idea`.


## Workflow

we have 3 types of branches

|           branch             |             info                  |
|-------------------------------|----------------------------------|
| master                        | Protected release branch         |
| feature/<feature-name>         | Features and wip stuff goes here |
| hotfix/<bug-name or issue #>  | Hopefully we will not need those |


## Pull Requests

before rebasing:
- [Squash](http://gitready.com/advanced/2009/02/10/squashing-commits-with-rebase.html) your commits into logical units!
- [rebase](http://gitready.com/intermediate/2009/01/31/intro-to-rebase.html) changes in master into your branch (instead of merging)
- make sure you followed the guidelines


The following are a few quick guidelines on pull requests. That is to say they are not necessarily *rules*, so there may be exceptions and all that. Just try to stick to those points as a baseline.
- Make sure your code is formatted properly.
- Make sure it builds and works.
- Try to keep your changes as minimal as possible. In particular, no whitespace changes in existing files, please.
- Use the [Staging Area](http://gitready.com/beginner/2009/01/18/the-staging-area.html) when committing
- dont mess up :P

create a PR: https://github.com/Nightfall/SRails/compare/feature-name

[irc]: http://webchat.esper.net/?channels=#sapphire
[issues]: https://github.com/Nightfall/SRails/issues?state=open
[pr]: https://github.com/Nightfall/SRails/pulls?state=open


[rebase]: https://www.atlassian.com/git/tutorials/merging-vs-rebasing