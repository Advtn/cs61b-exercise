package gitlet;


import static gitlet.MyUtils.exit;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Advtn
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            exit("Please enter a command.");
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init" -> {
                validateNumArgs(args, 1);
                Repository.init();
            }
            case "add" -> {
                Repository.checkWorkingDir();
                validateNumArgs(args, 2);
                String fileName = args[1];
                new Repository().add(fileName);
            }
            case "commit" -> {
                Repository.checkWorkingDir();
                validateNumArgs(args, 2);
                String message = args[1];
                if (message.isEmpty()) {
                    exit("Please enter a commit message.");
                }
                new Repository().commit(message);
            }
            case "rm" -> {
                Repository.checkWorkingDir();
                validateNumArgs(args, 2);
                String fileName = args[1];
                new Repository().remove(fileName);
            }
            case "log" -> {
                Repository.checkWorkingDir();
                validateNumArgs(args, 1);
                new Repository().log();
            }
            case "global-log" -> {
                Repository.checkWorkingDir();
                validateNumArgs(args, 1);
            }
            case "find" -> {
                Repository.checkWorkingDir();
                validateNumArgs(args, 2);
                String commitMessage = args[1];
                new Repository().find(commitMessage);
            }
            case "status" -> {
                Repository.checkWorkingDir();
                validateNumArgs(args, 1);
                new Repository().status();
            }
            case "checkout" -> {
                validateNumArgs(args, 1);
            }
            case "branch" -> {
                Repository.checkWorkingDir();
                validateNumArgs(args, 2);
                String branchName = args[1];
                new Repository().branch(branchName);
            }
            case "rm-branch" ->{
                validateNumArgs(args, 2);
            }
            case "reset" -> {
                validateNumArgs(args, 2);
            }
            case "merge" -> {
                validateNumArgs(args, 2);
            }
            default -> exit("No command with that name exists.");
        }
    }

    public static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            exit("Incorrect operands.");
        }
    }
}
