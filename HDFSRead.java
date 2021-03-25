package producer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.IOException;
import java.net.URI;
//import java.nio.file.FileSystem;

//public class HDFSRead {
//    static String uri = "hdfs://skkvm.eastus.cloudapp.azure.com:8020/user/azureuser/tfile.out";
//    public static void main(String[] args) throws IOException, InterruptedException {
//        Configuration conf = new Configuration();
//        conf.set("fs.defaultFS", "hdfs://skkvm.eastus.cloudapp.azure.com:8020");
////        conf.set("dfs.datanode.dns.interface", "eth0");
//        FileSystem fs = FileSystem.get(URI.create(uri), conf);
//        FSDataInputStream in = null;
//        try{
//            in = fs.open(new Path(uri));
//            IOUtils.copyBytes(in, System.out, 4096, false);
////            in.seek(0);
//        } finally {
//            IOUtils.closeStream(in);
//            fs.close();
//        }
//    }
//}
public class HDFSRead {
    static String uri = "hdfs://skkvm.eastus.cloudapp.azure.com:8020/user/azureuser/tfile.out";
    static String coreFilePathString = "file:///D:/tmp/core-site.xml";
    static String hdfsFilePathString = "hdfs://13.90.25.110:8020/user/azureuser/tfile.out";

    public static void main(String[] args) throws IOException, InterruptedException {
        Configuration conf = new Configuration();
        Path coreSitePath = new Path(coreFilePathString);
        conf.addResource(coreSitePath);
//        conf.set("fs.defaultFS", "hdfs://skkvm.eastus.cloudapp.azure.com:8020");
//        conf.set("dfs.datanode.dns.interface", "eth0");
        FileSystem fs = FileSystem.get(conf);
        Path inputPath = new Path(hdfsFilePathString);
        FSDataInputStream in = null;
        try {
            in = fs.open(inputPath);
            IOUtils.copyBytes(in, System.out, 4096, false);
//            in.seek(0);
        } finally {
            IOUtils.closeStream(in);
            fs.close();
        }
    }
}

