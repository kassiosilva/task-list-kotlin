import java.util.Date
import java.util.UUID

/**
 * 1. Criar classe de dados ✅
 * 2. Utilize companion object para gerar IDs únicos automaticamente para cada Task ✅
 *
 * **/


data class Task(
    val id: String = generateId(),
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean,
    var createdAt: Date = Date()
) {
    companion object {
        private fun generateId() = UUID.randomUUID().toString()
    }
}


fun main() {
    println("Hello World!")

    println(Task(title = "Teste 1", isCompleted = false))
    println(Task(title = "Teste 2", isCompleted = true))

}