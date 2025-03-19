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
    var isCompleted: Boolean = false,
    val description: String? = null,
    var createdAt: Date = Date()
) {
    companion object {
        private fun generateId() = UUID.randomUUID().toString()
    }
}

class TaskManager {
    private val tasks = mutableListOf<Task>(
        Task(title = "Teste 1"),
        Task(title = "Teste 2"),
        Task(title = "Teste 3"),
        Task(title = "Teste 4"),
        Task(title = "Teste 5"),
        Task(title = "Teste 6"),
    )

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

val statusTask = { isCompleted: Boolean -> if (isCompleted) "Concluída" else "Pendente" }

fun actionCreateTask(): Task {
    var title: String? = null
    println("Insira o TÍTULO da tarefa:")
    while (title.isNullOrBlank()) {
        print("-> ")
        title = readlnOrNull()

        if (title.isNullOrBlank()) {
            println("O nome inserido é inválido. Tente novamente.")
        }
    }

    println("Insira a DESCRIÇÃO da tarefa:")
    val description: String? = readlnOrNull()
    print("-> ")

    return Task(title = title, description = description)
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

        println("SUAS TAREFAS:")
        println(
            taskManager.listTasks().joinToString(
                separator = "\n",
                transform = { (id, title, isCompleted) ->
                    "ID: $id, TÍTULO: $title, STATUS: ${statusTask(isCompleted)}"
                }
            ).ifEmpty { "Nenhuma task adicionada." }
        )

        print("-> ")
        action = readlnOrNull()?.toIntOrNull()

        when (action) {
            1 -> {
                val task = actionCreateTask()

                taskManager.addNewTask(task = task)

                println("A task foi criada com SUCESSO!")
            }

            2 -> {
                var id: String? = null

                println("Insira o ID da tarefa para atualizar seu status:")
                while (id.isNullOrBlank()) {
                    print("-> ")
                    id = readlnOrNull()

                    if (id.isNullOrBlank() || taskManager.findTask(id = id) == null) {
                        println("O ID inserido é inválido. Insira novamente.")
                        id = null
                    }
                }

                val taskToBeUpdated = taskManager.findTask(id = id)

                taskToBeUpdated?.let { (_, _, isCompleted) ->
                    var status: Int? = null
                    println(
                        "Essa tarefa está com o STATUS: ${statusTask(isCompleted)}. Gostaria de mudar para ${
                            statusTask(
                                !isCompleted
                            )
                        }?"
                    )

                    while(status == null) {
                        print("Digite 1 para SIM ou 2 para NÃO -> ")
                        status = readlnOrNull()?.toIntOrNull()

                        if (status == null) {
                            println("Opção inválida. Tente novamente.")
                        }
                    }

                    when (status) {
                        1 -> {
                            if (taskManager.updateTask(id = id, isCompleted = !isCompleted)) {
                                println("Tarefa atualizada com sucesso!")
                            } else {
                                println("Não foi possível atualizar sua tarefa.")
                            }
                        }
                        else -> println("OK! Sua tarefa continua com o mesmo status.")
                    }
                }

            }

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