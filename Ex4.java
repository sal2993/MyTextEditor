/*
* programmers: Sal Camara and Ali
*
*/
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import java.util.*;
import java.io.*;

public class Ex4 extends Basic
{
  public static void main(String[] args)
  {
    // example: hard-coded location and size of window:
    Ex4 a = new Ex4("Simple Text Editor", 0, 0, 900, 600);

  } // end main
 
//  private ArrayList<String> doc;  // the list of all the strings in the document
//  private int cursorRow; // index in the list of cursor

   private EditableList doc;

  private int cursorCol; // column in doc.get(cursor)

  public Ex4( String title, int ulx, int uly, int pw, int ph )
  {
    super(title,ulx,uly,pw,ph);

    doc = new EditableList();

    String fileName = FileBrowser.chooseFile( true );

    try{
      Scanner input = new Scanner( new File( fileName ) );

      while( input.hasNext() )
      {
        String s = input.nextLine();
        doc.insert( s );
      }

      // get rid of bogus first line 
      // and start at the beginning
      doc.moveToFirst();
      doc.remove();
     
    } // end try
    catch(Exception e)
    {
      System.out.println("something went wrong");
      e.printStackTrace();
      System.exit(1);
    } // end catch

    setBackgroundColor( new Color( 0, 0, 0 ) );
    cameras.add( new Camera( 10, 30, 
                             800, 500, 
                             0, 50, 0, 50,
                             Color.white ) );
 
    cursorCol = 0;

    super.start();
  } // end public

  private static double leftMargin = 5;
  private static double lineHeight = 1.8;
  private static double symWidth = 0.8;
  private static String[] instructions = {"ctrl-q: save and quit",
        "ctrl-f: move cursor to beginning of line ",
        "ctrl-l: move cursor to end of line ",
        "ctrl-d: delete entire line ",
        "ctrl-e: erase contents of current line ",
        "ctrl-c: combine cursor line with next line",
        "ctrl-t: move cursor to top of file",
        "ctrl-b: move cursor to bottom of file"
        };


  public void step()
  {
    Camera cam;

    cam = cameras.get(0);
    cam.activate();
    // make the camera use the desired font
    cameras.get(0).setFont( new Font( Font.MONOSPACED, Font.PLAIN, 18 ) );
    double y = 48;
    if ( doc.current() == 0 )
    {
        for (int i = 0; i < instructions.length; i++)
        {
            cam.setColor( Color.blue );
            cam.drawText(instructions[i], 0, y );
            y -= lineHeight; 
        }
    }
    y = 48;
    // draw 12 lines before and after the cursorRow
    for( int k=-12; k<=12; k++ )
    {
      // ***s.c. change 4.26.15
      if( 0<=doc.current()+k && doc.current()+k < doc.size() )
      {
        // draw line number
        cam.setColor( Color.green );
        // ***s.c. change 4.26.15
        cam.drawText( (doc.current()+k) + ": ", 0, y );

        cam.setColor( Color.black );

        String line;
        if (doc.get( k ) != null)
        {
            line = doc.get( k );
        
  // Looks like you need to use back and forward, carefully,
  // maybe current()

        // draw line one symbol at a time
            for( int j=0; j<line.length(); j++ )
              cam.drawText( ""+line.charAt(j), leftMargin+symWidth*j, y );

            if( k==0 )
            {
              // draw the vertical cursor
              if( getStepNumber() % 4 == 0 )
                cam.setColor( Color.blue );
              else
                cam.setColor( Color.red );
              double x = leftMargin + cursorCol*symWidth;
              cam.fillRect( x-0.2, y-0.5, 0.2, 2.25 );
            }
        } 

      }// cursorRow+k is valid

      y -= lineHeight;

    }// draw 25 lines

  }// step method

  public void keyTyped( KeyEvent e )
  {
    char key = e.getKeyChar();

System.out.println("key typed coerced to int: " + ((int) key) );

    if( ' '<=key && key<='~' )
    {// insert printable symbol at the cursor
      String s = doc.get( 0 ); // *** s.c. changed 4.26.15
      if( cursorCol==0 )
      {
        s = key + s;
      }
      else
      {
        s = s.substring( 0, cursorCol ) + key + s.substring( cursorCol );
      }
      doc.set( s );
      cursorCol++;
    }
    else if( key == 'q' - 'a' + 1 )
    {// ctrl-q --- save and quit
      save();
      System.exit(0);
    }
    else if( key == 'f' - 'a' + 1 )
    {// ctrl-f   --- put column cursor on position 0 of current line
      cursorCol = 0;
    }
    else if( key == 'l' - 'a' + 1 )
    {// ctrl-l   --- put column cursor on last position of current line
      cursorCol = doc.get( 0 ).length(); // ***s.c. changed 4.26.15
    }
    else if( key == 'd' - 'a' + 1 )
    {// ctrl-d   --- delete entire current line
     // ***s.c. deletes entire line and moves cursor to cursor.prev
      if( doc.size() > 1 )
      {
        doc.remove();    // ***s.c. changed 4.26.15
        cursorCol = doc.get(0).length(); // moves cursor to end of prev line
      }
    }
    else if( key == 'e' - 'a' + 1 )
    {// ctrl-e  --- erase current line
     // ***s.c. deletes all the characters of a line. so cursor.data is empty
      doc.set( "" );
    }
    else if( key == 'c' - 'a' + 1 )
    {// ctrl-c   --- combine current line and next, if there is a next line
     // ***s.c. changes 4.26.15 Note: does not work as intended. no way to
     // remove a node other then the one cursor is pointing to
      if( doc.current() < doc.size()-1 )
      {
        String s1 = doc.get( 0 );
        String s2 = doc.get( 1 );
        doc.set( s1+s2 );
        
        doc.forward();
        doc.remove();
      }
    }
    else if( key == 't' - 'a' + 1)
    {// ctrl-t   --- move cursor to top of file
        doc.moveToFirst();
    }
    else if( key == 'b' - 'a' + 1)
    {// ctrl-b   --- move cursor to bottom of file
        doc.moveToLast();
    }
    
    // always make sure changes didn't make cursorCol too far over
    cursorCol = Math.min( cursorCol, doc.get( 0 ).length() );
  }

  public void keyPressed( KeyEvent e )
  {
    int code = e.getKeyCode();
 
System.out.println("key pressed: " + code );
System.out.println("n: " + doc.size() + " current: " + doc.current());

    if( code == KeyEvent.VK_DOWN && doc.forward() )
    {
      // ***s.c. changed 4.26.15
    }
    else if( code == KeyEvent.VK_UP && doc.back() )
    {
      // ***s.c. changed 4.26.15
    }
    else if( code == KeyEvent.VK_LEFT )
    {
      if( cursorCol > 0 )
      cursorCol--;
    }
    else if( code == KeyEvent.VK_RIGHT )
    {
      if( cursorCol < doc.get( 0 ).length() )
        cursorCol++;
    }
    else if( code == KeyEvent.VK_DELETE ) 
    {
      // ***s.c. changed 4.26.15
      String s = doc.get( 0 );
      if( cursorCol < s.length() )
      {
        s = s.substring(0,cursorCol) + s.substring(cursorCol+1);
      // ***s.c. changed 4.26.15
        doc.set( s );
      }
      else if( s.length() == 0 )
      {// kill the empty line
        doc.remove();
      }
    }
    else if( code == KeyEvent.VK_BACK_SPACE )
    {// ***s.c. changed 4.26.15
      // get the cursor string
      String s = doc.get( 0 );

      // if the cursor is at colum 0 and there is a valid string at cursor.prev
      if( cursorCol == 0 )
      {// join the line above with this one
        // move the cursorCol to correct position /w a check for null
        if (doc.get(-1) != null)
        {
            cursorCol = doc.get( -1 ).length();
            // delete that node
            doc.remove();
            // append the previous string (now deleted) to the current string
            doc.set( doc.get( 0 ) + s );
        }
      }
      else if( cursorCol > 0 )
      {
        s = s.substring(0,cursorCol-1) + s.substring(cursorCol);
        // ***s.c. changed 4.26.15
        doc.set( s );
        cursorCol--;
      }
      else if( s.length() == 0 )
      {// kill the empty line
        doc.remove();
      }
    }
    // make a new line
    else if( code == KeyEvent.VK_ENTER )
    {
      // ***s.c. changed 4.26.15
      String s = doc.get( 0 );
      String first = s.substring( 0, cursorCol );
      String second = s.substring( cursorCol );
      doc.set( first );
      doc.insert( second );
      cursorCol = 0;
    }

    // always make sure changes didn't make cursorCol too far over
    cursorCol = Math.min( cursorCol, doc.get( 0 ).length() );

  }

  private void save()
  {
    String fileName = FileBrowser.chooseFile( false );
    doc.moveToFirst();
    try{
      PrintWriter output = new PrintWriter( new File( fileName ) );
      for( int k=0; k<doc.size(); k++ )
      {
        output.println( doc.get( k ) );
      }
      output.close();
    }
    catch(Exception e)
    {
      System.out.println("Something went wrong with saving.  Sorry.");
    }
  }

}
