package com.example.sudoku1;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Random;
import java.util.ArrayList;

public class MainActivity extends Activity implements OnFocusChangeListener {
	
	public ArrayList<ArrayList<Integer>> Available;
	private int[][] table;
	private int[][] userSolution;
	private static int[] levels;
	private int levelSelected;
	private static ArrayList<Integer> temp;
	private boolean save=false;
	private int hintI, hintJ;
	//SharedPreferences savedGame;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Toast toast = Toast.makeText(getApplicationContext(), "Please wait while the app generates your puzzle!", Toast.LENGTH_LONG);
		//toast.setGravity(Gravity.CENTER,0, 0);
		//toast.setView(new View(getApplicationContext()));
		//toast.show();
		
		//if(true) return;
		
		//savedGame = PreferenceManager.getDefaultSharedPreferences(this);
		/*try {
			if(savedInstanceState.getBoolean("saved")) 
				{
				
				
				//savedInstanceState = savedInstanceState.getBundle("savedGane");
				return;
				}
		}
		catch(Exception e) {}*/
		
		//if(savedInstanceState.getBoolean("saved")) return;
		
		//temporarily hidden
		final Button btn = (Button)findViewById(R.id.button5);
		btn.setVisibility(Button.INVISIBLE);
		
		table=new int[9][9];
		levels = new int[3];
		levels[0] = 5; // easy: 45 total 9x5
		levels[1] = 4; // medium: 34 total 9x4 -2
		levels[2] = 3; // evil: 27 total 9x3
		
		userSolution = new int[9][9];
		
		//change this as per user's request
		levelSelected=1;
		
		temp = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9));
		
		
		final LinearLayout t = (LinearLayout)findViewById(R.id.LinearLayout1);		
		for(int i=1; i<=9; i++)
		{
			for(int j=1; j<=9; j++)
			{
				final EditText e = (EditText)t.findViewWithTag("t" + i + j);
				e.setText("");
				e.setOnFocusChangeListener(this);
				if(((j==4 || j==5 || j==6) && (i==4 || i==5 || i==6)) || (j==1 || j==2 || j==3 || j==7 || j==8 || j==9) && (i==1 || i==2 || i==3 || i==7 || i==8 || i==9))
					e.setBackgroundResource(R.drawable.cell_region);
				else
					e.setBackgroundResource(R.drawable.cell_shape);
			}
		}
		
		
		showPuzzle();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	private void showPuzzle()
	{
		generateGrid();
		//clearAllSquares();
		final LinearLayout t = (LinearLayout)findViewById(R.id.LinearLayout1);
		
		for(int i=0; i<9; i++)
		{
			java.util.Collections.shuffle(temp);
			for(int j=0; j<levels[levelSelected]; j++)
			{
				int y = temp.get(j).intValue();
				final EditText e = (EditText)t.findViewWithTag("t" + String.valueOf(i+1) + String.valueOf(y));
				e.setText(String.valueOf(table[i][y-1]));
				userSolution[i][y-1] = table[i][y-1];
				e.setEnabled(false);
			}
		}
	}
	
	public void addSquare(Square s)
	{
		table[s.Across-1][s.Down-1]=s.Value;
	}
	
	public void displayFullSudoku()
	{
		final LinearLayout t = (LinearLayout)findViewById(R.id.LinearLayout1);
		for(int i=0; i<9; i++)
		{
			for(int j=0;j<9;j++)
			{
				final EditText e = (EditText)t.findViewWithTag("t" + String.valueOf(i+1) + String.valueOf(j+1));
				e.setText(String.valueOf(table[i][j]));
			}
		}
	}
	
	public void clearAllSquares()
	{
		final LinearLayout t = (LinearLayout)findViewById(R.id.LinearLayout1);		
		for(int i=1; i<=9; i++)
		{
			for(int j=1; j<=9; j++)
			{
				final EditText e = (EditText)t.findViewWithTag("t" + i + j);
				e.setText("");
				//if(((j==4 || j==5 || j==6) && (i==4 || i==5 || i==6)) || (j==1 || j==2 || j==3 || j==7 || j==8 || j==9) && (i==1 || i==2 || i==3 || i==7 || i==8 || i==9))
				//	e.setBackgroundResource(R.drawable.cell_region);
				//else
				//	e.setBackgroundResource(R.drawable.cell_shape);
			}
		}
	}
	
	
	public void generateGrid()
	{
		//clearAllSquares();
		
		ArrayList<Square> Squares = new ArrayList<Square>(81);
		Available = new ArrayList<ArrayList<Integer>>(81);
		int c = 0;
		
		for(int x =0; x < 81; x++)
		{
			Available.add(x, new ArrayList<Integer>());
			for(int i=1; i<=9; i++)
				Available.get(x).add(i);
		}
		
		do
		{
			if(Available.get(c).size() != 0)
			{
				int i = GetRan(0, Available.get(c).size());
				int z = Available.get(c).get(i);
				if(!Conflicts(Squares, Item(c,z)))
				{
					Squares.add(c, Item(c,z));
					Available.get(c).remove(i);
					c++;
				}
				else
				{
					Available.get(c).remove(i);
				}
			}
			else
			{
				for(int y = 1; y<=9; y++)
					Available.get(c).add(y);
				Squares.set(c-1, null);
				c--;
			}
		}while(c <81);
		
		for(int j=0; j<=80; j++)
		{
			addSquare(Squares.get(j));
		}
	}

	private int GetRan(int lower, int upper)
	{
		return (new Random()).nextInt(upper) + lower;	
	}
	
	private boolean Conflicts(ArrayList<Square> CurrentValues, Square test)
	{
		for(Square s: CurrentValues)
		{
			if( s!=null && ((s.Across!=0 && s.Across==test.Across) || (s.Down!=0 && s.Down==test.Down) || (s.Region!=0 && s.Region==test.Region)) )
			{
				if(s.Value==test.Value) return true;
			}
		}
		return false;
	}
	
	private Square Item(int n, int v)
	{
		n++;
		Square item = new Square();
		item.Across = GetAcrossFromNumber(n);
		item.Down = GetDownFromNumber(n);
		item.Region = GetRegionFromNumber(n);
		item.Value = v;
		item.Index = n-1;
		return item;
	}
	
	private int GetAcrossFromNumber(int n)
	{
		int k=n%9;
		if(k==0) return 9;
		else return k;
	}
	
	private int GetDownFromNumber(int n)
	{
		int k;
		if(GetAcrossFromNumber(n)==9) k=n/9;
		else k = n/9 +1;
		return k;
	}
	
	private int GetRegionFromNumber(int n)
	{
		int k=0, a=GetAcrossFromNumber(n), d=GetDownFromNumber(n);
		
		if(1<=a && a<4 && 1<=d && d<4) k=1;
		else if(4<=a && a<7 && 1<=d && d<4) k=2;
		else if(7<=a && a<10 && 1<=d && d<4) k=3;
		else if(1<=a && a<4 && 4<=d && d<7) k=4;
		else if(4 <= a && a < 7 && 4 <= d && d < 7) k = 5;
		else if(7 <= a && a < 10 && 4 <= d && d < 7) k = 6;
		else if(1 <= a && a < 4 && 7 <= d && d < 10) k = 7;
		else if(4 <= a && a < 7 && 7 <= d && d < 10) k = 8;
		else if(7 <= a && a < 10 && 7 <= d && d < 10) k = 9;
		
		return k;
	}


	class Square
	{
		public int Across;
		public int Down;
		public int Region;
		public int Value;
		public int Index;
	}
	
	
	public void btnClear(View view)
	{
		final LinearLayout t = (LinearLayout)findViewById(R.id.LinearLayout1);
		for(int i=0; i<9; i++)
		{
			for(int j=0;j<9;j++)
			{
				final EditText e = (EditText)t.findViewWithTag("t" + String.valueOf(i+1) + String.valueOf(j+1));
				if(e.isEnabled())
				{
					e.setText("");
					userSolution[i][j]=0;
				}
			}
		}
	}
	
	
	public void btnSave(View view)
	{
		
		save=true;
		//SharedPreferences.Editor editor = savedGame.edit();
		//editor.put
	}
	
	public void btnGo(View view)
	{
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_LONG;
		CharSequence text="Hmm, the computer can't decide!";

		if(Arrays.deepEquals(userSolution, table)){
			text = "Well done!";
		}
		else{
			text = "Oops, your solution is wrong.";
			
			final LinearLayout t = (LinearLayout)findViewById(R.id.LinearLayout1);
			for(int i=0; i<9; i++)
			{
				for(int j=0;j<9;j++)
				{
					final EditText e = (EditText)t.findViewWithTag("t" + String.valueOf(i+1) + String.valueOf(j+1));
					if(userSolution[i][j] != table[i][j])
					{
						e.setBackgroundResource(R.drawable.incorrect);
						e.setText(String.valueOf(table[i][j]));
					}
				}
			}
		}
		
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		
		try {
			final TableLayout buttons = (TableLayout)findViewById(R.id.tableLayout2);
			buttons.removeAllViews();
		}finally{}
	}
	
	
	public void btnShowMe(View view)
	{
		displayFullSudoku();
		
		try {
			final TableLayout buttons = (TableLayout)findViewById(R.id.tableLayout2);
			buttons.removeAllViews();
		}finally{}
	}
	
	
	public void btnHint(View view)
	{
		boolean hintGiven=false;
		for(int i=0; i<9; i++)
		{
			for(int j=0; j<9; j++)
			{
				final LinearLayout t = (LinearLayout)findViewById(R.id.LinearLayout1);
				final EditText e = (EditText)t.findViewWithTag("t" + String.valueOf(i+1) + String.valueOf(j+1));
				if(e.getText().toString().equals("") || userSolution[i][j]!=table[i][j]) {
					e.setText(String.valueOf(table[i][j]));
					userSolution[i][j]=table[i][j];
					e.setEnabled(false);
					hintGiven=true;
					break;
				}
			}
			if(hintGiven) break;
		}
	}
	
	/*@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  super.onSaveInstanceState(savedInstanceState);
	  // Save UI state changes to the savedInstanceState.
	  // This bundle will be passed to onCreate if the process is
	  // killed and restarted.
	  
	  if(save) {
		  savedInstanceState.putBoolean("saved", true);
		  //savedInstanceState.putAll(savedInstanceState);
		  //savedInstanceState.putBundle("savedGame", savedInstanceState);
		  //savedInstanceState.put
	  }
	  // etc.
	}*/
	

	@Override
    public void onFocusChange(View v, boolean hasFocus) {
		
		EditText bawx = (EditText)v;
		String bawxTag = bawx.getTag().toString();
		int i=Character.getNumericValue(bawxTag.charAt(1)), j=Character.getNumericValue(bawxTag.charAt(2));
		try {
			userSolution[i-1][j-1] = bawx.getText().equals("")?0:Integer.parseInt(bawx.getText().toString());
		}
		catch(NumberFormatException e) {}
		final CheckBox c = (CheckBox)findViewById(R.id.checkBox1);
		if(c.isChecked() && !bawx.getText().equals(""))
			bawx.setEnabled(false);
    }
	
	
}

