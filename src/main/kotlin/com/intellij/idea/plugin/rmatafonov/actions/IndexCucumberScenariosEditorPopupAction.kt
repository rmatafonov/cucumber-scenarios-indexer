package com.intellij.idea.plugin.rmatafonov.actions

import com.intellij.idea.plugin.rmatafonov.service.Indexer
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.command.WriteCommandAction



class IndexCucumberScenariosEditorPopupAction : AnAction("Index Cucumber Scenarios") {
    override fun actionPerformed(event: AnActionEvent?) {
        event?.let { e ->
            val editor = e.getRequiredData(CommonDataKeys.EDITOR)
            val project = e.getRequiredData(CommonDataKeys.PROJECT)

            with(editor.document) {
                Indexer().indexScenarios(text.lines()).joinToString("\n").let { updatedContent ->
                    WriteCommandAction.runWriteCommandAction(project) { setText(updatedContent) }
                }
            }
        }
    }

    override fun update(e: AnActionEvent?) {
        e?.let { event ->
            val isWritable = event.getData(CommonDataKeys.EDITOR)?.document?.isWritable == true
            val isFeature = event.getData(PlatformDataKeys.VIRTUAL_FILE)?.extension == "feature"
            e.presentation.isEnabledAndVisible = isWritable && isFeature
        }
    }
}
