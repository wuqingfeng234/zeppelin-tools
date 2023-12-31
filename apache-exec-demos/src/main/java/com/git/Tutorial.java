package com.git;


import org.apache.commons.exec.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Tutorial {

    private final String cmd = "ping 10.53.207.10";

    public static void main(String[] args) throws IOException, InterruptedException {
        Tutorial tutorial = new Tutorial();
        tutorial.buildCmdlineIncrementally();
    }

    private void jdkCmd() throws IOException, InterruptedException {
        String cmd = "cmd /c ping 10.53.207.10";
        Process process = Runtime.getRuntime().exec(cmd);
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            System.out.println(inputStreamToString(process.getInputStream()));
        } else {
            System.out.println(inputStreamToString(process.getErrorStream()));
        }
    }

    private void firstExec() throws IOException {
        CommandLine commandLine = CommandLine.parse(cmd);
        DefaultExecutor executor = new DefaultExecutor();
        executor.execute(commandLine);
    }

    private void resultStream() throws IOException {
        CommandLine commandLine = CommandLine.parse(cmd);
        DefaultExecutor executor = new DefaultExecutor();

        ByteArrayOutputStream susStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(susStream, errStream);

        executor.setStreamHandler(streamHandler);
        executor.setExitValue(0);
        int exitCode = executor.execute(commandLine);

        String suc = susStream.toString("GBK");
        String err = errStream.toString("GBK");
        System.out.println(suc);
        System.out.println("---------------------------");
        System.out.println(err);
    }

    private void watchDog() throws IOException {
        CommandLine commandLine = CommandLine.parse(cmd);
        DefaultExecutor executor = new DefaultExecutor();
        ExecuteWatchdog watchdog = new ExecuteWatchdog(10);
        executor.setWatchdog(watchdog);
        executor.execute(commandLine);
    }
    private void buildCmdlineIncrementally() throws IOException {
        Map map = new HashMap();
        map.put("file", new File("invoice.pdf"));
        CommandLine cmdLine = new CommandLine("C:\\Program Files\\Adobe\\Acrobat DC\\Acrobat\\Acrobat.exe");
        cmdLine.addArgument("/p");
        cmdLine.addArgument("/h");
        cmdLine.addArgument("${file}");
        cmdLine.setSubstitutionMap(map);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(1);
        ExecuteWatchdog watchdog = new ExecuteWatchdog(60000);
        executor.setWatchdog(watchdog);
        int exitValue = executor.execute(cmdLine);

    }
    private void AsyncExec() throws IOException, InterruptedException {
        CommandLine commandLine = CommandLine.parse(cmd);
        DefaultExecutor executor = new DefaultExecutor();

        ByteArrayOutputStream susStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(susStream, errStream);

        executor.setStreamHandler(streamHandler);
        executor.setExitValue(0);
        ExecuteResultHandler executeResultHandler = new ExecuteResultHandler() {
            @Override
            public void onProcessComplete(int i) {
                try {
                    String suc = susStream.toString("GBK");
                    System.out.println(suc);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                System.out.println("3. 异步执行完成");
            }

            @Override
            public void onProcessFailed(ExecuteException e) {
                try {
                    String err = errStream.toString("GBK");
                    System.out.println(err);
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
                System.out.println("3. 异步执行出错");
            }
        };
        executor.execute(commandLine, executeResultHandler);
        Thread.currentThread().join(5 * 1000);
    }

    private String inputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "GBK"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line).append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }
}
