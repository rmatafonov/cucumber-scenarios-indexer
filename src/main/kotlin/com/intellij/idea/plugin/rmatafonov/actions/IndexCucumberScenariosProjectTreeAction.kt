package com.intellij.idea.plugin.rmatafonov.actions

import com.intellij.idea.plugin.rmatafonov.service.Indexer
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.ReadonlyStatusHandler
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor

class IndexCucumberScenariosProjectTreeAction : AnAction("Index Cucumber Scenarios") {
    override fun actionPerformed(e: AnActionEvent?) {
        e?.let { event ->
            event.getData(PlatformDataKeys.VIRTUAL_FILE)?.let { virtualFile ->
                VfsUtilCore.visitChildrenRecursively(virtualFile, CucumberFeatureVirtualFileVisitor(event.project))
            }
        }
    }
}

class CucumberFeatureVirtualFileVisitor(project: Project?) : VirtualFileVisitor<CucumberFeatureVirtualFileVisitor>() {
    private val project: Project = checkNotNull(project)

    override fun visitFile(file: VirtualFile): Boolean {
        if (file.extension != "feature") {
            return true
        }

        val document = checkNotNull(FileDocumentManager.getInstance().getDocument(file)) {
            return true
        }

        if (!ReadonlyStatusHandler.ensureDocumentWritable(project, document)) {
            return true
        }

        val updatedContent = Indexer().indexScenarios(document.text.lines()).joinToString("\n")

        CommandProcessor.getInstance().executeCommand(
                project,
                {
                    ApplicationManager.getApplication().runWriteAction {
                        document.setText(updatedContent)
                    }
                },
                "Index Cucumber Scenarios",
                "com.github.rmatafonov",
                document
        )

        return true
    }
}