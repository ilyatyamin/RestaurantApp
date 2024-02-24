package restaurant.order

import java.lang.RuntimeException

class Review(st: Int, com: String) {
    init {
        if (st !in 1..5) {
            throw RuntimeException("Stars need to be from 1 to 5")
        }
        var stars = st
        var comment = com
    }

}