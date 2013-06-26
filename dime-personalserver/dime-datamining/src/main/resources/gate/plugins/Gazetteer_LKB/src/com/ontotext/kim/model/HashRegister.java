package com.ontotext.kim.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.collections.comparators.NullComparator;

import com.ontotext.kim.util.KimLogs;

/**
 * This class implements a hash-code indexed storage for objects. It resembles
 * the <code>HashSet</code> class with these substantial differences:<br>
 * - The hash-code value is supplied externally
 * - Multiple objects can be stored with equal hash-codes. They are grouped
 * and stored in packages. These packages are implemented as the internal
 * class <code>HashElement</code>.<br>
 * - Stored objects are retrieved by packages through their hash-code value.<br>
 * - If stored objects implement <code>Comparable</code> they are sorted in the
 * packages and are returned as a sorted array. This allows binary search within
 * the result.<br>
 * <br>
 * The implementation of the storage structure as also the resize logic is
 * simplified intentionally. By design it is intended to support only operations
 * addition, search and retrieval (not removal).
 * 
 * @author danko
 */
public class HashRegister implements Serializable {
  private static final long serialVersionUID = 2442L;

  /**
   * Determines the step for extension of the array field
   * <code>HashElement.elementHolder</code>
   */
  protected static int subRegIncrement = 2;


  /**
   * Determines the initial size of the main-register. A better distribution
   * is achieved if it is a prime number. 
   */
  protected static int initialSize = 1021;
  /**
   * Determines the maximal percent of filling of the main-register
   * after which the HashRegister is expanded by doubling its size.
   */
  protected int maxFillPerc = 75;


  /**
   * A container class that holds all of the stored objects with 
   * same hash-code value. If the Objects implement
   * <code>Comparable</code> they are sorted in the packages and
   * are returned as a sorted array. This allows binary search in
   * the result.
   * 
   * @author danko
   *
   */
  public static class HashElement implements Serializable {
    private static final long serialVersionUID = 2442L;

    public final int subRegHash;
    public Serializable elementHolder = null;

    public HashElement (int hashCode) {
      subRegHash = hashCode;
    }

    /**
     * Adds new element to the container. The added element must have
     * the same hash-code value as the container. This must be verified
     * outside this class.
     * 
     * @param element - the added element
     */
    public byte add(Serializable element) {
      byte elemCountChange = add12(element); 
      if (elemCountChange < 0) {
        if (element instanceof Comparable)
          elemCountChange = addSorted((Comparable) element);
        else
          elemCountChange = addUnsorted(element);
      }

      return elemCountChange;
    }
    /** Method adds new element to the container in unsorted manner.
     * The method is called only if the container already have 2 
     * elements stored in. 
     * @param element - the element to store
     * @return number of elements stored: 1 - if stored successfully;
     * 0 - element already existed and no duplicates are allowed;
     */
    private byte addUnsorted(Object element) {
      int existing = Arrays.asList(elementHolder).indexOf(element);
      if (existing >= 0) {
        return 0; // ignore the new one and retain the old one
      }

      Object[] newSubReg;
      Object[] oldSubReg = ((Object[])elementHolder);
      int addPosition = oldSubReg.length;
      while (oldSubReg[addPosition - 1] == null)
        addPosition--;
      if (addPosition == oldSubReg.length) {
        newSubReg = new Object[oldSubReg.length + subRegIncrement];
        for (int i=0; i < oldSubReg.length; i++)
          newSubReg[i] = oldSubReg[i];
      }
      else
        newSubReg = oldSubReg;
      newSubReg[addPosition] = element;
      elementHolder = newSubReg;
      return 1;
    }

    private static final Comparator<Comparable> nsc = new NullComparator(true);

    /** Method adds new element to the container in sorted manner.
     * The method is called only if the container already have 2 
     * elements stored in. 
     * @param element - the element to store
     * @return number of elements stored: 1 - if stored successfully;
     * 0 - element already existed and no duplicates are allowed;
     */
    private byte addSorted(Comparable element) {
      Comparable[] newSubReg;
      Comparable[] oldSubReg = ((Comparable[])elementHolder);
      int addPosition = Arrays.binarySearch( oldSubReg, element, nsc);
      if (addPosition >= 0) {
        return 0; // ignore the new one and retain the old one
      }

      addPosition = - addPosition - 1;
      if (oldSubReg[oldSubReg.length-1] != null) {
        newSubReg = new Comparable[oldSubReg.length + subRegIncrement];
        for (int i = 0; i < oldSubReg.length; i++) {
          if (i < addPosition)
            newSubReg[i] = oldSubReg[i];
          else
            newSubReg[i+1] = oldSubReg[i];
        }
      }
      else {
        for (int i = oldSubReg.length-1; i > addPosition; i--)
          oldSubReg[i] = oldSubReg[i-1];
        newSubReg = oldSubReg;
      }

      newSubReg[addPosition] = element;
      elementHolder = newSubReg;
      return 1;
    }

    /** Method adds new element to the container in sorted manner.
     * The method is called only if the container is empty or has 1 
     * element stored in.
     * @param element - element to be stored
     * @return - returns the change in the number of stored elements
     * (0 or 1) or returns -1 if element is not handled
     */
    private byte add12(Serializable element) {
      if (element == null)
        return 0;

      if (elementHolder == null) {
        elementHolder = element;
        return 1;
      }

      if (elementHolder instanceof Object[]){
        return -1;
      }
      if (element.equals(elementHolder)) {
        return 0; // ignore the new one and retain the old one
      }
      Object[] newSubReg;
      if (element instanceof Comparable) {
        newSubReg = new Comparable[1 + subRegIncrement];
        if (nsc.compare((Comparable) elementHolder,
                (Comparable) element) < 0) {
          newSubReg[0] = elementHolder;
          newSubReg[1] = element;
        }
        else {
          newSubReg[0] = element;
          newSubReg[1] = elementHolder;
        }
      }
      else {
        newSubReg = new Object[1 + subRegIncrement];
        newSubReg[0] = elementHolder;
        newSubReg[1] = element;
      }
      elementHolder = newSubReg;
      return 1;

    }

    /** A shortcut method which calculates the storage index in given
     * container. The index is calculated based on the hash-code of
     * the current HashElement.
     * @param container - the container where the HashElement will be stored
     * @return - the value of the index
     */
    public int index(Object[] container) {
      return mainIx(subRegHash, container);
    }

    /** Checks if given element is already stored in the container.
     * @param element - the element to be checked.
     * @return <b>true</b> - if found
     */
    public boolean contains(Object element) {
      Object[] elements = getElements();
      if (element == null)
        return (elements == null);
      else if (element instanceof Comparable)
        return Arrays.binarySearch( (Comparable[])elements,
                (Comparable) element, nsc) >= 0;
                else
                  return Arrays.asList(elements).contains(element);
    }

    /** Gets the stored instance that equals the given instance.
     * @param element - the element to be checked.
     * @return the equal stored instance
     */
    public Object get(Object element) {
      Object[] elements = getElements();
      int i = -1;
      if (element != null) {
        if (element instanceof Comparable)
          i = Arrays.binarySearch( (Comparable[])elements,
                  (Comparable) element, nsc);
        else
          i = Arrays.asList(elements).indexOf(element);
      }
      return ((i >= 0)? elements[i]: null);
    }

    /** Returns the stored elements as an object array
     * @return array with all stored elements
     */
    public Object[] getElements() {
      if (elementHolder == null)
        return null;
      else if (elementHolder instanceof Object[]) {
        Object[] eList = (Object[]) elementHolder;
        int i = eList.length;
        while (i>0 && eList[i-1] == null)
          i--;
        if (eList[0] instanceof Comparable)
          return Arrays.asList(
                  eList).subList(0, i).toArray(new Comparable[0]);
        else
          return Arrays.asList(
                  eList).subList(0, i).toArray();
      }
      else {  // getPoint.elementHolder contains single object
        if (elementHolder instanceof Comparable)
          return new Comparable[]{(Comparable) elementHolder};
        else
          return new Object[]{elementHolder};
      }
    }

  }

  /** Comparator for sorting <code>HashElement</code> instances. */
  public static class HashElementComp implements Comparator<HashElement> {
    Object[] container;
    public HashElementComp(Object[] container) {
      this.container = container;
    }

    public int compare(HashElement o1, HashElement o2) {
      int k1 = o1.index(container);
      int k2 = o2.index(container);
      if (k1 < k2)  return -1;
      if (k1 > k2)  return 1;
      return 0;
    }
  }      

  /** The main storage structure of the Hash Register. The index under
   * which an object will be stored is calculated through the method
   * <code>mainIx</code>. Calculation is based on the externally
   * provided hash-code of the object and the current size of the array */
  protected Object[] mainReg;
  /** Access synchronization locking object */
  protected transient Object mainRegLock = new Object();
  /** Number of different hash-code values provided with stored objects */
  protected int hashesCount = 0;
  /** Current limit of the number of distinct hash-code values that 
   * could be stored with the current size of <code>mainReg</code> */
  protected int hashesCountLimit = 0;
  /** The number of objects stored in the register */
  protected int elementsCount = 0;
  /** This flag determines if the auto-resizing is turned on */
  protected boolean autoResize = true;

  /** This public constructor starts the hash register with predefined size
   * and automatic resizing turned on */
  public HashRegister() {
    this(initialSize, true);
  }
  /** This protected constructor allows starting the hash register with
   * different than default size and automatic resizing option.
   * @param newSize - the initial <code>mainReg</code> size.
   * @param autoRes - the auto-resize flag.
   */
  protected HashRegister(int newSize, boolean autoRes) {
    mainReg = new Object[newSize];
    hashesCountLimit = mainReg.length * maxFillPerc / 100;
    autoResize = autoRes;
  }

  /** The method resizes the <code>mainReg</code> array if the content
   * volume limit was reached.
   * @return <b>true</b> if resize was performed
   */
  public boolean resizeMainReg() {
    // If limit is not reached -> quit resize
    if (hashesCountLimit > hashesCount)  return false;

    long start = System.currentTimeMillis();

    Object[] newMainReg;
    try {
      newMainReg = new Object[mainReg.length*2];
    } catch (Exception e) {
      KimLogs.logNERC_GAZETTEER.warn("Unsuccessful attempt to resize" +
              " [HashRegister] due to:\n" + e.getMessage());
      return false;
    }

    synchronized (mainRegLock) {
      // Cycle through the old register and move the [HashElement] objects
      // to the new main-register
      for (int main_i=0; main_i < mainReg.length; main_i++) {
        int new_main_i = main_i + mainReg.length;

        if (mainReg[main_i] == null) {
          // If there was no sub-register:
          // Do nothing
        }
        else if (mainReg[main_i] instanceof HashElement) {
          // If there was just one [HashElement] for sub-register:
          // Determine it's new place
          newMainReg[((HashElement)mainReg[main_i]).index(newMainReg)] =
            mainReg[main_i];
        }
        else {
          // If there were two or more [HashElement] as sub-register:
          // Try to split the sub-register in two parts.
          HashElement[] oldSubReg = (HashElement[]) mainReg[main_i];

          // The comparator [HashElementComp] allows [HashElement]
          // instances to be sorted with respect to the value of their
          // next main-register index (after the expansion).
          Arrays.sort(oldSubReg, new HashElementComp(newMainReg));

          // Scan where is the splitting point between the elements
          // that retain their old main-register index and those
          // that will move to a new main-register index
          int j1 = 0;
          while (j1 < oldSubReg.length
                  && oldSubReg[j1].index(newMainReg) == main_i)
            j1++;

          // Handle the elements retaining the old main-register index
          if (j1 == 0) {
          }
          else if (j1 == 1) {
            newMainReg[main_i] = oldSubReg[0];
          }
          else if (j1 < oldSubReg.length) {
            HashElement[] newSubReg = new HashElement[j1];
            for (int k=0; k<j1; k++)
              newSubReg[k] = oldSubReg[k];
            newMainReg[main_i] = newSubReg;
          }
          else {
            newMainReg[main_i] = oldSubReg;
          }

          // Handle the elements moving to the new main-register index
          int j2 = oldSubReg.length - j1;
          if (j2 == 0) {
          }
          else if (j2 == 1) {
            newMainReg[new_main_i] = oldSubReg[oldSubReg.length - 1];
          }
          else if (j2 < oldSubReg.length) {
            HashElement[] newSubReg = new HashElement[j2];
            for (int k=0; k<j2; k++)
              newSubReg[k] = oldSubReg[j1 + k];
            newMainReg[new_main_i] = newSubReg;
          }
          else {
            newMainReg[new_main_i] = oldSubReg;
          }
        }

        // Release the old sub-register to allow for early GC.
        mainReg[main_i] = null;
      }

      // Release the old main-register to allow for GC.
      mainReg = newMainReg;
      hashesCountLimit = mainReg.length * maxFillPerc / 100;
    }
    long duration = System.currentTimeMillis() - start;
    KimLogs.logNERC_GAZETTEER.debug("Main Register resized to " +
            mainReg.length + " for " + duration + "ms.");
    return true;
  }

  /** Adds a new element to the hash register.
   * @param hashValue - the hash-code value related with the stored element
   * @param element - the stored element
   */
  public void add(int hashValue, Serializable element) {
    elementsCount += getHashElement(hashValue, true).add(element);
    if (autoResize)  resizeMainReg();
  }

  /** Retrieves an array of stored elements corresponding to a hash-code
   * @param hashValue - the hash-code value
   * @return the related elements (<b>null</b> if none found)
   */
  public Object[] get(int hashValue) {
    HashElement getPoint = getHashElement(hashValue, false);
    if (getPoint != null)
      return getPoint.getElements();
    else
      return null;
  }
  /** Retrieves an element instance that is relevant to the given
   * hash-code and is equal to the given element.
   * @param hashValue - the hash-code value
   * @param element - the element for comparison
   * @return the corresponding stored element
   */
  public Object get(int hashValue, Object element) {
    HashElement getPoint = getHashElement(hashValue, false);
    if ( getPoint != null )
      return getPoint.get(element);
    return null;
  }

  /** Checks if there are any stored elements related to the given
   * hash-code value.
   * @param hashValue - the hash-code value
   * @return <b>true</b> if an <code>HashElement</code> instance is found
   * which has the same as given hash-code value. No check for existence
   * of stored elements is done, so the test is positive even is null element
   * with such hash-code has been added.
   */
  public boolean exists(int hashValue) {
    return (!(getHashElement(hashValue, false) == null));
  }

  /** Checks if there is a stored element related to the given
   * hash-code value and equal to the given element.
   * @param hashValue - the hash-code value
   * @param element - the element for comparison
   * @return <b>true</b> if matching element is found.
   */
  public boolean exists(int hashValue, Object element) {
    HashElement getPoint = getHashElement(hashValue, false);
    if ( getPoint != null )
      return getPoint.contains(element);
    return false;
  }

  /** Retrieves the number of stored elements.
   * @return the elements count
   */
  public int getElementsCount() {
    return elementsCount;
  }

  /** This method searches a <code>HashElement</code> corresponding to the
   * given hash-code value. Depending on the flag <code>createIfMissing</code>
   * if no such hash element is found it can be created.
   * @param hashValue - the hash-code value
   * @param createIfMissing - flag to force creation if not found.
   * @return the found/created <code>HashElement</code>
   */
  private HashElement getHashElement(
          int hashValue, boolean createIfMissing) {
    HashElement getPoint;

    synchronized (mainRegLock) {

      int main_i = mainIx(hashValue, mainReg);
      Object subReg = mainReg[main_i];

      if (subReg == null) {
        if (createIfMissing) {
          getPoint = new HashElement(hashValue);
          mainReg[main_i] = getPoint;
          hashesCount++;
        }
        else
          getPoint = null;
      }
      else if (subReg instanceof HashElement) {
        getPoint = (HashElement) subReg;
        if (getPoint.subRegHash != hashValue)
          if (createIfMissing) {
            HashElement[] newSubReg = new HashElement[2];
            newSubReg[0] = getPoint;
            getPoint = new HashElement(hashValue);
            newSubReg[1] = getPoint; 
            mainReg[main_i] = newSubReg;
            hashesCount++;
          }
          else {
            getPoint = null;
          }
      }
      else { // subReg is instance of HashElement[]
        HashElement[] oldSubReg = (HashElement[]) subReg;
        getPoint = null;
        for (int i=0; i < oldSubReg.length; i++) {
          if (oldSubReg[i].subRegHash == hashValue) {
            getPoint = oldSubReg[i];
            break;
          }
        }
        if (createIfMissing && getPoint == null) {
          HashElement[] newSubReg = new HashElement[oldSubReg.length+1];
          for (int i=0; i<oldSubReg.length; i++)
            newSubReg[i] = oldSubReg[i];
          getPoint = new HashElement(hashValue);
          newSubReg[newSubReg.length-1] = getPoint;
          mainReg[main_i] = newSubReg;
          hashesCount++;
        }
      }
    }

    return getPoint;
  }


  //======================================================
  // An interface and a method for bulk operation over the
  // whole content of the hash-register by external logic
  //======================================================
  /** This is a listener interface which provides means to perform bulk
   * operations over all of the stored elements in the register. This
   * interface must be used together with the method
   * <code>processContent</code>. */
  public interface ContentProcessor {
    public void process(Object[] elements);	    
  }

  /** This method accepts an implementation of the
   * <code>ContentProcessor</code> interface. Then it cycles all
   * of the stored elements in the register and passes them to the
   * <code>process</code> method of the <code>ContentProcessor</code>
   * implementation.
   * @param cProc - <code>ContentProcessor</code> implementation;
   */
  public void processContent(ContentProcessor cProc) {
    for (int main_i=0; main_i<mainReg.length; main_i++) {
      if (mainReg[main_i] == null)  continue;
      if (mainReg[main_i] instanceof HashElement)
        cProc.process(((HashElement)mainReg[main_i]).getElements());
      else {
        HashElement[] subReg = (HashElement[]) mainReg[main_i];
        for (int i=0; i<subReg.length; i++)
          cProc.process(subReg[i].getElements());
      }
    }
  }

  //==================================================
  // Content Inspection methods for profiling purposes
  //==================================================
  public void printContent(int mainRegLim, int subRegLim) {
    int repLen = Math.min(mainReg.length, mainRegLim);
    for (int i=0; i<mainReg.length; i++) {
      if ( mainReg[i] == null)
        ;
      else if (mainReg[i] instanceof HashElement) {
        System.out.println("<"+i+">"+getHEContent((HashElement) mainReg[i]));
        repLen--;
      }
      else {
        int subRepLen = Math.min(((HashElement[])mainReg[i]).length, subRegLim);
        System.out.print("<"+i+">");
        for (int j=0; j<subRepLen; j++)
          System.out.print(getHEContent(((HashElement[])mainReg[i])[j])+",");
        System.out.println();
        repLen--;
      }
      if (repLen <= 0)  break;
    }
  }
  private String getHEContent(HashElement he) {
    String cont = he.subRegHash + "[";
    Object[] eList = he.getElements();
    for (int i=0; i<eList.length; i++)
      cont += eList[i].toString() + "|";
    cont += "]";
    return cont;
  }

  public void printDistribution() {
    int[] bucketSize = new int[11];

    for (int i=0; i<bucketSize.length; i++)  bucketSize[i] = 0;

    for (int i=0; i<mainReg.length; i++) {
      if ( mainReg[i] == null)
        bucketSize[0]++;
      else if (mainReg[i] instanceof HashElement)
        bucketSize[1]++;
      else {
        int size = ((HashElement[])mainReg[i]).length;
        if (size >= bucketSize.length) size = bucketSize.length-1;
        bucketSize[size]++;
      }
    }
    System.out.println("Distribution of buckets by sizes for HashRegister with" +
            "MRSize="+mainReg.length+" Elements="+hashesCount);
    System.out.println("ForSize BucketCount");
    for (int i=0; i<bucketSize.length; i++)
      System.out.println(i + "    " + bucketSize[i]);
  }

  //==================================================
  // Serialization / Deserialization handling 
  //==================================================
  private void readObject(java.io.ObjectInputStream in)
  throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    // Recreate the lock object of the instance
    mainRegLock = new Object();
  }

  /** Method which calculates the storage index in given container.
   * The index is calculated based on a hash-code value.
   * @param hashValue - the hash-code value
   * @param container - the container where the HashElement will be stored
   * @return - the value of the index
   */
  private static int mainIx(int hashValue, Object[] container) {
    return Math.abs(hashValue % container.length);
  }

}
