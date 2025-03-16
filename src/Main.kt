import java.util.Date
import java.util.UUID

/**
 * 1. Criar classe de dados ✅
 * 2. Utilize companion object para gerar IDs únicos automaticamente para cada Task ✅
 * 3. Implemente uma classe `TaskManager` com: ✅
 *     - Uma lista de tarefas (`tasks`) inicializada como uma lista vazia. (OK)
 *     - Métodos para:
 *         - **Adicionar uma nova tarefa.** (OK)
 *         - **Listar todas as tarefas (destruturando `title` e `isCompleted`).** (Ok)
 *         - **Buscar uma tarefa por ID.** (Ok)
 *         - **Atualizar o status (`isCompleted`) de uma tarefa específica.**(Ok)
 *         - **Excluir uma tarefa pelo ID.**
 *         - **Filtrar tarefas concluídas ou pendentes usando `filter`.** (Ok)
 * 4. Use funções de validação como `require` para garantir que:
 *     - O título de uma tarefa não esteja vazio.
 *     - A tarefa existe antes de realizar operações como atualizar ou excluir.
 * **/


data class Task(
    val id: String = generateId(),
    val title: String,
    var isCompleted: Boolean,
    val description: String? = null,
    var createdAt: Date = Date()
) {
    companion object {
        private fun generateId() = UUID.randomUUID().toString()
    }
}


class TaskManager {
    private val tasks = mutableListOf<Task>()

    fun addNewTask(task: Task) = tasks.add(task)

    fun listTasks() = tasks.toList()

    fun findTask(id: String) = tasks.find { id == it.id }

    fun updateTask(id: String, isCompleted: Boolean): Boolean {
        val taskPosition = tasks.indexOfFirst { id == it.id }

        if (taskPosition == -1) {
            return false
        }

        tasks[taskPosition].isCompleted = isCompleted

        return true
    }

    fun removeTask(id: String) = tasks.removeIf { it.id == id }

    fun findTasksByStatus(isCompleted: Boolean): List<Task> {
        return tasks.filter { it.isCompleted == isCompleted }
    }
}


fun main() {
    val taskManager = TaskManager()
    var action: Int? = null

    while (action != 6) {
        println(
            """
                
            +--+--+--+--+--+--+--+--+--+--+
            |          TaskList           |
            +--+--+--+--+--+--+--+--+--+--+
            | 1 - Adicionar               |
            | 2 - Atualizar status        |
            | 3 - Buscar                  |
            | 4 - Deletar                 |
            | 5 - Buscar por status       |
            | 6 - Sair                    |
            +--+--+--+--+--+--+--+--+--+--+
        
            """.trimIndent()
        )

        println("Suas tarefas:")
        println(
            taskManager.listTasks().joinToString(
                separator = "\n",
                transform = { (id, title, isCompleted) ->
                    "Título: $title, status: ${if (isCompleted) "Concluído" else "Pendente"}, id: $id"
                }
            ).ifEmpty { "Nenhuma task adicionada." }
        )

        print("-> ")
        action = readlnOrNull()?.toIntOrNull()

        when (action) {
            1 -> {}
            2 -> {}
            3 -> {}
            4 -> {}
            5 -> {}
            6 -> {
                println("Obrigado. Volte sempre!")
            }
            else -> println("Opcão escolhida é inválida. Tente novamente.")
        }
    }
}