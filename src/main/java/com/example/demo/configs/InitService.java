package com.example.demo.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

@Service
public class InitService {
    private static final Logger log = LoggerFactory.getLogger(InitService.class);

    private static final int ThreadNum = 50000;

  //  private static  int mobile = 0;

    @Autowired
    private CommonMqService commonMqService;

    public void generateMultiThread(String mobile){
        commonMqService.sendRobbingMsgV2(String.valueOf(mobile));
       /* try{
            CountDownLatch countDownLatch = new CountDownLatch(1);
            for (int i = 0; i<ThreadNum; i++){
                new Thread(new RunThread(countDownLatch)).start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }*/

    }


  /*  private class RunThread implements Runnable{
        private final CountDownLatch startLatch;

        public RunThread(CountDownLatch startLatch){
            this.startLatch = startLatch;
        }
        public void run() {
            try {
                startLatch.await();
                mobile += 1;
                commonMqService.sendRobbingMsgV2(String.valueOf(mobile));

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }*/
}
