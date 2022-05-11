# Step by Step
> Challenge for today : refactor the `TripService` class to ensure Clean Code / SOLID principles

Legacy code golden rules :
`You cannot change any existing code if not covered by tests.`

The only exception is if we need to change the code to add unit tests, but in this case, just automated refactorings (via IDE) are allowed.

![Legacy code refactoring](img/legacy-code-refactoring.png)

## Tips
- Start testing from shortest to deepest branch
- Start refactoring from deepest to shortest branch

![Working with Legacy Code Tips](img/tips.png)

## My "solution"
1. [Cover the production code](steps/1.cover-the-code.md) (prepare the refactoring)
2. [Refactor the production code](steps/2.refactoring.md)
3. [Surface / other refactorings](steps/3.other-refactorings.md)

## What did we use / learn ?
- Seams
- Automated refactoring via our IDE
  - Extract Method
  - Change Signature
  - Rename
- Use `Higher Order Functions` to avoid duplication
- Use code coverage as a driver
- Test Data Builder
- Feature Envy
- Intro to TDD

Sources : 
- [Nicolas Carlo - understand legacy code](https://understandlegacycode.com/blog/key-points-of-working-effectively-with-legacy-code/#identify-seams-to-break-your-code-dependencies)
- [Sandro Mancuso - Testing legacy with Hard-wired dependencies](https://www.codurance.com/publications/2011/07/16/testing-legacy-hard-wired-dependencies)
- [Micheal Feathers - Working Effectively with Legacy Code](https://www.oreilly.com/library/view/working-effectively-with/0131177052/)

<a href="https://youtu.be/LSqbXorkyfQ" rel="Sandro's video">![Sandro's video](img/video.png)</a>