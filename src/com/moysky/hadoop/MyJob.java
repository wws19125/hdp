package com.moysky.hadoop;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.KeyValueTextInputFormat;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.io.IntWritable;

public class MyJob extends Configured implements Tool {
	public static class MapClass extends MapReduceBase implements Mapper<Text,Text,Text,IntWritable>
	{
		
		@Override
		public void map(Text key, Text value,
				OutputCollector<Text, IntWritable> output, Reporter reporter)
				throws IOException {
			// TODO Auto-generated method stub
			//output.collect(value, key);
		    output.collect(key,new IntWritable(1));
		}
		
	}
	public static class Reduce extends MapReduceBase implements org.apache.hadoop.mapred.Reducer<Text,IntWritable,Text,IntWritable>
	{

		@Override
		public void reduce(Text key, Iterator<IntWritable> values,
				OutputCollector<Text, IntWritable> output, Reporter reporter)
				throws IOException {
			// TODO Auto-generated method stub
		    /*
			String csv = "";
			while(values.hasNext())
			{
				if(csv.length()>0)csv += ",";
				csv += values.next().toString();
			}
			output.collect(key, new Text(csv));
		    */
		    int sum=0;
		    while(values.hasNext())
			{
			    sum+=2;
			    //sum += values.next().get();
			    values.next();
			}
		    output.collect(key,new IntWritable(sum));
		}
		
	}
	@Override
	public int run(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Configuration conf = this.getConf();
		org.apache.hadoop.mapred.JobConf job = new org.apache.hadoop.mapred.JobConf(conf,MyJob.class);
		org.apache.hadoop.fs.Path in = new Path(args[0]);
		org.apache.hadoop.fs.Path out = new Path(args[1]);
		org.apache.hadoop.mapred.FileInputFormat.setInputPaths(job, in);
		org.apache.hadoop.mapred.FileOutputFormat.setOutputPath(job, out);
		job.setJobName("MyJob");
		job.setMapperClass(MapClass.class);
		job.setReducerClass(Reduce.class);
		job.setInputFormat(KeyValueTextInputFormat.class);
		job.setOutputFormat(TextOutputFormat.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		job.set("key.value.separator.in.input.line", ",");
		JobClient.runJob(job);
		return 0;
	}
	public static void main(String[] args) throws Exception
	{
		int res = ToolRunner.run(new Configuration(), new MyJob(),args);
		System.exit(res);
	}
}

