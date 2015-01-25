package com.antonio.android.reproductorvideo;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

/* producto realizado por AntonioBMR inc.*/

public class MainActivity extends Activity {
    private static int REPRODUCIR_VIDEO=1;
    private static int CARGAR_VIDEO=2;
    private VideoView vv;
    private boolean visible;
    private int position;
    private SeekBar seekBar;
    private Button btnPlay,btnPause,btnStop,btnBuscar;
    private LinearLayout botonera;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        vv = (VideoView) findViewById(R.id.vv);
        position=0;
        Intent intent = getIntent();
        Uri uridata=intent.getData();
        if(uridata!=null) {
            try {
                vv.setVideoURI(uridata);
            } catch (Exception e) {
                tostada(e.toString());
            }
        }else{
            Intent i = new Intent (Intent.ACTION_PICK, MediaStore.Video.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(i, CARGAR_VIDEO);
        }
        visible=false;
        seekBar=(SeekBar)this.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBarChangeEvent());

        btnPlay = (Button) this.findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new ClickEvent());

        btnPause = (Button) this.findViewById(R.id.btnPause);
        btnPause.setOnClickListener(new ClickEvent());

        btnStop = (Button) this.findViewById(R.id.btnStop);
        btnStop.setOnClickListener(new ClickEvent());

        btnBuscar=(Button) this.findViewById(R.id.btnBuscar);

        botonera=(LinearLayout)this.findViewById(R.id.botonera);
        botonera.setVisibility(View.INVISIBLE);

        vv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                        if (visible == true) {
                            botonera.setVisibility(View.INVISIBLE);
                            visible = false;
                        } else if (visible == false) {
                            botonera.setVisibility(View.VISIBLE);
                            visible = true;
                        }
                        return true;
                }
        });
        vv.start();
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBar.setMax(vv.getDuration());
                seekBar.postDelayed(onEverySecond, 1000);
            }
        });
    }
    //sincroniza tiempo de reproduccion y seekbar
    private Runnable onEverySecond = new Runnable() {
        @Override
        public void run() {
            if (seekBar != null) {
                seekBar.setProgress(vv.getCurrentPosition());
            }if (vv.isPlaying()) {
                seekBar.postDelayed(onEverySecond, 500);
            }if(vv.isPlaying()!=true){
                seekBar.postDelayed(onEverySecond, 0);
            }
        }
    };

    //para buscar videos.
    public void seleccionar(View view){
        onPause();
        position=0;
        seekBar.setProgress(0);
        vv.seekTo(0);
        Intent i = new Intent (Intent.ACTION_PICK, MediaStore.Video.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(i, CARGAR_VIDEO);
    }


    //evento botones reproduccion video
    class ClickEvent implements View.OnClickListener {

        @Override
        public void onClick(View arg0) {
            if (arg0 == btnPause) {
                onPause();
                if(btnPause.getVisibility()==View.VISIBLE){
                    btnPause.setVisibility(View.INVISIBLE);
                }
                if(btnStop.getVisibility()==View.VISIBLE){
                    btnStop.setVisibility(View.INVISIBLE);

                }

            } else if (arg0 == btnPlay) {
                if(vv.isPlaying()){
                    onPause();
                    if(btnPause.getVisibility()==View.VISIBLE){
                        btnPause.setVisibility(View.INVISIBLE);
                    }
                    if(btnStop.getVisibility()==View.VISIBLE){
                        btnStop.setVisibility(View.INVISIBLE);
                    }
                }else{
                    onResume();
                    if(btnPause.getVisibility()==View.INVISIBLE){
                        btnPause.setVisibility(View.VISIBLE);
                    }
                    if(btnStop.getVisibility()==View.INVISIBLE){
                        btnStop.setVisibility(View.VISIBLE);
                    }

                }

            } else if (arg0 == btnStop) {
                onPause();
                if(btnPause.getVisibility()==View.VISIBLE){
                    btnPause.setVisibility(View.INVISIBLE);
                }
                if(btnStop.getVisibility()==View.VISIBLE){
                    btnStop.setVisibility(View.INVISIBLE);
                }
                position=0;

                seekBar.setProgress(0);
                vv.seekTo(0);
            }
        }
    }
    //cambios al mover la seekbar
    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            this.progress = progress * vv.getDuration()
                    / seekBar.getMax();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            seekBar.setProgress(progress);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            vv.seekTo(progress);
        }
    }
    //metodos propios
    @Override
    protected void onResume() {
        super.onResume();
        try{
            if (vv != null) {
                vv.seekTo(position);
                vv.start();
            }
        }catch (Exception e) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
            if (vv != null) {
                position = vv.getCurrentPosition();
                vv.pause();
            }
        }catch (Exception e) {
        }
    }

    ////recoger resultado intent
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK &&
                requestCode == REPRODUCIR_VIDEO) {
            vv = (VideoView) findViewById(R.id.vv);
            vv.setVideoURI(data.getData());
            vv.start();
        }
        if (resultCode == Activity.RESULT_OK &&
                requestCode == CARGAR_VIDEO) {
            vv = (VideoView) findViewById(R.id.vv);
            vv.setVideoURI(data.getData());
            vv.start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public Toast tostada(String t) {
        Toast toast =
                Toast.makeText(getApplicationContext(),
                        t + "", Toast.LENGTH_SHORT);
        toast.show();
        return toast;
    }

}
