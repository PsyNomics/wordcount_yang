package example.hadoop.wordcount;


import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class WordCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
	private MultipleOutputs<Text, IntWritable> multipleOutputs;
	
	@Override
	public void setup(Context context) {
		multipleOutputs = new MultipleOutputs<>(context);
	}
	
	@Override
	public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
		int sum =0;
		
		for (IntWritable val : values) {
			sum += val.get();
		}
		
		multipleOutputs.write("WordCount", key, new IntWritable(sum));
		// 첫 번째 인자로
	}
}
