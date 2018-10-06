import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.*
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiPlainTextFile
import java.io.BufferedReader
import java.io.InputStreamReader

class IndexCucumberScenariosAction : AnAction("Index Cucumber Scenarios") {
    override fun actionPerformed(e: AnActionEvent?) {
        e?.let { event ->
            event.getData(PlatformDataKeys.VIRTUAL_FILE)?.let { virtualFile ->
                VfsUtilCore.visitChildrenRecursively(virtualFile, MyVirtualFileVisitor(event.project))
            }
        }
    }

    private class MyVirtualFileVisitor(project: Project?) : VirtualFileVisitor<MyVirtualFileVisitor>() {
        val project: Project = checkNotNull(project)

        override fun visitFile(file: VirtualFile): Boolean {
            if (!file.name.endsWith(".feature")) {
                return true
            }

            var i = 1

            val document = checkNotNull(FileDocumentManager.getInstance().getDocument(file))

            val updatedContent = document.text.lines().map {
                return@map when {
                    it.trim().matches(Regex("^Scenario: \\d+.*")) -> {
                        it.replace(Regex("Scenario: \\d+"), String.format("Scenario: %02d", i++))
                    }
                    it.trim().matches(Regex("^Scenario Outline: \\d+.*")) -> {
                        it.replace(Regex("Scenario Outline: \\d+"), String.format("Scenario Outline: %02d", i++))
                    }
                    it.trim().startsWith("Scenario:") -> {
                        it.replace("Scenario:", String.format("Scenario: %02d -", i++))
                    }
                    it.trim().startsWith("Scenario Outline:") -> {
                        it.replace("Scenario Outline:", String.format("Scenario Outline: %02d -", i++))
                    }
                    else -> it
                }
            }

            CommandProcessor.getInstance().executeCommand(
                    project,
                    { document.setText(updatedContent.joinToString("\n")) },
                    "Index Cucumber Scenarios",
                    123,
                    document
            )

            return true
        }
    }
}
