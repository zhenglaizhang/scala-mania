/*
1. The ‘determination’ (or ‘extension’) problem
Given f and h as shown, what are all g, if any, for which h = g ∘ f?

A -f-> B
A -h-> C
B -g-> C ?


2. The ‘choice’ (or ‘lifting’) problem
Given g and h as shown, what are all g, if any, for which h = g ∘ f?
A -f-> B ?
A -h-> C
B -g-> C



These two notions are analogous to division problem.

Retractions and sections

Definitions: If f: A => B:

  a retraction for f is an arrow r: B => A for which r ∘ f = 1A
  a section for f is an arrow s: B => A for which f ∘ s = 1B

retraction:
A -f-> B
B -r-> A ?



section:
A -s-> B ?
B -f-> A





Surjective(漫射)
If an arrow f: A => B satisfies the property ‘for any y: T => B there exists an x: T => A such that f ∘ x = y‘, it is often said to be ‘surjective for arrows from T.’
*/