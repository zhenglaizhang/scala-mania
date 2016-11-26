/*
A particular definition or a theorem is called abstract, when it relies only on category theoric notions, rather than some additional information about the objects and arrows. The advantage of an abstract notion is that it applies in any category immediately.

Definition 1.3 In any category C, an arrow f: A => B is called an isomorphism, if there is an arrow g: B => A in C such that:

g ∘ f = 1A and f ∘ g = 1B.

Awodey names the above definition to be an abstract notion as it does make use only of category theoric notion.

Extending this to Scalaz, learning the nature of an abtract typeclass has the advantage of it applying in all concrete data structures that support it.
 */ 