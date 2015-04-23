package scottm.examples;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class GraphicsExamplesActivity extends Activity implements View.OnTouchListener {

    private GraphicsView gv;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        gv = (GraphicsView) findViewById(R.id.graphics_view);
        Log.d("GraphicsView", "gv object: " + gv);
        gv.setOnTouchListener(this);
    }

    public void onResume() {
        super.onResume();
        gv = (GraphicsView) findViewById(R.id.graphics_view);
        Log.d("GraphicsView", "gv object: " + gv);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.d("GraphicsView", "onTouch called.");
        gv.toggleAnimation();
        return true;
    }

    public void onStop() {
        super.onStop();
        gv = (GraphicsView) findViewById(R.id.graphics_view);
        Log.d("GraphicsView", "gv object: " + gv);
    }
}