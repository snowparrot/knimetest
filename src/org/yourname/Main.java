package org.yourname;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.LambdaConversionException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Main class.
 * 
 * @author KNIME GmbH
 */
public class Main {

	public static void main(String[] args) throws IOException {
		Operations operations = Operations.getInstance();
		

		String input_source = "";
		String input_type = "";
		LinkedList<String> cmd_operations = new LinkedList<String>();
		int n_threads = 0;
		String output_source = "";

		for (int i = 0; i < args.length; i++)
		{
			String arg = args[i];
			switch(arg)
			{
				case "--input":
					input_source = args[i + 1];
					// so we ignore the input in the loop
					i = i + 1;
					break;
				case "--inputtype":
					if (operations.ifTypeIsPossible(args[i + 1])) {
						input_type = args[i + 1];
					}
					i = i + 1;
					break;
				case "--output":
					output_source = args[i + 1];
					i = i + 1;
					break;
				case "--threads":
					n_threads = Integer.parseInt(args[i + 1]);
					i = i + 1;
					break;
				case "--operations":
					// after operations we have to check a list of operations
					for (int j = i + 1; j < args.length; j++)
					{
						String cmd_op = args[j];
						if (operations.ifOperationIsPossible(input_type, cmd_op))
						{
							cmd_operations.add(cmd_op);
						}
						else
						{
							// so the outer loop can read the argument again
							i = j - 1;
							break;
						}
					}
					break;
				default:
			}
		}

		if (output_source == "")
		{
			output_source = "output.txt";
		}

		// reads the source
		BufferedReader reader = new BufferedReader(new FileReader(input_source));
		ArrayList<String> input_file = new ArrayList<String>();
		String line;
		while ((line = reader.readLine()) != null) {
			input_file.add(line);
		}
		reader.close();



		LinkedList<String> output_file = new LinkedList<String>();
		ExecutorService executor = Executors.newFixedThreadPool(n_threads); 

		int chunk_size = input_file.size() / n_threads;

		switch(input_type) {
			case "string": {
				Operations.lambda<String> chained_op = operations.chain_string_operations(cmd_operations);

				// we can use the list to reference to all futures in the right order
				List<Future<List<String>>> future_list;
				future_list = new ArrayList<Future<List<String>>>();

				for (int i = 0; i < input_file.size(); i += chunk_size) {
					// chunking here
					int end_chunk = i < input_file.size() ? i + chunk_size : input_file.size();
					List<String> chunk_list = input_file.subList(i, end_chunk);
					
					// starts one future
					LambdaCallable<String> new_lambda = new LambdaCallable<String>(chained_op, chunk_list);
					Future<List<String>> f_lambda = executor.submit(new_lambda);

					future_list.add(f_lambda);
				}

				try {
					// here we ensure the right order
					for (Future<List<String>> f_lambda : future_list) {
						
						List<String> output_chunk = f_lambda.get();
						
						for (String output_line: output_chunk) {
							output_file.add(output_line);
						}
					}
				} catch (Exception e) {
					System.out.println("Not valid data");
					e.printStackTrace();
					System.exit(-1);
				}
				break;
			}
			case "int": {
				Operations.lambda<Integer> chained_op = operations.chain_integer_operations(cmd_operations);
				
				List<Future<List<Integer>>> future_list;
				future_list = new ArrayList<Future<List<Integer>>>();


				for (int i = 0; i < input_file.size(); i += chunk_size) {
					int end_chunk = i < input_file.size() ? i + chunk_size : input_file.size();
					List<Integer> chunk_list  = new LinkedList<Integer>();


					for (String string_line : input_file.subList(i, end_chunk)) {
						chunk_list.add(Integer.parseInt(string_line));
					}
					
					LambdaCallable<Integer> new_lambda = new LambdaCallable<Integer>(chained_op, chunk_list);
					Future<List<Integer>> f_lambda = executor.submit(new_lambda);

					future_list.add(f_lambda);
				}

				try {

					for (Future<List<Integer>> f_lambda : future_list) {
						List<Integer> output_chunk = f_lambda.get();
						
						for (Integer output_line: output_chunk) {
							output_file.add(Integer.toString(output_line));
						}
					}
				} catch (Exception e) {
					System.out.println("Not valid data");
					e.printStackTrace();
					System.exit(-1);
				}
				break;
			}
			case "double": {
				Operations.lambda<Double> chained_op = operations.chain_double_operations(cmd_operations);
				
				List<Future<List<Double>>> future_list;
				future_list = new ArrayList<Future<List<Double>>>();


				for (int i = 0; i < input_file.size(); i += chunk_size) {
					int end_chunk = i < input_file.size() ? i + chunk_size : input_file.size();
					List<Double> chunk_list  = new LinkedList<Double>();
					for (String string_line : input_file.subList(i, end_chunk)) {
						chunk_list.add(Double.parseDouble(string_line));
					}
					
					
					LambdaCallable<Double> new_lambda = new LambdaCallable<Double>(chained_op, chunk_list);
					Future<List<Double>> f_lambda = executor.submit(new_lambda);

					future_list.add(f_lambda);
				}

				try {

					for (Future<List<Double>> f_lambda : future_list) {
						List<Double> output_chunk = f_lambda.get();
						
						for (Double output_line: output_chunk) {
							output_file.add(Double.toString(output_line));
						}
					}
				} catch (Exception e) {
					System.out.println("Not valid data");
					e.printStackTrace();
					System.exit(-1);
				}
				break;
			}
		}

		try {
			File output_fo = new File(output_source);
			output_fo.createNewFile();

			FileWriter output_fw = new FileWriter(output_fo, false);
			
			for (String output_line: output_file) {
				Statistics.getInstance().updateStatisticsWithLine(output_line);
				output_fw.write(output_line + "\n");
			}

			output_fw.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

		

		
		// DO NOT CHANGE THE FOLLOWING LINES OF CODE
		System.out.println(String.format("Processed %d lines (%d of which were unique)", //
				Statistics.getInstance().getNoOfLinesRead(), //
				Statistics.getInstance().getNoOfUniqueLines()));
	}


	public static class LambdaCallable<E> implements Callable<List<E>> {
		private Operations.lambda<E> lambda;
		private List<E> chunk;

		public LambdaCallable(Operations.lambda<E> lambda, List<E> chunk) {
			this.lambda = lambda;
			this.chunk = chunk;
		}

		public List<E> call() throws Exception {
			List<E> output = new LinkedList<E>();
			for (E element: chunk) {
				output.add(lambda.use(element));
			}

			return output;
		}
	}
}
