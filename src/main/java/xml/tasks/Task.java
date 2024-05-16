package xml.tasks;

import jakarta.xml.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Отвечает за хранение и обработку данных конкретной задачи из XML-файла
 * Реализует интерфейс Comparable для сортировки вывода задач по статусу (done - в конце) и приоритету (DESC)
 * Реализует интерфейс Cloneable, чтобы при редактировании задачи и ошибке валидации часть данных в оригинальном списке не изменилась. (метод TaskList.getTaskById всегда возвращает клон задачи)
 */
@XmlRootElement(name="task")
@XmlAccessorType(XmlAccessType.FIELD)
public class Task implements Comparable<Task>,Cloneable{
    public final static String STATUS_NEW = "new";
    public final static String STATUS_IN_PROGRESS = "in_progress";
    public final static String STATUS_COMPLETED = "done";
    public static final Set<String> STATUSES = Set.of(STATUS_NEW, STATUS_IN_PROGRESS,  STATUS_COMPLETED);
    public static final Integer CAPTION_MAX_LENGTH = 50;

    @XmlAttribute(name="id")
    private String id;
    @XmlAttribute(name="caption")
    private String caption;
    @XmlAttribute(name="isDeleted")
    private Boolean isDeleted;
    @XmlElement(name="description")
    private String description;
    @XmlElement(name="priority")
    private Integer priority;
    @XmlElement(name="deadline")
    private Date deadline;
    @XmlElement(name="status")
    private String status;
    @XmlElement(name="completionDate")
    private Date completionDate;

    public Task() {
        this.isDeleted = false;
    }

    public static Request generateTask(String command) {
        List<String> values = Arrays.asList(command.substring(command.indexOf("new ") + 4).split((",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"), -1));
        Request request = new Request(Request.Command.NEW);
        Task taskToCreate = new Task();
        taskToCreate.setValues(values, request);
        taskToCreate.setStatus(STATUS_NEW);
        request.setTask(taskToCreate);
        return request;
    }
    public static Request startTask(Task taskToStart) {
        if(taskToStart == null){
            return new Request("Task is not found");
        }
        taskToStart.setStatus(Task.STATUS_IN_PROGRESS);
        return new Request(Request.Command.EDIT, taskToStart);
    }
    public static Request updateTask(String command, Task taskToUpdate) {
        if(taskToUpdate == null){
            return new Request("Task is not found");
        }
        Request request = new Request(Request.Command.EDIT);
        List<String> values = Utils.getArrayOfValues(command, taskToUpdate.getId());
        taskToUpdate.setValues(values, request);
        request.setTask(taskToUpdate);
        return request;
    }
    public static Request completeTask(Task taskToComplete) {
        if(taskToComplete == null){
            return new Request("Task is not found");
        }
        taskToComplete.setStatus(Task.STATUS_COMPLETED);
        taskToComplete.setCompletionDate(new Date());
        return new Request(Request.Command.EDIT, taskToComplete);
    }

    public static Request removeTask(Task taskToComplete) {
        if(taskToComplete == null){
            return new Request("Task is not found");
        }
        taskToComplete.setDeleted(true);
        return new Request(Request.Command.EDIT, taskToComplete);
    }

    private void setValues(List<String> values, Request request) {
        if(values.size() != 4){
            request.setError("Insufficient or excessive data provided");
            return;
        }
        String error = "";
        String caption = values.get(0);
        if(!caption.isEmpty()){
            String captionValue = Utils.removeQuotes(values.get(0));
            if(caption.length() > Task.CAPTION_MAX_LENGTH){
                error += "Caption must be less then 50 characters\n";
            }
            this.setCaption(captionValue);
        }

        String description = values.get(0);
        if(!description.isEmpty()){
            this.setDescription(Utils.removeQuotes(values.get(1)));
        }
        String priorityStr = values.get(2);
        if(!priorityStr.isEmpty()){
            int priority = 0;
            try{
                priority = Integer.parseInt(Utils.removeQuotes(values.get(2)));
            }
            catch (NumberFormatException e){
                error += "Wrong priority value provided\n";
            }
            if(priority < 0 || priority > 10){
                error += "Priority must be from 0 to 10\n";
            }
            this.setPriority(priority);
        }

        String deadLineStr = values.get(3);
        if(!deadLineStr.isEmpty()){
            Date deadLine = null;
            try {
                deadLine = new SimpleDateFormat("yyyy-dd-MM").parse(Utils.removeQuotes(values.get(3)));
            } catch (ParseException e) {
                error += "Date format must be yyyy-dd-MM\n";
            }
            this.setDeadline(deadLine);
        }

        request.setError(error);
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }
    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
    @Override
    public int compareTo(Task o) {

        return status.equals(STATUS_COMPLETED) && !o.status.equals(STATUS_COMPLETED) ? 1 :
                !status.equals(STATUS_COMPLETED) && o.status.equals(STATUS_COMPLETED) ? -1 :
                        priority != null && o.priority != null ? o.priority - priority : 0;

    }

    @Override
    public Task clone() {
        try {
            Task clone = (Task) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
