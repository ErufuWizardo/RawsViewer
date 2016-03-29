package erufu.wizardo.rawsviewer;

public class ExecutionHelper {

    public void executeWithExceptionPropagation(Command command) {
        try {
            command.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    public interface Command {
        void execute() throws Exception;
    }

}
