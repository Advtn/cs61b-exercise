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
            case "init":
                validateNumArgs(args, 1);
                Repository.init();
                break;
            case "add":
                Repository.checkWorkingDir();
                validateNumArgs(args, 2);
                String fileName = args[1];
                new Repository().add(fileName);
                break;
            case "commit":
                Repository.checkWorkingDir();
                validateNumArgs(args, 2);
                String message = args[1];
                new Repository().commit(message);
                break;
            case "rm":
                validateNumArgs(args, 2);
                break;
            case "log":
                Repository.checkWorkingDir();
                validateNumArgs(args, 1);
                new Repository().log();
                break;
            case "global-log":
                validateNumArgs(args, 1);
                break;
            case "find":
                validateNumArgs(args, 2);
                break;
            case "status":
                Repository.checkWorkingDir();
                validateNumArgs(args, 1);
                new Repository().status();
                break;
            case "checkout":
                validateNumArgs(args, 1);
                break;
            case "branch":
                validateNumArgs(args, 2);
                break;
            case "rm-branch":
                validateNumArgs(args, 2);
                break;
            case "reset":
                validateNumArgs(args, 2);
                break;
            case "merge":
                validateNumArgs(args, 2);
                break;
            default:
                exit("No command with that name exists.");
                break;
        }
    }

    public static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            exit("Incorrect operands.");
        }
    }
}
