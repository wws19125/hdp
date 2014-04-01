package com.moysky.hadoop;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.MultipleTextOutputFormat;

public class MultiFile extends org.apache.hadoop.conf.Configured implements org.apache.hadoop.util.Tool
{
	public static class MapClass extends org.apache.hadoop.mapred.MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable, Text,NullWritable,Text>
	{
		@Override
		public void map(LongWritable key,Text value,OutputCollector<NullWritable,Text> output,Reporter reporter)
			throws java.io.IOException
		{
			output.collect(NullWritable.get(),value);
		}
	}
	public static class PartitionByCounterMTOF extends MultipleTextOutputFormat<NullWritable,Text>
	{
		@Override
		public String generateFileNameForKeyValue(NullWritable key,Text value,String filename)
		{
			String[] arr = value.toString().split(",",-1);
			String country = arr[4].substring(1,3);
			return country+"/"+filename;
		}
	}
	public static class MosMapClass extends org.apache.hadoop.mapred.MapReduceBase implements org.apache.hadoop.mapred.Mapper<LongWritable,Text,NullWritable,Text>
	{
		protected org.apache.hadoop.mapred.lib.MultipleOutputs mos;
		protected org.apache.hadoop.mapred.OutputCollector<NullWritable,Text> col;
		@Override
		public void map(LongWritable key,Text value,org.apache.hadoop.mapred.OutputCollector<NullWritable,Text> out,org.apache.hadoop.mapred.Reporter reporter)
			throws java.io.IOException
		{
			String[] arr = value.toString().split(",",-1);
			String chrono = arr[0]+","+arr[1]+","+arr[2];
			String geo = arr[0]+","+arr[4]+","+arr[5];
			col = mos.getCollector("chrono", reporter);
			col.collect(NullWritable.get(), new Text(chrono));
			col = mos.getCollector("geo", reporter);
			col.collect(NullWritable.get(), new Text(geo));
		}
		@Override 
		public void configure(JobConf conf)
		{
			mos = new org.apache.hadoop.mapred.lib.MultipleOutputs(conf);
		}
		@Override
		public void close() throws java.io.IOException
		{
			mos.close();
		}
	}
	@Override
	public int run(String[] args)
		throws java.io.IOException
	{
		org.apache.hadoop.conf.Configuration conf = this.getConf();
		org.apache.hadoop.mapred.JobConf job = new org.apache.hadoop.mapred.JobConf(conf,MultiFile.class);
		org.apache.hadoop.mapred.FileInputFormat.setInputPaths(job, new org.apache.hadoop.fs.Path(args[0]));
		org.apache.hadoop.mapred.FileOutputFormat.setOutputPath(job, new org.apache.hadoop.fs.Path(args[1]));
		
		job.setJobName("multifile");
		job.setMapperClass(MosMapClass.class);
		
		job.setInputFormat(org.apache.hadoop.mapred.TextInputFormat.class);
		//job.setOutputFormat(PartitionByCounterMTOF.class);
		job.setOutputValueClass(Text.class);
		org.apache.hadoop.mapred.lib.MultipleOutputs.addNamedOutput(job,"chrono",org.apache.hadoop.mapred.TextOutputFormat.class,NullWritable.class,Text.class);
		org.apache.hadoop.mapred.lib.MultipleOutputs.addNamedOutput(job,"geo",org.apache.hadoop.mapred.TextOutputFormat.class,NullWritable.class,Text.class);
		job.setNumReduceTasks(0);
		org.apache.hadoop.mapred.JobClient.runJob(job);
		return 0;
	}
	public static void main(String[] args)
		throws Exception
	{
		int res = org.apache.hadoop.util.ToolRunner.run(new org.apache.hadoop.conf.Configuration(), new MultiFile(), args);
		System.exit(res);
	}
}
