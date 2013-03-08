package com.lyeeedar.Roguelike3D;

import java.io.Serializable;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

/**
 *   Collection type a bit like ArrayList but does not preserve the order
 *   of its entities, speedwise it is very good, especially suited for games.
 */

public class Bag<E> implements Serializable, Iterable<E> {
	
	private static final long serialVersionUID = 9141471894142964687L;
	
	private Object[] data;
	private int size = 0;

	/**
	 * Constructs an empty Bag with an initial capacity of ten.
	 *
	 */
	public Bag() {
		this(10);
	}

	/**
	 * Constructs an empty Bag with the specified initial capacity.
	 *
	 * @param capacity the initial capacity of Bag
	 */
	public Bag(int capacity) {
		data = new Object[capacity];
	}

	/**
	 * Removes the element at the specified position in this Bag.
	 * does this by overwriting it with the last element then removing 
	 * last element
	 * 
	 * @param index the index of element to be removed
	 * @return element that was removed from the Bag
	 */
	@SuppressWarnings("unchecked")
	public E remove(int index) {
		E o = (E) data[index]; // make copy of element to remove so it can be returned
		data[index] = data[--size]; // overwrite item to remove with last element
		data[size] = null; // null last element, so gc can do its work
		return o;
	}

	/**
	 * Removes the first occurrence of the specified element from this Bag,
	 * if it is present.  If the Bag does not contain the element, it is
	 * unchanged. does this by overwriting it was last element then removing 
	 * last element
	 * 
	 * @param o element to be removed from this list, if present
	 * @return <tt>true</tt> if this list contained the specified element
	 */
	public boolean remove(E o) {
		for (int i = 0; i < size; i++) {
			if (o == data[i]) {
				data[i] = data[--size]; // overwrite item to remove with last element
				data[size] = null; // null last element, so gc can do its work
				return true;
			}
		}

		return false;
	}

	/**
	 * Removes from this Bag all of its elements that are contained in the
	 * specified Bag.
	 *
	 * @param bag Bag containing elements to be removed from this Bag
	 * @return {@code true} if this Bag changed as a result of the call
	 */
	public boolean removeAll(Bag<E> bag) {
		boolean modified = false;

		for (int i = 0; i < bag.size(); i++) {
			Object o1 = bag.get(i);

			for (int j = 0; j < size; j++) {
				Object o2 = data[j];

				if (o1 == o2) {
					remove(j);
					j--;
					modified = true;
					break;
				}
			}
		}

		return modified;
	}

	/**
	 * Returns the element at the specified position in Bag.
	 *
	 * @param  index index of the element to return
	 * @return the element at the specified position in bag
	 */
	@SuppressWarnings("unchecked")
	public E get(int index) {
		return (E) data[index];
	}

	/**
	 * Returns the number of elements in this bag.
	 *
	 * @return the number of elements in this bag
	 */
	public int size() {
		return size;
	}

	/**
	 * Returns true if this list contains no elements.
	 *
	 * @return true if this list contains no elements
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Adds the specified element to the end of this bag.
	 * if needed also increases the capacity of the bag.
	 *
	 * @param o element to be added to this list
	 */
	public void add(E o) {   
		// if size greater than data capacity increase capacity
		if(size == data.length) {
			grow();
		}

		data[size++] = o;
	}

	private void grow() {
		Object[] oldData = data;
		int newCapacity = (oldData.length * 3) / 2 + 1;
		data = new Object[newCapacity];
		System.arraycopy(oldData, 0, data, 0, oldData.length);
	}

	/**
	 * Removes all of the elements from this bag. The bag will
	 * be empty after this call returns.
	 */
	public void clear() {
		// null all elements so gc can clean up
		for (int i = 0; i < size; i++) {
			data[i] = null;
		}

		size = 0;
	}

	@Override
	public Iterator<E> iterator()
	{
		return new BagIterator();
	}
	
	class BagIterator implements Iterator<E>
	{
		int pos = 0;
		int size = Bag.this.size;

		@Override
		public boolean hasNext() {
			return (pos < size);
		}

		@SuppressWarnings("unchecked")
		@Override
		public E next() {
			return (E) data[pos++];
		}

		@Override
		public void remove() {
			Bag.this.remove(--pos);
			size--;
		}
	}
	
	  /**
     * Save the state of the <tt>ArrayList</tt> instance to a stream (that
     * is, serialize it).
     *
     * @serialData The length of the array backing the <tt>ArrayList</tt>
     *             instance is emitted (int), followed by all of its elements
     *             (each an <tt>Object</tt>) in the proper order.
     */
    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException{
        // Write out element count, and any hidden stuff
        s.defaultWriteObject();

        // Write out array length
        s.writeInt(data.length);

        // Write out all elements in the proper order.
        for (int i=0; i<size; i++)
            s.writeObject(data[i]);
    }

    /**
     * Reconstitute the <tt>ArrayList</tt> instance from a stream (that is,
     * deserialize it).
     */
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {
        // Read in size, and any hidden stuff
        s.defaultReadObject();

        // Read in array length and allocate array
        int arrayLength = s.readInt();
        Object[] a = data = new Object[arrayLength];

        // Read in all elements in the proper order.
        for (int i=0; i<size; i++)
            a[i] = s.readObject();
    }
}