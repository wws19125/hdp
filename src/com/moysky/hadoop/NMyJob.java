package com.moysky.hadoop;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

public class NMyJob extends org.apache.hadoop.conf.Configured implements org.apache.hadoop.util.Tool {
    public static class MapClass extends org.apache.hadoop.mapreduce.Mapper<LongWritable,Text,Text,Text>
    {
	@Override
	    public void map(LongWritable key,Text value,Context context)
	    throws java.io.IOException,InterruptedException
	    {
		String[] citation = value.toString().split(",");
		context.write(new Text(citation[0]),new Text( citation[1]));
	    }
    }
    public static class Reduce extends org.apache.hadoop.mapreduce.Reducer<Text,Text,Text,Text>
    {
	@Override
	    public void reduce(Text key,Iterable<Text> values,Context context)
	    throws java.io.IOException,java.lang.InterruptedException
	    {
		String cvs = "";
		for(Text t : values)
		    cvs += cvs.length()==0?t.toString():","+t.toString();
		context.write(key, new Text(cvs));
	    }
    }
    @Override
	public int run(String[] args)
	throws Exception
    {
	org.apache.hadoop.conf.Configuration conf = this.getConf();
	org.apache.hadoop.mapreduce.Job job = new org.apache.hadoop.mapreduce.Job(conf, "myJob");
	job.setJarByClass(NMyJob.class);
	org.apache.hadoop.mapreduce.lib.input.FileInputFormat.setInputPaths(job, new org.apache.hadoop.fs.Path(args[0]));
	org.apache.hadoop.mapreduce.lib.output.FileOutputFormat.setOutputPath(job, new org.apache.hadoop.fs.Path(args[1]));
	job.setMapperClass(MapClass.class);
	job.setReducerClass(Reduce.class);
	job.setInputFormatClass(org.apache.hadoop.mapreduce.lib.input.TextInputFormat.class);
	job.setOutputFormatClass(org.apache.hadoop.mapreduce.lib.output.TextOutputFormat.class);
	
	job.setOutputKeyClass(Text.class);
	job.setOutputValueClass(Text.class);
	
	System.exit(job.waitForCompletion(true)?0:1);
	return 0;
    }
    public static void main(String[] args)
	throws java.io.IOException,java.lang.InterruptedException,Exception
    {
	int res = org.apache.hadoop.util.ToolRunner.run(new org.apache.hadoop.conf.Configuration(),new NMyJob(),args);
	System.exit(res);
    }
}
