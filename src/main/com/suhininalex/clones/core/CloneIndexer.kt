package com.suhininalex.clones.core

import com.suhininalex.clones.core.postprocessing.filterSubClassClones
import com.suhininalex.clones.core.structures.Token
import com.suhininalex.clones.core.structures.TreeCloneClass
import com.suhininalex.clones.core.utils.addIf
import com.suhininalex.clones.core.utils.riseTraverser
import com.suhininalex.suffixtree.Node
import com.suhininalex.suffixtree.SuffixTree
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.concurrent.read

class CloneIndexer {

    var tree = SuffixTree<Token>()
    internal val rwLock = ReentrantReadWriteLock()

    fun clear() {
        tree = SuffixTree()
    }

    fun getAllSequenceCloneClasses(id: Long, minCloneLength: Int): List<TreeCloneClass> = rwLock.read {
        return tree
                .getAllSequenceClasses(id, minCloneLength)
                .toList()
                .filterSubClassClones()
    }

    fun getAllCloneClasses(minCloneLength: Int): List<TreeCloneClass>  = rwLock.read {
        tree.getAllCloneClasses(minCloneLength)
    }

}

fun Node.visitChildren(visit: (Node) -> Unit) {
    visit(this)
    this.edges.mapNotNull { it.terminal }.forEach { it.visitChildren(visit) }
}

fun SuffixTree<Token>.getAllCloneClasses(minTokenLength: Int): List<TreeCloneClass> {
    val clones = ArrayList<TreeCloneClass>()
    root.visitChildren {
        val cloneClass = TreeCloneClass(it)
        if (cloneClass.length > minTokenLength) {
            clones.add(cloneClass)
        }
    }
    return clones
}

fun SuffixTree<Token>.getAllSequenceClasses(id: Long, minTokenLength: Int): Sequence<TreeCloneClass>  {
    val classes = LinkedList<TreeCloneClass>()
    val visitedNodes = HashSet<Node>()
    for (branchNode in this.getAllLastSequenceNodes(id)) {
        for (currentNode in branchNode.riseTraverser()){
            if (visitedNodes.contains(currentNode)) break
            visitedNodes.add(currentNode)
            classes.addIf(TreeCloneClass(currentNode)) {it.length > minTokenLength}
        }
    }
    return classes.asSequence()
}