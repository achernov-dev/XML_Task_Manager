package xml.tasks;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Корневой класс, содержащий список задач. Также содержит функции по поиску определенной задачи, вывод задач определенном статусе и операции по добавлению/изменению в список
 */
@XmlRootElement(name="ToDoList")
@XmlAccessorType(XmlAccessType.FIELD)
public class TaskList {
    @XmlElement(name="task")
    public List<Task> taskList = new ArrayList<>();

    public Task getTaskById(String taskId) {
        Task searchingTask = this.taskList.stream().filter(task -> task.getId().equals(taskId) && !task.getDeleted()).findFirst().orElse(null);
        return searchingTask != null ? searchingTask.clone() : null;
    }

    public List<Task> getListCommand(Request command) {
        return this.taskList.stream().filter(t -> !t.getDeleted() && (command.getFilter() == null || t.getStatus().equals(command.getFilter()))).sorted().toList();
    }

    public Task addTask(Request request) throws JAXBException {
        Task taskToCreate = request.getTask();
        taskToCreate.setId("id" + (this.taskList.size() + 1));
        this.taskList.add(taskToCreate);
        Main.rebuildXML(this);
        return taskToCreate;
    }

    public Task editTask(Request request) throws JAXBException {
        Task taskToUpdate = request.getTask();
        int taskIndex = Utils.getTaskIndex(taskList, taskToUpdate.getId());
        taskList.set(taskIndex, taskToUpdate);
        Main.rebuildXML(this);
        return taskToUpdate;
    }
}
