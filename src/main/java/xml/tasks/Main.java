package xml.tasks;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.util.Scanner;

/**
 * Отвечает за:
 *  Пуск и остановку приложения.
 *  Считывание и запись данных в файл XML
 *  Считывание команд с консоли и вызов соответствующих функций.
 */
public class Main {

    final static String FILE_PATH = Utils.isJUnitTest() ? "src/test/data/tasks.xml" : "src/data/tasks.xml";
    final static String STOP_CODE = "exit";
    final static JAXBContext context;

    static {
        try {
            context = JAXBContext.newInstance(TaskList.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @throws JAXBException на случай, если файл окажется пустым
     */
    public static void main(String[] args) throws JAXBException {

        Unmarshaller unmarshaller = context.createUnmarshaller();
        TaskList taskList;
        try{
            taskList = (TaskList) unmarshaller.unmarshal(new File(FILE_PATH));
        }
        catch (JAXBException ex){
            taskList = new TaskList();
        }

        Scanner sc = new Scanner(System.in);
        Printer.printWelcome();
        while(true){
            String command = sc.nextLine().trim().replaceAll("[\\s]{2,}", " ");
            if(command.equalsIgnoreCase(STOP_CODE)){
                return;
            }
            readAndReact(command, taskList);
        }

    }

    /**
     *
     * @param command - отредактированная команда ввода (с удаленными лишними пробелами)
     * @param taskList - объект класса TaskList, в котором хранятся данные из XML-файла
     * @throws JAXBException
     * Обрабатывает введенную команду и получает объект класса Request, в котором хранятся результаты обработки.
     */
    public static void readAndReact(String command, TaskList taskList) throws JAXBException {
        Request request = Request.generateRequest(command, taskList);
        if(request.getError() != null && !request.getError().isEmpty()){
            Printer.printError(request);
            return;
        }
        switch (request.getName()) {
            case HELP -> Printer.printHelp();
            case LIST -> Printer.printListOutput(taskList.getListCommand(request));
            case NEW -> Printer.printCreateResult(taskList.addTask(request));
            case EDIT -> Printer.printUpdateResult(taskList.editTask(request));
        }

    }

    public static void rebuildXML(TaskList taskList) throws JAXBException {
        Marshaller marsh = context.createMarshaller();
        marsh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marsh.marshal(taskList, new File(FILE_PATH));
    }
}