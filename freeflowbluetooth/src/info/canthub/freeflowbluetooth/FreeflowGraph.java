package info.canthub.freeflowbluetooth;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class FreeflowGraph extends View {

	private ArrayList<Paint> palette;
	private ArrayList<ArrayList<Float>> alldata;
	
	public FreeflowGraph(Context context, AttributeSet attrs) {
		super(context, attrs);
		palette = new ArrayList<Paint>();
		alldata = new ArrayList<ArrayList<Float>>();
		for (int i=0; i<20; i++) {
			Paint p = new Paint();
			p.setStyle(Paint.Style.FILL);
			p.setARGB(255,
					(int)(128+127*Math.sin(i*4313.213)),
					(int)(128+127*Math.sin(i*8432.443+12.22)),
					(int)(128+127*Math.sin(i*7732.017+47.17)));
			palette.add(p);
		}
	}
	
	public ArrayList<Float> giveData(String data) {
		ArrayList<Float> newdata = new ArrayList<Float>();
		for (String val : Arrays.asList(data.split("\\s*,\\s*"))) {
			newdata.add(Float.parseFloat(val));
		}
		alldata.add(newdata);
		if (alldata.size() > getWidth()/4) alldata.remove(0);
		invalidate();
		return newdata;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(0);
		int y = getHeight();
		
		for (int i=0; i<alldata.size(); i++) {
			for (int j=0; j<alldata.get(i).size(); j++)
				canvas.drawCircle(i*4, alldata.get(i).get(j)*y, 2, palette.get(j));
		}
	}
}
