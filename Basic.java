/*  basic application to be extended
*/

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;

public class Basic extends JFrame implements Runnable, KeyListener,
                                             MouseListener, 
                                             MouseMotionListener,
                                             WindowListener
{
  protected static int FPS = 50; // desired frames per second

  private int stepNumber;

  // part of the basic framework
  private Thread theThread;	//to control the animation

  private boolean waiting;

  protected int pixelWidth, pixelHeight; // dimensions of window in pixels
  private int ulcX, ulcY;  // location of upper left corner of window on screen

  private Color backgroundColor;  // background color of entire window

  private double mouseX, mouseY;  // maintain correct mouse location
  private int whichCamera;  // maintain which camera's pixel grid region mouse is in

  private Graphics dbg;  
  private Image dbImage = null;

  protected ArrayList<Camera> cameras;

  public Basic( String title, int ulx, int uly, int pw, int ph ) 
  {
    super( title );  // call JFrame

    ulcX = ulx;  ulcY = uly;
    pixelWidth = pw;  pixelHeight = ph;

    setBounds(ulcX, ulcY, pixelWidth, pixelHeight );
    setResizable( false );

    addKeyListener(this);
    // setFocusable(true);
//    requestFocus();
    addMouseListener(this);
    addMouseMotionListener(this);
    addWindowListener(this);

    cameras = new ArrayList<Camera>();

    stepNumber = 0;

    // force mouse to be in center of window at start
    mouseX = pixelWidth/2;
    mouseY = pixelHeight/2;

  }
  
  public void start()
  {
    setVisible(true);
    
    // physically draw the mouse cursor where we want it
    try{ 
      Robot rob = new Robot();
      rob.mouseMove( ulcX+pixelWidth/2, ulcY+pixelHeight/2 );
    }catch(Exception e){}

    if (theThread == null)	
      theThread = new Thread(this);
    theThread.start();
  }
  
  public void run()
  {
    while (true)
    {
      stepNumber++;

      long startTime = System.nanoTime();

      render();
      paintScreen();

      long stopTime = System.nanoTime();

      double elapsed = (stopTime-startTime)/1000000.0;  // milliseconds

      int waitTime = 1000/FPS - (int) Math.round( elapsed );
      if( waitTime < 1 )
        waitTime = 1;

      try {
        Thread.sleep(waitTime);
      } 
      catch (InterruptedException ie) 
      {System.err.println("OOPS");}
    }
  }
  
  private void render()
  {
    if( dbImage == null )
    {// create the buffer
      dbImage = createImage( pixelWidth, pixelHeight );
      if( dbImage == null )
      {
        System.out.println("dbImage is null???");
        return;
      }
      else
      {// dbg is created, tell Camera
        dbg = dbImage.getGraphics();
        Camera.setGraphicsContext( dbg );
      }
    }

    // clear the background of entire window
    dbg.setColor( backgroundColor );
    dbg.fillRect(0,0,pixelWidth,pixelHeight);

    // give app a chance to update its instance variables
    // and then draw stuff to dbg

    step();
  }

  private void paintScreen()
  {
    Graphics gr;
    try{
      gr = this.getGraphics();
      if( gr != null && dbImage != null )
        gr.drawImage(dbImage,0,0,null);
      Toolkit.getDefaultToolkit().sync();  // maybe not needed?
      gr.dispose();
    }
    catch(Exception e)
    {
      System.out.println("graphics context error" + e );
      System.exit(1);
    }
  }

  // override this method with code to display stuff 
  // and update app instance variables
  public void step()
  {
    System.out.println("Step number: " + getStepNumber() );
  }

  public int getStepNumber()
  {
    return stepNumber;
  }
  
  // implement KeyListener:
  // ******************************************************************
  public void keyPressed(KeyEvent e){}
  public void keyReleased(KeyEvent e){}
  public void keyTyped(KeyEvent e){}

  // implement MouseListener:
  // ******************************************************************
  public void mouseClicked(MouseEvent e)
  {
    updateMouse(e);
  }
  public void mouseEntered(MouseEvent e)
  {
    updateMouse(e);
  }
  public void mouseExited(MouseEvent e)
  {
    updateMouse(e);
  }
  public void mousePressed(MouseEvent e)
  {
    updateMouse(e);
  }
  public void mouseReleased(MouseEvent e)
  {
    updateMouse(e);
  }
  
  // implement MouseMotionListener:
  // ******************************************************************
  public void mouseDragged(MouseEvent e)
  {
    updateMouse(e);
  }
  public void mouseMoved(MouseEvent e)
  {
    updateMouse(e);
  }

  private void updateMouse( MouseEvent e )
  {
    int ix = e.getX(), iy = e.getY();

    // determine which camera this mouse event hits
    // and compute mouseX, mouseY in that camera's graph paper coords
    
    whichCamera = -1;
    Camera ac=null, temp;

    for( int k=0; k<cameras.size(); k++ )
    {
      temp = cameras.get(k);
      if( temp.hits(ix,iy) )
      {
        ac = temp;
        whichCamera = k;
      }
    }

    if( whichCamera == -1 )
    {
      mouseX=0;  mouseY=0; 
      whichCamera = -1;
    }
    else
    {// mouse event happened in an active camera, update coords for that cam
      mouseX = ac.invMapX( ix );
      mouseY = ac.invMapY( iy );
    }

  }

  // -----------------------------------------------------------------
  // These can be called, but don't override

  protected double getMouseX()
  {
    return mouseX;
  }

  protected double getMouseY()
  {
    return mouseY;
  }

  protected int getMouseCamera()
  {
    return whichCamera;
  }

  // set background color of entire window
  protected void setBackgroundColor( Color color )
  {
    backgroundColor = color;
  }
  
  // implement WindowListener:
  // ******************************************************************^M
  public void windowActivated(WindowEvent e){}
  public void windowClosed(WindowEvent e){}
  public void windowClosing(WindowEvent e)
  { 
    System.exit(0);
  } // end of windowClosing()
  public void windowDeactivated(WindowEvent e){}
  public void windowDeiconified(WindowEvent e){}
  public void windowIconified(WindowEvent e){}
  public void windowOpened(WindowEvent e){}

}
