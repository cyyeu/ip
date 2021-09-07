package duke.parser.cli;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import duke.commands.AddTaskCommand;
import duke.commands.Command;
import duke.commands.DeleteCommand;
import duke.commands.DoneCommand;
import duke.commands.ExitCommand;
import duke.commands.FindCommand;
import duke.commands.InvalidArgumentsException;
import duke.commands.ListCommand;
import duke.commands.UnknownCommandException;
import duke.parser.UnableToParseException;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Todo;

/**
 * This class encapsulates the parsing of CLI commands and strings.
 */
public class CliParser {
    private static final InvalidArgumentsException INVALID_ARG_EXCEPTION_TODO =
            new InvalidArgumentsException("todo [task]");
    private static final InvalidArgumentsException INVALID_ARG_EXCEPTION_FIND =
            new InvalidArgumentsException("find [query]");
    private static final InvalidArgumentsException INVALID_ARG_EXCEPTION_EVENT =
            new InvalidArgumentsException("event [task] /at [time period]");
    private static final InvalidArgumentsException INVALID_ARG_EXCEPTION_DEADLINE =
            new InvalidArgumentsException("deadline [task] /by [YYYY-MM-DD]");
    private static final InvalidArgumentsException INVALID_ARG_EXCEPTION_DELETE =
            new InvalidArgumentsException("delete [task id]");
    private static final InvalidArgumentsException INVALID_ARG_EXCEPTION_DONE =
            new InvalidArgumentsException("done [task id]");
    private static final String DELIMITER_CLI = " ";
    private static final String DELIMITER_DEADLINE = " /by ";
    private static final String DELIMITER_EVENT = " /at ";
    /**
     * Returns the corresponding command from user input.
     *
     * @param input The user input.
     * @return The corresponding command.
     * @throws UnknownCommandException Thrown if the command keyword has no match.
     * @throws InvalidArgumentsException Thrown if there are invalid arguments.
     * @throws UnableToParseException Thrown if there was an error parsing the command.
     */
    public static Command parseCommand(String input)
            throws UnknownCommandException, InvalidArgumentsException, UnableToParseException {
        // index 0 has command, index 1 has command arguments (if applicable)
        String[] splitInput = input.split(DELIMITER_CLI, 2);
        String stringCmd = splitInput[0];
        switch (stringCmd) {
        case "todo":
            return parseTodo(splitInput);
        case "deadline":
            return parseDeadline(splitInput);
        case "event":
            return parseEvent(splitInput);
        case "done":
            return parseDone(splitInput);
        case "delete":
            return parseDelete(splitInput);
        case "find":
            return parseFind(splitInput);
        case "list":
            return new ListCommand();
        case "bye":
            return new ExitCommand();
        default:
            throw new UnknownCommandException();
        }
    }

    /**
     * Parses the add todo command
     *
     * @param args The Arguments of the command.
     * @return The corresponding command.
     * @throws InvalidArgumentsException Thrown if arguments are invalid.
     */
    private static AddTaskCommand parseTodo(String[] args) throws InvalidArgumentsException {
        if (args.length != 2) {
            throw INVALID_ARG_EXCEPTION_TODO;
        }

        return new AddTaskCommand(new Todo(args[1]));
    }

    /**
     * Parses the add find command
     *
     * @param args The Arguments of the command.
     * @return The corresponding command.
     * @throws InvalidArgumentsException Thrown if arguments are invalid.
     */
    private static Command parseFind(String[] args) throws InvalidArgumentsException {
        if (args.length != 2) {
            throw INVALID_ARG_EXCEPTION_FIND;
        }

        return new FindCommand(args[1]);
    }


    /**
     * Parses the add event command
     *
     * @param args The Arguments of the command.
     * @return The corresponding command.
     * @throws InvalidArgumentsException Thrown if arguments are invalid.
     */
    private static AddTaskCommand parseEvent(String[] args) throws InvalidArgumentsException {
        if (args.length != 2) {
            throw INVALID_ARG_EXCEPTION_EVENT;
        }

        String[] eventArgs = args[1].split(DELIMITER_EVENT);
        if (eventArgs.length != 2) {
            throw INVALID_ARG_EXCEPTION_EVENT;
        }

        return new AddTaskCommand(new Event(eventArgs[0], eventArgs[1]));
    }

    /**
     * Parses the add deadline command
     *
     * @param args The Arguments of the command.
     * @return The corresponding command.
     * @throws InvalidArgumentsException Thrown if arguments are invalid.
     */
    private static AddTaskCommand parseDeadline(String[] args) throws InvalidArgumentsException {
        if (args.length != 2) {
            throw INVALID_ARG_EXCEPTION_DEADLINE;
        }
        String[] deadlineArgs = args[1].split(DELIMITER_DEADLINE);
        if (deadlineArgs.length != 2) {
            throw INVALID_ARG_EXCEPTION_DEADLINE;
        }

        try {
            LocalDate by = LocalDate.parse(deadlineArgs[1]);
            return new AddTaskCommand(new Deadline(deadlineArgs[0], by));
        } catch (DateTimeParseException e) {
            throw INVALID_ARG_EXCEPTION_DEADLINE;
        }
    }


    /**
     * Parses the done command
     *
     * @param args The Arguments of the command.
     * @return The corresponding command.
     * @throws InvalidArgumentsException Thrown if arguments are invalid.
     * @throws UnableToParseException Thrown if unable to parse task id.
     */
    private static DoneCommand parseDone(String[] args) throws InvalidArgumentsException, UnableToParseException {
        if (args.length != 2) {
            throw INVALID_ARG_EXCEPTION_DONE;
        }

        int taskId = parseTaskId(args[1]);
        return new DoneCommand(taskId);
    }

    /**
     * Parses the delete command
     *
     * @param args The Arguments of the command.
     * @return The corresponding command.
     * @throws InvalidArgumentsException Thrown if arguments are invalid.
     * @throws UnableToParseException Thrown if unable to parse task id.
     */
    private static DeleteCommand parseDelete(String[] args) throws InvalidArgumentsException, UnableToParseException {
        if (args.length != 2) {
            throw INVALID_ARG_EXCEPTION_DELETE;
        }

        int taskId = parseTaskId(args[1]);
        return new DeleteCommand(taskId);
    }

    private static int parseTaskId(String index) throws UnableToParseException {
        int i;

        try {
            i = Integer.parseInt(index);
        } catch (NumberFormatException e) {
            throw new UnableToParseException("task id");
        }

        return i;
    }
}