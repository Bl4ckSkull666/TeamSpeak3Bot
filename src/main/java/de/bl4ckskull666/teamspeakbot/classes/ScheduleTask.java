package de.bl4ckskull666.teamspeakbot.classes;

public class ScheduleTask {
    private boolean _isRun = false;
    private boolean _cancel = false;
    private Thread _thread = null;

    public ScheduleTask(Runnable r) {
        _thread = new Thread(r);
        _thread.start();
    }

    public ScheduleTask(Runnable r, long delay) {
        _thread = new Thread(new runDelayTask(r, delay));
        _thread.start();
    }

    public ScheduleTask(Runnable r, long delay, long repeat) {
        Thread t = new Thread(new runRepeatTask(r, delay, repeat));
        t.start();
    }

    private class runDelayTask implements Runnable {
        private final Runnable _run;
        private final long _delay;
        public runDelayTask(Runnable r, long delay) {
            _run = r;
            _delay = delay;
        }

        public void run() {
            _isRun = true;
            long i = 0;
            while (!_cancel) {
                try {
                    Thread.sleep(100l);
                    i += 100;
                    if (i >= _delay)
                        break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if(_cancel)
                return;

            _run.run();
        }
    }

    private class runRepeatTask implements Runnable {
        private final Runnable _run;
        private final long _delay;
        private final long _repeat;
        public runRepeatTask(Runnable r, long delay, long repeat) {
            _run = r;
            _delay = delay;
            _repeat = repeat;
        }

        public void run() {
            _isRun = true;
            long i = 0;
            while (!_cancel) {
                try {
                    Thread.sleep(100l);
                    i += 100;
                    if (i >= _delay)
                        break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if(_cancel)
                return;

            _run.run();
            _thread = new Thread(new runRepeatTask(_run, _repeat, _repeat));
            _thread.start();
        }
    }
}
