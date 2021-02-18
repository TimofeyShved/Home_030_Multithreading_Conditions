package com.Multithreading;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    //Conditions
    static Lock lock =new ReentrantLock();
    static Condition condition = lock.newCondition();
    static int account = 0;

    public static void main(String[] args) throws Exception {
        //Conditions
        new AccountPlus().start();
        new AccountMinus().start();

        //Callable and FutureTask
        Callable<Integer> callable = new MyCallable();  // создаем код, который должен быть в потоке
        FutureTask futureTask = new FutureTask(callable); // запихиваем наш код в futureTask
        new Thread(futureTask).start();         // создаем нужный нам поток и запускаем его, типа futureTask
        System.out.println(futureTask.get()); // возвращает результат
    }

    //Callable and FutureTask
    static class MyCallable implements Callable{
        @Override
        public Object call() throws Exception { // возвращаемый поток
            int j=0;
            for (int i=0;i<10;i++) {
                j++;
                Thread.sleep(100);
            }
            return j;
        }
    }

    //Conditions
    static class AccountPlus extends Thread{
        @Override
        public void run() {
            lock.lock();
            account+=10;
            condition.signal(); // посылает сигал
            lock.unlock();
        }
    }

    //Conditions
    static class AccountMinus extends Thread{
        @Override
        public void run() {
            if(account<10){
                try {
                    lock.lock();
                    System.out.println("account = "+account);
                    condition.await();                          // ждем пока не придёт сигнал
                    System.out.println("account = "+account);
                    lock.unlock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            account-=10;
            System.out.println("account at the end = "+account);
        }
    }
}
