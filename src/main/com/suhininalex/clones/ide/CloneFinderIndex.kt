package com.suhininalex.clones.ide;

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.GeneratedSourcesFilter
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.EverythingGlobalScope
import com.intellij.util.indexing.*
import com.intellij.util.io.EnumeratorIntegerDescriptor
import com.intellij.util.io.KeyDescriptor
import com.suhininalex.clones.core.CloneIndexer
import com.suhininalex.clones.core.languagescope.LanguageIndexedPsiManager
import com.suhininalex.clones.core.utils.Logger
import com.suhininalex.clones.core.utils.isSourceFile
import com.suhininalex.clones.core.utils.isTestFile
import com.suhininalex.clones.ide.configuration.PluginSettings

class CloneFinderIndex : ScalarIndexExtension<Int>(){

    companion object {
        val NAME: ID<Int, Void> = ID.create("CloneFinderIndexer")

        fun enshureUpToDate(project: Project){
            FileBasedIndex.getInstance().ensureUpToDate(NAME, project, EverythingGlobalScope(project))
        }

        fun rebuild(){
            LanguageIndexedPsiManager.update()
            CloneIndexer.clear()
            FileBasedIndex.getInstance().requestRebuild(NAME)
        }
    }

    override fun getVersion(): Int = 1706281

    override fun dependsOnFileContent(): Boolean = true

    override fun getKeyDescriptor(): KeyDescriptor<Int> = EnumeratorIntegerDescriptor.INSTANCE

    override fun getName(): ID<Int, Void> = NAME

    override fun getIndexer(): DataIndexer<Int, Void, FileContent> = CloneFinderIndexer()

    override fun getInputFilter(): FileBasedIndex.InputFilter = FileBasedIndex.InputFilter { virtualFile ->
         PluginSettings.enabledForProject && LanguageIndexedPsiManager.isFileTypeSupported(virtualFile.fileType)
    }

}

class CloneFinderIndexer() : DataIndexer<Int, Void, FileContent> {

    override fun map(fileContent: FileContent): Map<Int, Void> {
        val psiFile = PsiManager.getInstance(fileContent.project).findFile(fileContent.file)!!
        if (psiFile.isSourceFile() && !psiFile.isDisabledTest() && !psiFile.isGenerated()){
            Logger.log("[Indexer] Add file suffixes ${psiFile.virtualFile}")
            CloneIndexer.removeFile(fileContent.file)
            CloneIndexer.addFile(psiFile)
        }
        return emptyMap()
    }

    fun PsiFile.isGenerated(): Boolean{
        return GeneratedSourcesFilter.isGeneratedSourceByAnyFilter(virtualFile, project)
    }

    fun PsiFile.isDisabledTest(): Boolean {
        return PluginSettings.disableTestFolder && isTestFile()
    }
}