/*
SeqLike is just an implementation layer for Seq that allows you to specify return types. There are extremely few things that are SeqLike but not Seq, and those are arguably an error. So you can feel comfortable not worrying about the -Likes. (If you want to build new collections of the type you are given and keep the types straight, use CanBuildFrom instead.)

So then the question is whether to use GenSeq or Seq. The problem with GenSeq is that the processing might be done in parallel, which means you have to avoid using any operation where that could violate your expectations (e.g. summing with a foreach). Furthermore, the general consensus seems to be that the GenX part of the collections hierarchy overcomplicates the collections and makes it more difficult to incorporate alternative choices of parallel collections. So my recommendation would be Seq unless you are pretty sure that you have use-cases where you'd like parallel processing. If you simply don't care, Seq is simpler to reason about for you and for users of the function.





The Scala collection library avoids code duplication and achieves the "same-result-type" principle by using generic builders and traversals over collections in so-called implementation traits. These traits are named with a Like suffix; for instance, IndexedSeqLike is the implementation trait for IndexedSeq, and similarly,  TraversableLike is the implementation trait for Traversable. Collection classes such as Traversable or IndexedSeq inherit all their concrete method implementations from these traits. Implementation traits have two type parameters instead of one for normal collections. They parameterize not only over the collection's element type, but also over the collection's representation type, i.e., the type of the underlying collection, such as Seq[I] or List[T]...

 */