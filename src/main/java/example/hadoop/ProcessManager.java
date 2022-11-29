package example.hadoop;

import org.apache.hadoop.util.ProgramDriver;
import example.hadoop.wordcount.WordCountDriver;

public class ProcessManager {

	public static void main(String[] args) throws Throwable {
		ProgramDriver programDriver = new ProgramDriver();
		programDriver.addClass("word", WordCountDriver.class, "TIMS MapReduce job(WordCount)");
		programDriver.driver(args);

		// ProgramDriver programDiver = new P



	}
}
