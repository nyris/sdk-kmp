# Contributing

## Features or Bugs

For any bug fix request or feature request, please create an [issue](#) for it.

## Gi Branching

Our git branching is inspired by git-flow which has one source branch `main`.

### Branch naming

* Feature branch: `feature/TICKET-ID_description_of_your_ticket`, create this branch when you start
  working on task/story branch.
* Release version name should follow [semantic versioning](https://semver.org/), ex: `1.2.3`
    * 1 represents major version
    * 2 represents minor version (which will includes new features and bug fixes)
    * 3 represents patch version (which will include hotfix)

### Commit message

Our commit message format drive
from [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/).
supported `<type>` are: fix, feat, ci, docs, style, test and project.

Your commit should follow this format:

```
<type>(#TICKET-ID): Description of the commit
[optional body]

ex: feat(#123): Fix crashing app issue
Fixed something which was crashing the app on runtime
```

Please group your commits by context, you can use `git --amend` to amend new commit with previous
commit or interactive rebase `git rebase -t` to squash the same commits or for removing and editing.

### Tags

your tag naming should follow this format:

```
ex: 1.2.3
```

### Rebase:

Our workflow relies on rebasing instead of merging, whenever you feel your branch is outdated just
rebase it against develop.

### Code Style

Please make sure to import the `config/code/style.xml` to your Android Studio or IntelliJ.

### PR Creation

Before creating any PR, please make sure to run on your local machine:

```shell
./gradlew --init-script gradle/init.gradle.kts spotlessApply detekt lint test 
```

When merging your PR, you must make sure to use `squash and merge` and not `create merge commit`.
Before the PR merging you need to make sure the commit message formatting is respected and at
the end of PR merging, your commits must be on the top of the main's commits.

You can also use `rebase and merge`.

### Protected branches

In Github we have `main` branch as protected branch. If you want to push something to it then you
need to create a PR for it.

### Github Issue title

When you define the issue title, we recommend to use this following emojis

- 🐛: Bug
- ⬆️: Version bump
- 🌟: Feature
- 🧪: Tests
- 📦: Publish