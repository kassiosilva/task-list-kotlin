import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

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

sealed class Result {
    data class Success(val message: String) : Result()
    data class Error(val exception: Exception) : Result()
}

fun handleResult(result: Result) {
    println(
        when (result) {
            is Result.Error -> result.exception.message
            is Result.Success -> result.message
        }
    )
}

val statusTask = { isCompleted: Boolean -> if (isCompleted) "Concluída" else "Pendente" }

class TaskManager {
    private val tasks = mutableListOf<Task>()

    fun addNewTask(task: Task): Result {
        try {
            require(task.title.isNotBlank()) { "\nO título da tarefa não pode ser vazio." }

            tasks.add(task)

            return Result.Success(message = "\nA tarefa foi criada com SUCESSO!")
        } catch (error: Exception) {
            return Result.Error(exception = error)
        }

    }

    fun listTasks() = tasks.toList()

    fun findTask(id: String) = tasks.find { id == it.id }

    fun updateTask(id: String, isCompleted: Boolean): Result {
        try {
            val taskPosition = tasks.indexOfFirst { id == it.id }

            check(taskPosition != -1) { "\nA tarefa não existe na lista." }

            tasks[taskPosition].isCompleted = isCompleted

            return Result.Success(message = "\nTarefa atualizada com sucesso!")
        } catch (error: Exception) {
            return Result.Error(exception = error)
        }
    }

    fun removeTask(id: String): Result {
        try {
            check(tasks.any { id == it.id }) { "\nNão existe nenhuma tarefa com esse ID." }

            tasks.removeIf { it.id == id }

            return Result.Success("\nA tarefa foi deletada com SUCESSO.")
        } catch (error: Exception) {
            return Result.Error(exception = error)
        }
    }

    fun findTasksByStatus(isCompleted: Boolean): List<Task> {
        return tasks.filter { it.isCompleted == isCompleted }
    }
}

fun Task.toFormattedString(): String {
    val simpleDateFormat =
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

    val dateFormatted = simpleDateFormat.format(this.createdAt)

    return "ID: $id, Título: $title, status: ${statusTask(isCompleted)}, Criada em: $dateFormatted"
}

fun List<Task>.countCompletedTasks(): Int {
    return this.count { it.isCompleted }
}


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
    print("-> ")
    val description: String? = readlnOrNull()

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

        if (taskManager.listTasks().countCompletedTasks() > 0) {
            println("TAREFAS COMPLETAS: ${taskManager.listTasks().countCompletedTasks()}")
        }

        println("LISTA DE TAREFAS:")
        println(
            taskManager.listTasks().joinToString(
                separator = "\n",
                transform = { task -> task.toFormattedString() }
            ).ifEmpty { "Nenhuma task adicionada." }
        )

        println("\nDigite a ação de tarefa: ")
        print("-> ")
        action = readlnOrNull()?.toIntOrNull()

        if (action in 2..5 && taskManager.listTasks().isEmpty()) {
            println("É preciso adicionar uma tarefa primeiro.")
            action = null
        } else {
            when (action) {
                1 -> {
                    val task = actionCreateTask()

                    handleResult(taskManager.addNewTask(task = task))
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

                    taskToBeUpdated?.let { (id, _, isCompleted) ->
                        var status: Int? = null
                        println(
                            "Essa tarefa está com o STATUS: ${statusTask(isCompleted)}. Gostaria de mudar para ${
                                statusTask(
                                    !isCompleted
                                )
                            }?"
                        )

                        while (status == null) {
                            print("Digite 1 para SIM ou 2 para NÃO -> ")
                            status = readlnOrNull()?.toIntOrNull()

                            if (status == null) {
                                println("Opção inválida. Tente novamente.")
                            }
                        }

                        when (status) {
                            1 -> {
                                handleResult(taskManager.updateTask(id = id, isCompleted = !isCompleted))
                            }

                            else -> println("OK! Sua tarefa continua com o mesmo status.")
                        }
                    }

                }

                3 -> {
                    var id: String? = null

                    println("Insira o ID da tarefa para realizar a busca:")
                    while (id.isNullOrBlank()) {
                        print("-> ")
                        id = readlnOrNull()

                        if (id.isNullOrBlank()) {
                            println("O ID inserido é inválido. Insira novamente.")
                            id = null
                        }
                    }

                    val task = taskManager.findTask(id = id)

                    println(
                        if (task != null) {
                            "A tarefa buscada é: ${task.toFormattedString()}"
                        } else {
                            "Não existe nenhuma tarefa com esse ID."
                        }
                    )
                }

                4 -> {
                    var id: String? = null

                    println("Insira o ID da tarefa a ser deletado:")
                    while (id.isNullOrBlank()) {
                        print("-> ")
                        id = readlnOrNull()

                        if (id.isNullOrBlank()) {
                            println("O ID inserido é inválido. Insira novamente.")
                            id = null
                        }
                    }

                    handleResult(taskManager.removeTask(id = id))
                }

                5 -> {
                    var status: Int? = null

                    while (status == null) {
                        print("Digite 1 para CONCLUÍDA ou 2 para PENDENTE -> ")
                        status = readlnOrNull()?.toIntOrNull()

                        if (status == null) {
                            println("Opção inválida. Tente novamente.")
                        }
                    }

                    val tasksFiltered = taskManager.findTasksByStatus(isCompleted = status == 1)

                    if (tasksFiltered.isNotEmpty()) {
                        println("SUAS TAREFAS ${if (status == 1) "CONCLUÍDAS" else "PENDENTES"}:")
                        println(
                            tasksFiltered.joinToString(
                                separator = "\n",
                                transform = { task -> task.toFormattedString() }
                            ))
                    } else {
                        println("Nenhuma tarefa com esse status.")
                    }
                }

                6 -> {
                    println("Obrigado. Volte sempre!")
                }

                else -> println("Opcão escolhida é inválida. Tente novamente.")
            }
        }
    }
}