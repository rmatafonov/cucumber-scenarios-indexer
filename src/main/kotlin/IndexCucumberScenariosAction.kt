import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.ui.Messages
import java.io.File
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class IndexCucumberScenariosAction : AnAction("Index Cucumber Scenarios") {
    override fun actionPerformed(e: AnActionEvent?) {
        e?.let {
            it.getData(PlatformDataKeys.VIRTUAL_FILE)?.let {
                File(it.canonicalPath).walkTopDown()
                        .filter { it.name.endsWith(".feature") }
                        .map {
                            if (rewriteFeature(it)) "${it.name} is changed" else it.name
                        }
                        .joinToString(separator = "\n").also {
                            Messages.showMessageDialog(e.project, it, "Done", Messages.getInformationIcon())
                        }
            }
        }
    }

    private fun rewriteFeature(file: File): Boolean {
        val indexingPath = file.toPath()
        val tmpPath = "$indexingPath.tmp"
        val indexingWriter = PrintWriter(tmpPath)
        var i = 1
        var isChanged = false

        Files.lines(indexingPath).apply {
            this.forEach {
                val replacingLine = when {
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
                indexingWriter.println(replacingLine)
                if (it != replacingLine) {
                    isChanged = true
                }
            }

            this.close()
        }

        indexingWriter.flush()
        indexingWriter.close()

        Files.deleteIfExists(indexingPath)
        Files.move(Paths.get(tmpPath), indexingPath, StandardCopyOption.REPLACE_EXISTING)

        return isChanged
    }


}
