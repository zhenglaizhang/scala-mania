import net.zhenglai.ds.{Branch, Leaf}

val tree = Branch(Branch(Leaf(1), Leaf(2)), Branch(Branch(Leaf(10), Leaf(8)), Leaf(3)))

tree.size

tree.leafSize

tree.branchSize

tree.depth

tree.maxValue

tree.map(_ * 2)
tree.flatMap(x => Branch(Leaf(2 * x), Leaf(2 + x)))
