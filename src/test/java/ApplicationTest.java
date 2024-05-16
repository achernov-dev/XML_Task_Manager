import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xml.tasks.Main;
import xml.tasks.Task;
import xml.tasks.TaskList;

public class ApplicationTest {
    TaskList taskList;
    @Test
    void testTaskCreation() throws JAXBException {
        taskList  = new TaskList();
        Main.readAndReact("new testRun1, testDescr1, 1, 2022-12-13", taskList);
        Main.readAndReact("new testRun2, testDescr2, 1, 2022-12-14", taskList);
        Main.readAndReact("new testRun3, testDescr3, 1, 2022-12-15", taskList);
        Assertions.assertEquals(3, taskList.taskList.size());
    }

    @Test
    void testTaskEdit() throws JAXBException {
        taskList  = new TaskList();
        Main.readAndReact("new ex, testDescr1, 1, 2022-12-13", taskList);
        String taskId = taskList.taskList.get(taskList.taskList.size()-1).getId();
        Main.readAndReact("edit " + taskId + " captionUpd, newDescr, 10, 2022-12-15", taskList) ;
        Task updatedTask = taskList.getTaskById(taskId);
        Assertions.assertEquals("captionUpd", updatedTask.getCaption());
        Assertions.assertEquals("newDescr", updatedTask.getDescription());
        Assertions.assertEquals(10, updatedTask.getPriority());
    }

    @Test
    void testTaskLifecycle() throws JAXBException {
        taskList  = new TaskList();
        Main.readAndReact("new Task, testDescr1, 1, 2022-12-13", taskList);
        String taskId = taskList.taskList.get(0).getId();

        Task task = taskList.getTaskById(taskId);
        Assertions.assertEquals(Task.STATUS_NEW, task.getStatus());

        Main.readAndReact("start " + taskId, taskList);
        Assertions.assertEquals(Task.STATUS_IN_PROGRESS, taskList.getTaskById(taskId).getStatus());

        Main.readAndReact("complete " + taskId, taskList);
        Assertions.assertEquals(Task.STATUS_COMPLETED, taskList.getTaskById(taskId).getStatus());

        Main.readAndReact("remove " + taskId, taskList);
        Assertions.assertTrue(taskList.taskList.get(0).getDeleted());
    }

    @Test
    void testInputValidation() throws JAXBException {
        taskList  = new TaskList();
        Main.readAndReact("new testRun1, testDescr1, 1, 2022-12-13", taskList);
        Main.readAndReact("new testRun2, testDescr2, 1, 2022-12-14", taskList);
        Main.readAndReact("new testRun3, testDescr3, 1, 2022-12-15", taskList);
        int tasksSize = taskList.taskList.size();
        Assertions.assertTrue(tasksSize > 0);

        Main.readAndReact("new dataFailed, testDescr1, 1, 2012-12", taskList);
        Main.readAndReact("new priorityFailed, testDescr1, 1d, 2022-12-13", taskList);
        Main.readAndReact("new badData, testDescr1, 1, 2022-12-13, something", taskList);
        Assertions.assertEquals(tasksSize, taskList.taskList.size());

        String badCaption = "";
        for(int i = 0; i<=51; i++){
            badCaption += "o";
        }
        Main.readAndReact("new " + badCaption + ", testDescr1, 1, 2022-12-13", taskList);
        Assertions.assertEquals(tasksSize, taskList.taskList.size());
    }
}
