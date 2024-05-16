package xml.tasks;

import java.util.List;

/**
 * Отвечает за публикацию вывода задач, результатов операций и прочей информации
 */
public class Printer {
    public static void printWelcome() {
        System.out.println("============ Welcome to your task list! =============");
        System.out.println("===== Type \"help\" to see the list of commands =====");
    }


    public static void printHelp() {
        System.out.println("  Commands:");
        System.out.println("     list                                                                 - display all tasks");
        System.out.println("     list -s \"<status>\"                                                   - display tasks with status <status> (new/in_progress/done");
        System.out.println("     complete \"<id>\"                                                      - mark the task with <id> as completed");
        System.out.println("     start \"<id>\"                                                         - mark the task with <id> as in_progress");
        System.out.println("     new \"<Caption>\", \"<Description>\", \"<Priority>\", \"<Deadline>\"         - create new task");
        System.out.println("     edit \"<id>\" \"<Caption>\", \"<Description>\", \"<Priority>\", \"<Deadline>\" - update task with <id> by appropriate values(no value and no quotes - remain as is)");
        System.out.println("     remove \"<id>\"                                                        - delete task with <id>");
        System.out.println("     exit                                                                 - stop execution");
    }

    public static void printListOutput(List<Task> listCommand) {
        if(listCommand.isEmpty()){
            System.out.println("No tasks found");
            return;
        }

        String pattern = "| %-4s | %-40s | %-80s | %-8s | %s | %-11s | %s      |%n";
        System.out.printf("| %-4s | %-40s | %-80s | %-8s | %-10s | %-11s | %-15s |%n", "id", "Caption", "Description", "Priority", "Deadline", "Status", "Completion Date");
        for(Task task : listCommand){
            String deadLine = task.getDeadline() == null ? "          " : String.format("%tY-%tm-%td", task.getDeadline(), task.getDeadline(), task.getDeadline());
            String completionDate = task.getCompletionDate() == null ? "          " : String.format("%tY-%tm-%td", task.getCompletionDate(), task.getCompletionDate(), task.getCompletionDate());
            String priority = task.getPriority() == null ? "" : task.getPriority().toString();
            System.out.printf(pattern,  task.getId(),
                                task.getCaption(),
                                task.getDescription(),
                                priority,
                                deadLine,
                                task.getStatus(),
                                completionDate);
        }
    }

    public static void printError(Request request) {
        System.out.println(request.getError());
    }

    public static void printCreateResult(Task task) {
        System.out.printf("Task was successfully created with id \"%s\"\n", task.getId());
    }
    public static void printUpdateResult(Task task) {
        System.out.printf("Task with id \"%s\" was successfully %s \n", task.getId(), task.getDeleted() ? "removed" : "updated");
    }

}
