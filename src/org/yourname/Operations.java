package org.yourname;
import java.util.*;



/**
 * This class deals with all aspects of getting the operators from terminal
 * to the real function.
 * 
 * @author Benjamin Krala
 */
public class Operations {
    private static Operations singleton;

    private String[] possible_types = {"string", "int", "double"};

    // Key is a type, Value is an Array which includes all possible terminal commands to use it
    private HashMap<String, String[]> possible_operations;

    // Key is a operation from the command line, Value is an lambda expression to execute it
    private HashMap<String, lambda<String>> operations_string;
    private HashMap<String, lambda<Integer>> operations_integer;
    private HashMap<String, lambda<Double>> operations_double;

    /** 
     * Easy lambda interface. Gets an element of type E and returns one element of type E.
    */
    public interface lambda<E> {
        public E use(E input);
    }

    private Operations () {
        possible_operations = new HashMap<String, String[]>();

        String[] op_str = {"capitalize", "reverse"};
        String[] op_num = {"reverse", "neg"};

        possible_operations.put("string", op_str);
        possible_operations.put("int", op_num);
        possible_operations.put("double", op_num);

        operations_string = new HashMap<String, lambda<String>>();
        //capitalize command for strings
        lambda<String> cap = (input) -> {
            return input.substring(0, 1).toUpperCase() + input.substring(1);
        };
        operations_string.put("capitalize", cap);
        //reverse command for strings
        lambda<String> rev_str = (input) -> {
            StringBuilder sb = new StringBuilder(input);  
            sb.reverse();
            return sb.toString();
        };
        operations_string.put("reverse", rev_str);

        operations_integer = new HashMap<String, lambda<Integer>>();
        //reverse command for Integers
        lambda<Integer> rev_int = (input) -> {
            int factor = 1;
            // here we keep the sign
            if (input < 0) {
                factor = -1;
                input = -1 * input;
            }
            StringBuilder sb = new StringBuilder(Integer.toString(input));  
            sb.reverse();
            return factor * Integer.parseInt(sb.toString());
        };
        operations_integer.put("reverse", rev_int);
        lambda<Integer> neg_int = (input) -> {
            return -1 * input;
        };
        operations_integer.put("neg", neg_int);

        operations_string.put("reverse", rev_str);

        operations_double = new HashMap<String, lambda<Double>>();
        lambda<Double> rev_dou = (input) -> {
            Double factor = 1.;
            if (input < 0) {
                factor = -1.;
                input = -1. * input;
            }
            StringBuilder sb = new StringBuilder(Double.toString(input));  
            sb.reverse();
            return factor * Double.parseDouble(sb.toString());
        };
        operations_double.put("reverse", rev_dou);
        lambda<Double> neg_dou = (input) -> {
            return -1. * input;
        };
        operations_double.put("neg", neg_dou);

    }

    public boolean ifTypeIsPossible (String cmd_type) {

        for (String possible_type: possible_types) {
            if (possible_type.equals(cmd_type)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the terminal type and the terminal operation is possible
     * @param cmd_type String which is the type from the terminal
     * @param cmd_op String which is one operation from the terminal
     * @return bool. It indicates if the Operation is possible.
     */
    public boolean ifOperationIsPossible (String cmd_type, String cmd_op) {
        String[] operations = possible_operations.get(cmd_type);
        for (String op: operations) {
            if (op.equals(cmd_op)) {
                return true;
            }
        }

        return false;

    }

    /**
     *  We give a list of all operations from the command line. 
     * The function returns a lambda which executes this on a string. 
     * @param cmd_operations a list of all operations from the command line. 
     *        One element for example is "reverse" or "neg"
     * @return a lambda which executes all operations on a string.
     */
    public lambda<String> chain_string_operations(List<String> cmd_operations) {
        List<lambda<String>> list_lambdas = new LinkedList<lambda<String>>();
        // for every terminal operation we find the matching lambda
        for (String cmd_op: cmd_operations) {
            list_lambdas.add(operations_string.get(cmd_op));
        }
        // now lets execute all lambdas in a row
        lambda<String> chain_string_operations = (input) -> {
            for (lambda<String> single_lambda: list_lambdas)
            {
               input = single_lambda.use(input);
            }
            return input;
        };
        return chain_string_operations;
    }

    /**
     *  We give a list of all operations from the command line. 
     * The function returns a lambda which executes this on a Integer. 
     * @param cmd_operations a list of all operations from the command line. 
     *        One element for example is "reverse" or "neg"
     * @return a lambda which executes all operations on a Integer.
     */
    public lambda<Integer> chain_integer_operations(List<String> cmd_operations) {
        List<lambda<Integer>> list_lambdas = new LinkedList<lambda<Integer>>();
        for (String cmd_op: cmd_operations) {
            list_lambdas.add(operations_integer.get(cmd_op));
        }
        lambda<Integer> chain_integer_operations = (input) -> {
            for (lambda<Integer> single_lambda: list_lambdas)
            {
               input = single_lambda.use(input);
            }
            return input;
        };
        return chain_integer_operations;
    }

    /**
     *  We give a list of all operations from the command line. 
     * The function returns a lambda which executes this on a Double. 
     * @param cmd_operations a list of all operations from the command line. 
     *        One element for example is "reverse" or "neg"
     * @return a lambda which executes all operations on a Double.
     */
    public lambda<Double> chain_double_operations(List<String> cmd_operations) {
        List<lambda<Double>> list_lambdas = new LinkedList<lambda<Double>>();
        for (String cmd_op: cmd_operations) {
            list_lambdas.add(operations_double.get(cmd_op));
        }
        lambda<Double> chain_double_operations = (input) -> {
            for (lambda<Double> single_lambda: list_lambdas)
            {
               input = single_lambda.use(input);
            }
            return input;
        };
        return chain_double_operations;
    }





    /**
     * There is only one instance of the class.
     * You can use this function to get it.
     * @return The instance of the class.
     */
    public static Operations getInstance()
    {        
        if (singleton == null)
		{
			singleton = new Operations();
		}
		return singleton;
    }
}
