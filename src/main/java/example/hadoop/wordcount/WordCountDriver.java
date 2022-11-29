package example.hadoop.wordcount;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.util.ToolRunner;

public class WordCountDriver extends org.apache.hadoop.conf.Configured implements org.apache.hadoop.util.Tool {
	private String inputPath;
	private String outputPath;
	private int numReduce;

	public static void main(String[] args) throws Throwable {
		int res = ToolRunner.run(new Configuration(), new WordCountDriver(), args);
		System.exit(res);
	}
	
	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = this.getConf();

		int result = parseArguments(args);
		if (result != 0) {
			return result;
		}
		// job 생성하기
		Job job = Job.getInstance(conf, "Mapreduce WordCount Process");
		job.setJarByClass(WordCountDriver.class);

		job.getConfiguration().addResource("configuation.xml");
		job.getConfiguration().reloadConfiguration();
		job.setNumReduceTasks(numReduce);

		// 입력 경로 설정하기
		FileInputFormat.addInputPaths(job, inputPath);
		// 출력 경로 설정하기
		FileOutputFormat.setOutputPath(job, new Path(outputPath));
		// 데이터를 각각 별도의 파일로 나누기
		MultipleOutputs.addNamedOutput(job, "WordCount", TextOutputFormat.class, IntWritable.class, Text.class);

		job.setMapperClass(WordCountMapper.class);
		job.setReducerClass(WordCountReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);

		boolean success = job.waitForCompletion(true);
		return success ? 0 : 1;
	}
	
	private static final String[][] requiredOptions = {{"i", "일별 데이터 입력 경로를 지정해 주십시오"},{"o", "출력 경로를 지정해 주십시오"},{"r", "Redue 수를 지정해 주십시오"}};
	
	private static Options getOptions() {
		Options options = new Options();
		options.addOption("i", "input", true, "입력경로 (필수)");
		options.addOption("o", "output", true, "출력경로 (필수)");
		options.addOption("r", "numReduce", true, "reduce 수 (필수)");
		return options;
	}
	
	private int parseArguments(String[] args) throws ParseException {
		Options options = getOptions();
		HelpFormatter formatter = new HelpFormatter();
		if (args.length == 0) {
			formatter.printHelp("<JAR>" + getClass().getName(), options, true);
			return -1;
		}
		CommandLineParser parser = new BasicParser();
		CommandLine cmd = parser.parse(options, args);

		for (String[] requiredOption : requiredOptions) {
			if (!cmd.hasOption(requiredOption[0])) {
				formatter.printHelp("<JAR>" + getClass().getName(), options, true);
				return -1;
			}
		}

		if (cmd.hasOption("i")) {
			inputPath = cmd.getOptionValue("i");
		}
		if (cmd.hasOption("o")) {
			outputPath = cmd.getOptionValue("o");
		}
		if (cmd.hasOption("r")) {
			numReduce = Integer.parseInt(cmd.getOptionValue("r"));
		}
		return 0;
	}
}
