package com.suhininalex.clones.core.utils

import java.util.*

fun <T> T.depthFirstTraverse(children: (T) -> Sequence<T>): Sequence<T> =
        sequenceOf(this) + children(this).flatMap { it.depthFirstTraverse(children) }

fun <T> T.depthFirstTraverse(recursionFilter: (T)-> Boolean, children: (T) -> Sequence<T>) =
        this.depthFirstTraverse { if (recursionFilter(it)) children(it) else emptySequence() }

fun <T> T.leafTraverse(isLeaf: (T)-> Boolean, children: (T) -> Sequence<T>) =
        this.depthFirstTraverse ({ ! isLeaf(it) }, children).filter { isLeaf(it) }

fun <T> times(times: Int, provider: ()-> Sequence<T>): Sequence<T> =
        (1..times).asSequence().flatMap { provider() }

inline fun <E> MutableList<E>.addIf(element: E, condition:(element: E)->Boolean){
    if (condition(element)) add(element)
}

fun <T> iterate(f:()->T?) = object : Iterator<T>{
    var next :T? = f()
    override fun hasNext() = next!=null
    override fun next():T {
        val result = next ?: throw NoSuchElementException()
        next = f()
        return result
    }
}