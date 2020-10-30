package org.yourname.test;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import org.junit.*;
import org.yourname.Operations;

public class testOperations {
    Operations op = Operations.getInstance();

    // Test if the chain operations work with Strings
    @Test
    public void testStringChainOperations()
    {
        String[] str_ops = {"capitalize", "reverse"};
        ArrayList<String> test_ops = new ArrayList<String>();
        Collections.addAll(test_ops, str_ops);
        
        Operations.lambda<String> chained_operation =  op.chain_string_operations(test_ops);
        assertEquals("21 cbA", chained_operation.use("abc 12"));
    }

        // Test if the chain operations work with Integer
        @Test
        public void testIntChainOperations()
        {
            String[] str_ops = {"neg", "reverse"};
            ArrayList<String> test_ops = new ArrayList<String>();
            Collections.addAll(test_ops, str_ops);
            
            Operations.lambda<Integer> chained_operation =  op.chain_integer_operations(test_ops);
            assertEquals((int) -1, (int) chained_operation.use(10));
        }
}
