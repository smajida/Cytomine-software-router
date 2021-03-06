package src.be.cytomine.software.consumer

import groovy.util.logging.Log4j

/*
 * Copyright (c) 2009-2015. Authors: see NOTICE file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@Log4j
class JobExecutionThread implements Runnable{

    ArrayList commandToExecute
    String softwareName
    File jobDirectory = new File(RabbitWorker.configFile.jobDirectory as String)

    @Override
    void run() {
        // Log file
        String logFile = softwareName + "-" + new Date().format('d-M-yyyy_hh-mm-ss-SSS').toString() + ".log"
        File logFileJob = new File((String)RabbitWorker.configFile.logsDirectory + logFile)
        logFileJob.getParentFile().mkdirs();
        logFileJob.createNewFile();

        // Data directory for software data
        def dataDirectory = new File((String)RabbitWorker.configFile.dataDirectory + softwareName + "-" + new Date().format('d-M-yyyy_hh-mm-ss-SSS').toString())
        if (!dataDirectory.exists()) {
            dataDirectory.mkdirs()
        }

        log.info "Job to execute : " + commandToExecute

        def process = new ProcessBuilder(commandToExecute)
        process.directory(jobDirectory)
        process.redirectErrorStream(true)

        process.redirectOutput(ProcessBuilder.Redirect.appendTo(logFileJob))
        process.start().waitFor()
    }

}
