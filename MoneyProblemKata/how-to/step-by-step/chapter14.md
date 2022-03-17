### Chapter 14 - Retrospective
`Retrospectives can be a powerful catalyst for change. A major transformation can start
from a single retrospective. —Esther Derby and Diana Larsen, Agile Retrospectives`

* Let’s take some time to recap what we did and reflect upon how we did it.
* We’ll frame our retrospective along these dimensions:
    * Profile : refers to the shape of the code
    * Purpose : includes what the code does and—more importantly—does not do
    * Process :
        * How we got to where we are
        * What other ways might have been possible
        * Implications of taking a specific path

#### Profile
* Include both :
    * Subjective aspects (readability, obviousness)
    * Their objective manifestations : namely complexity, coupling, and succinctness
* **Cyclomatic Complexity** : a measure of the degree of branching and looping in code
* **Coupling** : a measure of the interdependency of a block of code (e.g., a class or method) on other blocks of code
    * The two kinds of coupling are afferent and efferent coupling :
        * _Afferent_  : number of other components that depend on a given component
        * _Efferent_ : number of other components that a given component depends on
    * A measure of the stability of the code is the balance between afferent and efferent coupling
```text
Instability = efferent / efferent + afferent
```
* **Succinctness** :
    * Lines of code is a dangerous metric—especially across different languages
    * Compare lines of test code to the lines of production code in the same language

#### Purpose
* **Cohesion** : measure of the relatedness of the code in a module
    * High cohesion reflects that the code in a module—method, class, or package—represents a single, unified concept
* **Completeness** : Does our code do everything it should?
    * How complete are our tests, though?
    * Could we gain more confidence by writing additional tests?
        * Consider these cases: Overflow, Underflow, Division by zero

#### Process
* Assess the process of how we got to the final version of our code

* Reflect on your own code
* What could be said about it regarding the 3P ?
* What surprised you during this workshop ?
* What have been your discoveries ?

![Thanks](../../img/thanks.png)

`Thanks for having followed it and thanks Saleem for this great book`