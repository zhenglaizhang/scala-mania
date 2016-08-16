

val list = 1 :: 2 :: 3 :: 4 :: Nil

val list2 = 1 +: 2 +: 3 +: 4 +: Nil

List(1, 2, 3, 4)

List[Number](1, 2.0, 343d, 0x1)

List.range(1, 20, 3)

List.fill(3)("foo")

List.tabulate(5)(n +> n * n)