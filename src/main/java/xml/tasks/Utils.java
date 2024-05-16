package xml.tasks;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Utils {
    public static boolean isJUnitTest() {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().startsWith("org.junit.")) {
                return true;
            }
        }
        return false;
    }
    public static String removeQuotes(String value){
        String result = value.trim();
        if(result.startsWith("\"")){
            result = result.substring(1);
        }
        if(result.endsWith("\"")){
            result = result.substring(0, result.length()-1);
        }
        return result;
    }

    public static String getTaskId(String command) {
        String edit = command.substring(command.indexOf(" ") + 1);
        return removeQuotes(edit.substring(0, edit.contains(" ") ? edit.indexOf(" ") : edit.length()));
    }

    public static String getMainCommand(String command) {
        return command.indexOf(" ") > 0 ? command.trim().substring(0, command.indexOf(" ")).toLowerCase() : command;
    }

    public static int getTaskIndex(List<Task> taskList, String taskId){
        return IntStream.range(0, taskList.size())
                .filter(i -> taskList.get(i).getId().equals(taskId))
                .findFirst()
                .orElse(-1);
    }

    public static List<String> getArrayOfValues(String command, String id) {
        String idString = command.substring(command.indexOf(id) + id.length());
        String valueString = idString.substring(idString.indexOf(" ") + 1);
        return Arrays.asList(valueString.split((",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"), -1));
    }
}
