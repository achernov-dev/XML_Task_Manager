package xml.tasks;

import java.util.Arrays;
import java.util.List;

/**
 * Отвечает за хранение результатов обработки.
 * Содержит либо сообщение об ошибке, либо enum с необходимой операцией и соответствующими данными(объект задачи или запрашиваемый фильтр
 */
public class Request {
    private Command name;
    private String filter;
    private Task task;
    private String error;

    public Request(Command name) {
        this.name = name;
    }
    public Request(String error) {
        this.error = error;
    }
    public Request(Command name, String filter) {
        this.name = name;
        this.filter = filter;
    }

    public Request(Command name, Task task) {
        this.name = name;
        this.task = task;
    }
    enum Command {
        HELP,
        LIST,
        NEW,
        EDIT
    }

    /**
     *
     * @param command - отредактированная команда ввода (с удаленными лишними пробелами)
     * @param taskList - объект класса TaskList, в котором хранятся данные из XML-файла
     * @return объект класса Request, в котором хранятся результаты обработки.
     */
    public static Request generateRequest(String command, TaskList taskList){
        String mainCommand = Utils.getMainCommand(command);
        switch (mainCommand) {
            case "help" -> {
                return new Request(Command.HELP);
            }
            case "list" -> {
                List<String> argumentList = Arrays.asList(command.split(" "));
                if (argumentList.size() == 1) {
                    return new Request(Command.LIST);
                } else if (argumentList.size() == 3 && argumentList.get(1).equalsIgnoreCase("-s")) {
                    String status = argumentList.get(2);
                    if (!Task.STATUSES.contains(status)) {
                        return new Request(String.format("Status \"%s\" is not recognized", status));
                    }
                    return new Request(Command.LIST, status);
                } else {
                    return new Request("Command is not recognized");
                }
            }
            case "new" -> {
                return Task.generateTask(command);
            }
            case "edit" -> {
                String taskId = Utils.getTaskId(command);
                Task taskToUpdate = taskList.getTaskById(taskId);
                return Task.updateTask(command, taskToUpdate);
            }
            case "complete" -> {
                String taskId = Utils.getTaskId(command);
                Task taskToComplete = taskList.getTaskById(taskId);
                return Task.completeTask(taskToComplete);
            }
            case "start" -> {
                String taskId = Utils.getTaskId(command);
                Task taskToStart = taskList.getTaskById(taskId);
                return Task.startTask(taskToStart);
            }
            case "remove" -> {
                String taskId = Utils.getTaskId(command);
                Task taskToDelete = taskList.getTaskById(taskId);
                return Task.removeTask(taskToDelete);
            }
        }
        return new Request("Unknown request");
    }

    public Command getName() {
        return name;
    }
    public String getFilter() {
        return filter;
    }
    public Task getTask() {
        return task;
    }
    public void setTask(Task task) {
        this.task = task;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
}
