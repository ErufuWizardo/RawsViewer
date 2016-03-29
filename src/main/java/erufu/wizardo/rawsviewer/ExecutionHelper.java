/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package erufu.wizardo.rawsviewer;

/**
 *
 * @author Zorag
 */
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
