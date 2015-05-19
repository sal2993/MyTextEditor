/*
* Programmers: Sal Camara and Ali Hamodi
*
*
*/
public class EditableList
{
  private DLLNode head;   // points to the dummy head node
  private DLLNode tail;   // points to the dummy tail node
  private int n;          // number of actual items

  private DLLNode cursor; // points to the current item
  private int current;    // position of the current item

  public EditableList()
  {
    head = new DLLNode( "if you see this, you are doomed" ); 
    tail = new DLLNode( "Huh???" );
    n = 1;
    
    cursor =  new DLLNode( "" );

    current = 0;

    head.next = cursor;
    cursor.prev = head;
    cursor.next = tail;
    tail.prev = cursor;
  }

  // insert a new item 
  // immediately after the
  // current item
  public void insert( String s )
  {
    // partially start
    DLLNode temp = new DLLNode( s );
    temp.prev = cursor;
    temp.next = cursor.next;

    // nodes point to temp
    cursor.next.prev = temp;
    cursor.next = temp;
    
    // change the fields and move the cursor
    n++;
    current++;
    cursor = cursor.next;
	
  }
  
  // remove the current item
  // (if only one real item, refuse)
  public void remove()
  {
    // as long as a node remains (other then head/tail)
    if( n > 1 )
    {
        if ( current > 0)
        {
            // remove cursor node by making adjacent nodes point to each other
            DLLNode temp = cursor.prev; // node to make code bit simpler
            cursor.next.prev = temp;    // make next node's prev point to temp
            temp.next = cursor.next;

            // make cursor point to its previous node
            cursor = temp;
            // update fields
            current--;
            n--;
            temp = null;    // for garbage collection
            return; // make sure else-if doesnt run
        }
        else if (current == 0)
        {
            head.next = cursor.next;
            cursor.next.prev = head;
            cursor = cursor.next;
            n--;
            return;
        }
    }
  }
 
  // return the data item stored
  // in the node offset hops from
  // the cursor node, if offset is
  // valid, otherwise return null
  public String get( int offset )
  {
    // if offset is 0 return the cursor data
    if (offset == 0)
    {
        return cursor.data;
    }

    DLLNode temp = cursor; // temp ptr to node to traverse offset steps
    // if offset is greater then 0 hop offset number of times
    if (offset > 0)
    {
        for (int i = 0; i < offset; i++)
        {
            temp = temp.next;
            if (temp == tail || temp == null)
            {
                return null;
            }
        }
        return temp.data;
    }
    // if offset is less then 0 hop offset number of times
    else
    {
        for (int i = 0; i > offset; i--)
        {
            temp = temp.prev;
            if (temp == head || temp == null)
            {
                return null;
            }
        }
        return temp.data;
    }
  }

  // replace the current data by s
  public void set( String s )
  {
    cursor.data = s;
  }

  // if not on first real node,
  // move prev and return true,
  // else don't move and return false
  public boolean back()
  {
    if( cursor.prev != head )
    {
      cursor = cursor.prev;
      current--;
      return true;
    }
    else
      return false;
  }

  // if not on last real node,
  // move next and return true,
  // else don't move and return false
  public boolean forward()
  {
    if (cursor.next != tail)
    {
        cursor = cursor.next;
        current++;
        return true;
    }
    else
        return false;
  }
  
  public int size()
  {
    return n;
  }

  public int current()
  {
    return current;
  }
  
  // this method only moves to first line of the 
  // text editor.
  public void moveToFirst()
  {
    cursor = head.next;
    current = 0;
  }

  // make last actual item current
  public void moveToLast()
  {
    cursor = tail.prev;
    current = n-1;
  }

}
