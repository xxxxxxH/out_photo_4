package com.abc.photo.utils

import java.util.*

class FilterList {
    var names: MutableList<String> = LinkedList()
    var filters: MutableList<FilterType> =
        LinkedList<FilterType>()

    fun addFilter(
        name: String,
        filter: FilterType
    ) {
        names.add(name)
        filters.add(filter)
    }
}