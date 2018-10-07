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

            val document = checkNotNull(FileDocumentManager.getInstance().getDocument(file)) {
                return true
            }

            ReadonlyStatusHandler.ensureDocumentWritable(project, document)

            var i = 1

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
                    {
                        ApplicationManager.getApplication().runWriteAction {
                            document.setText(updatedContent.joinToString("\n"))
                        }
                    },
                    "Index Cucumber Scenarios",
                    "com.github.rmatafonov",
                    document
            )

            return true
        }
    }
}
