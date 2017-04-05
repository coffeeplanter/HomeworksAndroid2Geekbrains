package ru.geekbrains.android2.task2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AtomicInteger counter;
    private FloatingActionButton fab;
    private TextView mChronometerLabelTextView, mChronometerTextView, mIntervalLabelTextView1,
            mIntervalTextView1, mIntervalLabelTextView2, mIntervalTextView2;
    private boolean isTimerRunning;
    private Chronometer timer;
    private Messenger messenger5, messenger7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        isTimerRunning = false;

        // Инициализируем вьюхи
        mChronometerLabelTextView = (TextView) findViewById(R.id.chronometer_label);
        mChronometerTextView = (TextView) findViewById(R.id.chronometer);
        mIntervalLabelTextView1 = (TextView) findViewById(R.id.interval_messenger_1_label);
        mIntervalTextView1 = (TextView) findViewById(R.id.interval_messenger_1);
        mIntervalLabelTextView2 = (TextView) findViewById(R.id.interval_messenger_2_label);
        mIntervalTextView2 = (TextView) findViewById(R.id.interval_messenger_2);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        // Инициализируем Асинктаски
        timer = new Chronometer();
        messenger5 = new Messenger(5);
        messenger7 = new Messenger(7);

        // Устанавливаем обработчик кликов
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (isTimerRunning) {
            // Если асинктаски уже запущены, то останавливаем их
            timer.cancel(true);
            messenger5.cancel(true);
            messenger7.cancel(true);
            fab.setImageResource(R.drawable.ic_action_start);
            Snackbar.make(mChronometerLabelTextView, R.string.stop_timer_message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            isTimerRunning = false;
        } else {
            if (timer.isCancelled()) {
                // Если Асинктаски уже запускались, создаём их заново
                timer = new Chronometer();
                messenger5 = new Messenger(5);
                messenger7 = new Messenger(7);
            }
            fab.setImageResource(R.drawable.ic_action_stop);
            Snackbar.make(mChronometerLabelTextView, R.string.start_timer_message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            // Используем класс AtomicInteger как атомарную альтернативу Integer для потоков
            counter = new AtomicInteger();
            // Запускаем Асинктаски с параллельном режиме
            timer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, counter);
            messenger5.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, counter, new AtomicInteger(5));
            messenger7.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, counter, new AtomicInteger(7));
            isTimerRunning = true;
        }
    }

    /**
     * Класс-наследник AsyncTask для секундомера и уведомления других потоков
     */
    private class Chronometer extends AsyncTask<AtomicInteger, Integer, Void> {

        private AtomicInteger counter;

        @Override
        protected void onPreExecute() {
            mChronometerTextView.setText("0");
            mIntervalTextView1.setText("0");
            mIntervalTextView2.setText("0");
        }

        @Override
        protected Void doInBackground(AtomicInteger... params) {
            this.counter = params[0];
            System.out.println("Старт отсчёта!");
            while (!isCancelled()) {
                try {
                    Thread.sleep(1000);
                    synchronized (this.counter) {
                        counter.incrementAndGet();
                        System.out.println("Секунд прошло: " + counter.get());
                        publishProgress(counter.get());
                        counter.notifyAll();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Стоп отсчёта.");
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mChronometerTextView.setText(String.valueOf(values[0]));
        }

    }

    /**
     * Класс-наследник AsyncTask для вывода уведомлений об отметках времени
     */
    private class Messenger extends AsyncTask<AtomicInteger, Integer, Void> {

        private AtomicInteger counter;
        private int step;

        Messenger(int step) {
            this.step = step;
        }

        @Override
        protected void onPreExecute() {
            switch (step) {
                case 5:
                    mIntervalLabelTextView1.setText(getString(R.string.interval_messenger_runtime_label, step));
                    break;
                case 7:
                    mIntervalLabelTextView2.setText(getString(R.string.interval_messenger_runtime_label, step));
                    break;
            }
        }

        @Override
        protected Void doInBackground(AtomicInteger... params) {
            this.counter = params[0];
            this.step = params[1].get();
            System.out.println("Старт слежения, отслеживаемый интервал: " + step);
            while (!isCancelled()) {
                synchronized (this.counter) {
                    try {
                        counter.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if ((counter.get() % step == 0) && (counter.get() != 0)) {
                        System.out.println("Отсечка интервала " + step + " сек.");
                        publishProgress(counter.get());
                    }
                }
            }
            System.out.println("Стоп слежения, отслеживаемый интервал: " + step);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            switch (step) {
                case 5:
                    mIntervalTextView1.setText(String.valueOf(values[0]));
                    break;
                case 7:
                    mIntervalTextView2.setText(String.valueOf(values[0]));
                    break;
                default:
                    mIntervalTextView1.setText("Некорректный интервал");
                    mIntervalTextView2.setText("Некорректный интервал");
            }
        }

    }

}
